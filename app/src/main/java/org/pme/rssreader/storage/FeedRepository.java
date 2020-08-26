package org.pme.rssreader.storage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import org.pme.rssreader.network.NetworkApi;
import org.pme.rssreader.network.NetworkController;
import org.pme.rssreader.network.model.XmlFeed;
import org.pme.rssreader.network.model.XmlItem;
import org.pme.rssreader.storage.model.Feed;
import org.pme.rssreader.storage.model.FeedWithItems;
import org.pme.rssreader.storage.model.Item;
import org.pme.rssreader.sync.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class for feeds.
 */
public class FeedRepository {

    public static final String LOG_TAG_NETWORK = "Network";
    public static final String LOG_TAG_DATABASE = "Database";

    private FeedDao feedDao;
    private LiveData<List<FeedWithItems>> allFeedsObservable;

    private static FeedRepository INSTANCE;

    private Context context;

    public static FeedRepository getRepository(Context context) {
        if (INSTANCE == null) {
            synchronized (FeedRepository.class) {
                INSTANCE = new FeedRepository(context);
            }
        }
        return INSTANCE;
    }

    private FeedRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.feedDao = db.feedDao();
        this.allFeedsObservable = this.feedDao.getFeedsObservable();
        // Store application context for NotificationUtils
        this.context = context.getApplicationContext();
    }

    public LiveData<List<FeedWithItems>> getAllFeedsObservable() {
        return allFeedsObservable;
    }

    public LiveData<List<Item>> getFeedItemsObservable(int feedId) {
        return feedDao.getFeedItemsObservable(feedId);
    }

    public void insert(Feed feed) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            feed.setCreated(System.currentTimeMillis());
            feedDao.insert(feed);
        });
    }

    public void deleteFeed(Feed feed) {
        AppDatabase.databaseThreadExecutor.execute(() -> feedDao.delete(feed));
    }

    /**
     * Used by background alarms to refresh feeds when app is not running.
     * Triggers notification on new items.
     */
    public void refreshFeedsInBackground() {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            // Early return when no feeds available
            if (this.feedDao.getFeeds().size() == 0) {
                return;
            }

            NetworkApi api = NetworkController.getApi();

            for (FeedWithItems currentFeed : this.feedDao.getFeeds()) {
                api.getFeed(currentFeed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
                    @Override
                    public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                        if (response.isSuccessful()) {
                            List<Item> newItems = new ArrayList<>();

                            if (response.body() != null) {
                                for (XmlItem xmlItem : response.body().channel.item) {
                                    List<Item> currentFeedItems = currentFeed.getItems();
                                    Item currentItem = xmlItem.toItem();

                                    // Search item by guid
                                    Optional<Item> foundItem = currentFeedItems
                                            .stream().parallel()
                                            .filter(item -> item.getGuid().equals(currentItem.getGuid()))
                                            .findAny();

                                    if (!foundItem.isPresent()) {
                                        // Item was new, add to list
                                        currentItem.setFeedName(currentFeed.getFeed().getName());
                                        currentItem.setFeedId(currentFeed.getFeed().getFeedId());
                                        newItems.add(currentItem);
                                    }
                                }
                            }

                            // If any new items on current feed
                            if (newItems.size() > 0) {
                                // Insert items to DB
                                insertItemsForFeed(currentFeed.getFeed(), newItems);
                                // Notify the user maybe
                                NotificationUtils.createNotificationForNewItems(context, newItems);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                        Log.e(LOG_TAG_NETWORK, String.format("Error on URL: %s", currentFeed.getFeed().getLink()));
                    }
                });
            }
        });
    }

    /**
     * Refreshes all feeds, can be called on main thread, since it makes use of LiveData
     */
    public void refreshAllFeeds() {
        NetworkApi api = NetworkController.getApi();
        // Early return if no feeds available
        if (this.allFeedsObservable.getValue() == null) {
            return;
        }

        this.allFeedsObservable.observe((LifecycleOwner) context, feeds -> {
            for (FeedWithItems feed : this.allFeedsObservable.getValue()) {
                api.getFeed(feed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
                    @Override
                    public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                        Log.i(LOG_TAG_NETWORK, String.format("Request successful on URL: %s", feed.getFeed().getLink()));
                        if (response.isSuccessful()) {
                            List<Item> newItems = new ArrayList<>();
                            if (response.body() != null) {
                                for (XmlItem xmlItem : response.body().channel.item) {
                                    newItems.add(xmlItem.toItem());
                                }
                            }
                            insertItemsForFeed(feed.getFeed(), newItems);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                        Log.e(LOG_TAG_NETWORK, String.format("Error on URL: %s", feed.getFeed().getLink()));
                    }
                });
            }
        });
    }


    public interface RefreshFeedCallback {
        void onSuccess();
        void onFailure();
    }

    public void refreshFeed(int feedId, RefreshFeedCallback refreshFeedCallback) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            Feed feed = feedDao.getFeedById(feedId);
            if (feed == null) {
                refreshFeedCallback.onFailure();
                return;
            }
            NetworkApi api = NetworkController.getApi();
            api.getFeed(feed.getLink()).enqueue(new Callback<XmlFeed>() {
                @Override
                public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                    if (response.isSuccessful()) {
                        Log.i(LOG_TAG_NETWORK, String.format("Network call success on URL: %s", feed.getLink()));
                        List<Item> newItems = new ArrayList<>();
                        if (response.body() != null) {
                            for (XmlItem xmlItem : response.body().channel.item) {
                                newItems.add(xmlItem.toItem());
                            }
                        }
                        insertItemsForFeed(feed, newItems);
                        refreshFeedCallback.onSuccess();
                    } else {
                        refreshFeedCallback.onFailure();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                    Log.e(LOG_TAG_NETWORK, String.format("Error on URL: %s", feed.getLink()));
                    refreshFeedCallback.onFailure();
                }
            });
        });
    }

    public void refreshFeed(int feedId) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            Feed feed = feedDao.getFeedById(feedId);
            if (feed == null) {
                return;
            }
            NetworkApi api = NetworkController.getApi();
            api.getFeed(feed.getLink()).enqueue(new Callback<XmlFeed>() {
                @Override
                public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                    if (response.isSuccessful()) {
                        Log.i(LOG_TAG_NETWORK, String.format("Network call success on URL: %s", feed.getLink()));
                        List<Item> newItems = new ArrayList<>();
                        if (response.body() != null) {
                            for (XmlItem xmlItem : response.body().channel.item) {
                                newItems.add(xmlItem.toItem());
                            }
                        }
                        insertItemsForFeed(feed, newItems);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                    Log.e(LOG_TAG_NETWORK, String.format("Error on URL: %s", feed.getLink()));
                }
            });
        });
    }

    public void insertItemsForFeed(Feed feed, List<Item> items) {
        if (items.size() == 0) { return; }
        AppDatabase.databaseThreadExecutor.execute(() -> feedDao.insertItemsForFeed(feed, items));
    }

}
