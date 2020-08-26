package org.pme.rssreader.storage.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/**
 * Feed model for the database.
 */
@Entity(indices = {
    @Index(value = { "link", "name" }, unique = true)
})
public class Feed {

    @PrimaryKey(autoGenerate = true)
    private int feedId;

    private String name;

    private String link;

    private long created;

    public Feed(@NonNull String name, @NonNull String link) {
        this.name = name;
        this.link = link;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
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

}
