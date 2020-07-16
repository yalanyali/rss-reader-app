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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Dao
public abstract class FeedDao {

    @Insert
    public abstract void insert(Feed feed);

    @Query("DELETE FROM Feed")
    public abstract void deleteAll();

    @Query("SELECT * FROM Feed")
    public abstract LiveData<List<FeedWithItems>> getFeeds();

    @Query("SELECT * FROM Item WHERE feedId = :feedId")
    public abstract LiveData<List<Item>> getFeedItems(int feedId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void _insertAll(List<Item> items); // External ItemDao?

    void insertItemsForFeed(Feed feed, List<Item> items) {
        // Set feedId for items for FeedWithItems relation
        for (Item item : items) {
            Log.i("insertItemsForFeed", "FEED ID: " + feed.getFeedId());
            try {
                // Get host part of the feed url to use as a prefix on item urls
                URL feedUrl = new URL(feed.getLink());
                item.setLink(String.format("%s://%s%s", feedUrl.getProtocol(), feedUrl.getHost(), item.getLink()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            item.setFeedId(feed.getFeedId());
        }
        _insertAll(items);
    }
}
