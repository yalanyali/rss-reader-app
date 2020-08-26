package org.pme.rssreader.storage.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


/**
 * Relationship model for Room database.
 * Feed is a basic model but FeedWithItems has a list of items.
 */
public class FeedWithItems {

    @Embedded
    private Feed feed;

    @Relation(
        parentColumn = "feedId",
        entityColumn = "feedId"
    )
    private List<Item> items;

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
