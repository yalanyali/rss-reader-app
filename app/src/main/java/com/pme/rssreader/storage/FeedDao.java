package com.pme.rssreader.storage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.storage.model.Item;

import java.util.List;

@Dao
public abstract class FeedDao {

    @Insert
    public abstract void insert(Feed feed);

    @Query("DELETE FROM Feed")
    public abstract void deleteAll();

    @Query("SELECT * FROM Feed")
    public abstract LiveData<List<FeedWithItems>> getFeeds();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void _insertAll(List<Item> items); // ItemDao?

    void insertItemsForFeed(Feed feed, List<Item> items) {
        // Set feedId for items for FeedWithItems relation
        for (Item item : items) {
            Log.i("insertItemsForFeed", "FEED ID: " + feed.getFeedId());
            item.setFeedId(feed.getFeedId());
        }
        _insertAll(items);
    }
}
