package com.pme.rssreader.storage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.storage.model.Item;

import java.util.List;

public class FeedRepository {
    private FeedDao feedDao;
    private LiveData<List<FeedWithItems>> allFeeds;

    public FeedRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.feedDao = db.feedDao();
        this.allFeeds = this.feedDao.getFeeds();
    }

    public LiveData<List<FeedWithItems>> getAllFeeds() {
        return allFeeds;
    }

    public void insert(Feed feed) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            feed.setCreated(System.currentTimeMillis());
            Log.i( "RoomDB", "Saving: " + feed.toString());
            feedDao.insert(feed);
        });
    }

    public void insertItemsForFeed(Feed feed, List<Item> items) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            feedDao.insertItemsForFeed(feed, items);
        });
    }
}
