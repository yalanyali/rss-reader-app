package com.pme.rssreader.storage;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.pme.rssreader.network.NetworkApi;
import com.pme.rssreader.network.NetworkController;
import com.pme.rssreader.network.model.XmlFeed;
import com.pme.rssreader.network.model.XmlItem;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.sync.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Log.w("REPO INSTANCE:", "");
            }
        }
        return INSTANCE;
    }

    private FeedRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.context = context;
        this.feedDao = db.feedDao();
        this.allFeedsObservable = this.feedDao.getFeedsObservable();
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
            Log.i("RoomDB", "Saving: " + feed.toString());
            feedDao.insert(feed);
        });
    }

    public void deleteFeed(Feed feed) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            Log.i("RoomDB", "Deleting: " + feed.toString());
            feedDao.delete(feed);
        });
    }

    // NOT TO BE USED IN MAIN THREAD
    public void refreshFeedsInBackground() {
        Log.e("refreshFeedsInBackground", "CALLED");
        if (this.feedDao.getFeeds().size() == 0) {
            Log.e("refreshFeedsInBackground", "WAS NULL");
            return;
        }
        Log.e("refreshFeedsInBackground", "WAS NOT NULL");
        NetworkApi api = NetworkController.getApi();

        for (FeedWithItems currentFeed : this.feedDao.getFeeds()) {
            Log.w("NETWORK CALL:", currentFeed.getFeed().getLink());
            api.getFeed(currentFeed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
                @Override
                public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                    if (response.isSuccessful()) {
                        Log.w(LOG_TAG_NETWORK, "Network call: success.");
                        Log.w("FEED", String.valueOf(currentFeed.getFeed().getFeedId()));

                        List<Item> newItems = new ArrayList<>();

                        if (response.body() != null) {
                            for (XmlItem xmlItem : response.body().channel.item) {
                                List<Item> currentFeedItems = currentFeed.getItems();
                                Item currentItem = xmlItem.toItem();

                                Optional<Item> foundItem = currentFeedItems
                                        .stream().parallel()
                                        .filter(item -> item.getGuid().equals(currentItem.getGuid()))
                                        .findAny();

                                if (!foundItem.isPresent()) {
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
                            // Notify the user
                            NotificationUtils.createNotificationForNewItems(context, newItems);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                    Log.e(LOG_TAG_NETWORK, "Network call: error.");
                    Log.e(LOG_TAG_NETWORK, Objects.requireNonNull(t.getMessage()));
                }
            });
        }

    }

    // Can be called on main thread, since it makes use of LiveData
    public void refreshAllFeeds() {
        Log.e("refreshFeeds", "CALLED");
        NetworkApi api = NetworkController.getApi();
        if (this.allFeedsObservable.getValue() == null) {
            Log.e("refreshFeeds", "WAS NULL");
            return;
        }
        Log.e("refreshFeeds", "WAS NOT NULL");
        for (FeedWithItems feed : this.allFeedsObservable.getValue()) {
            Log.w("NETWORK CALL:", feed.getFeed().getLink());
            api.getFeed(feed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
                @Override
                public void onResponse(Call<XmlFeed> call, Response<XmlFeed> response) {
                    if (response.isSuccessful()) {
                        Log.w(LOG_TAG_NETWORK, "Network call: success.");
                        List<Item> newItems = new ArrayList<>();
                        for (XmlItem xmlItem : response.body().channel.item) {
                            newItems.add(xmlItem.toItem());
                        }
                        insertItemsForFeed(feed.getFeed(), newItems);
                    }
                }

                @Override
                public void onFailure(Call<XmlFeed> call, Throwable t) {
                    Log.e(LOG_TAG_NETWORK, "Network call: error.");
                    Log.e(LOG_TAG_NETWORK, t.getMessage());
                }
            });
        }

    }

    public void refreshFeed(int feedId) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            Feed feed = feedDao.getFeedById(feedId);
            if (feed != null) {
                NetworkApi api = NetworkController.getApi();
                api.getFeed(feed.getLink()).enqueue(new Callback<XmlFeed>() {
                    @Override
                    public void onResponse(Call<XmlFeed> call, Response<XmlFeed> response) {
                        if (response.isSuccessful()) {
                            Log.w(LOG_TAG_NETWORK, "Network call: success.");
                            List<Item> newItems = new ArrayList<>();
                            for (XmlItem xmlItem : response.body().channel.item) {
                                newItems.add(xmlItem.toItem());
                            }
                            insertItemsForFeed(feed, newItems);
                        }
                    }
                    @Override
                    public void onFailure(Call<XmlFeed> call, Throwable t) {
                        Log.e(LOG_TAG_NETWORK, "Network call: error.");
                        Log.e(LOG_TAG_NETWORK, t.getMessage());
                    }
                });
            }
        });
    }

//    public void refreshFeed(int feedId) {
//        NetworkApi api = NetworkController.getApi();
//
//        Feed feed = feedDao.getFeed(feedId);
//
//        api.getFeed(feed.getLink()).enqueue(new Callback<XmlFeed>() {
//            @Override
//            public void onResponse(Call<XmlFeed> call, Response<XmlFeed> response) {
//                if (response.isSuccessful()) {
//                    Log.i(LOG_TAG_NETWORK, "Network call: success.");
//                    Log.d("FEED", String.valueOf(feed.getFeedId()));
//
//                    List<Item> newItems = new ArrayList<>();
//                    for (XmlItem xmlItem : response.body().channel.item) {
//                        newItems.add(xmlItem.toItem());
//                    }
//                    insertItemsForFeed(feed, newItems);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XmlFeed> call, Throwable t) {
//                Log.i(LOG_TAG_NETWORK, "Network call: error.");
//                Log.i(LOG_TAG_NETWORK, t.getMessage());
//            }
//        });
//    }

    public void insertItemsForFeed(Feed feed, List<Item> items) {
        AppDatabase.databaseThreadExecutor.execute(() -> {
            feedDao.insertItemsForFeed(feed, items);
        });
    }

}
