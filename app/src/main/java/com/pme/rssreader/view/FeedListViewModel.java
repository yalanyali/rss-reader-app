package com.pme.rssreader.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.FeedWithItems;

import java.util.List;

public class FeedListViewModel extends AndroidViewModel {

    private FeedRepository feedRepository;
    private LiveData<List<FeedWithItems>> allFeeds;

    public FeedListViewModel(Application application) {
        super(application);
        this.feedRepository = new FeedRepository(application);
        this.allFeeds = this.feedRepository.getAllFeeds();
    }

    public LiveData<List<FeedWithItems>> getAllFeeds() {
        return allFeeds;
    }

    public void insert(Feed feed) {
        this.feedRepository.insert(feed);
    }
}
