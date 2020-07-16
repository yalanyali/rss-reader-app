package com.pme.rssreader.storage.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class FeedWithItems implements Serializable {
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
