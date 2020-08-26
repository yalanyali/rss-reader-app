package org.pme.rssreader.storage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.pme.rssreader.storage.model.Feed;
import org.pme.rssreader.storage.model.FeedWithItems;
import org.pme.rssreader.storage.model.Item;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Dao
public abstract class FeedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Feed feed);

    @Query("DELETE FROM Feed")
    public abstract void deleteAll();

    @Delete
    public abstract void delete(Feed feed);

    @Transaction
    @Query("SELECT * FROM Feed")
    public abstract LiveData<List<FeedWithItems>> getFeedsObservable();

    @Transaction
    @Query("SELECT * FROM Feed")
    public abstract List<FeedWithItems> getFeeds();

    // Minimal feed, without items
    @Query("SELECT * FROM Feed WHERE feedId = :feedId")
    public abstract Feed getFeedById(int feedId);

    @Query("SELECT * FROM Item WHERE feedId = :feedId ORDER BY pubDate DESC")
    public abstract LiveData<List<Item>> getFeedItemsObservable(int feedId);

    @Query("SELECT * FROM Item WHERE feedId = :feedId ORDER BY pubDate DESC")
    public abstract List<Item> getFeedItems(int feedId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void _insertAll(List<Item> items);

    void insertItemsForFeed(Feed feed, List<Item> items) {
        // Set feedId for items for FeedWithItems relation
        for (Item item : items) {
            // Check if items have full links
            if (!item.getLink().contains("http")) {
                try {
                    // Get host part of the feed url to use as a prefix on item urls
                    URL feedUrl = new URL(feed.getLink());
                    item.setLink(String.format("%s://%s%s", feedUrl.getProtocol(), feedUrl.getHost(), item.getLink()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            if (item.getFeedId() == 0) {
                item.setFeedId(feed.getFeedId());
            }
        }
        _insertAll(items);
    }
}
