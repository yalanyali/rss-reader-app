package com.pme.rssreader.storage.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {
    @Index(value = { "link", "name" }, unique = true)
})
public class Feed implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long feedId;

    @NonNull
    private String name;

    @NonNull
    private String link;

    @NonNull
    private long created;

    public Feed(@NonNull String name, @NonNull String link) {
        this.name = name;
        this.link = link;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public void setLink(@NonNull String link) {
        this.link = link;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Feed: " + name + " " + link;
    }

}
