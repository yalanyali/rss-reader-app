package com.pme.rssreader.storage.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(indices = {@Index(value = {"guid"}, unique = true)})
public class Item implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int feedId;

    @NonNull
    private String title;

    @NonNull
    private String link;

    private Date pubDate;

    private String content;

    private String description;

    private String guid;

    // NOT IN DATABASE, NOT POPULATED!
    // This is only for a quick lookup for notifications.
    // It gets populated manually on new items.
    @Ignore
    private String feedName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public void setLink(@NonNull String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Item: " + title + " " + link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * NOT IN DATABASE, NOT POPULATED!
     * This is only for a quick lookup for notifications.
     * It gets populated manually on new items.
     */
    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }
}
