package com.pme.rssreader.view;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.util.SingleLiveEvent;

import java.util.List;

public class FeedListViewModel extends AndroidViewModel {

    private FeedRepository feedRepository;
    private LiveData<List<FeedWithItems>> allFeeds;

    private SingleLiveEvent<Integer> itemSelectedEvent;

    public FeedListViewModel(Application application) {
        super(application);
        this.feedRepository = FeedRepository.getRepository(application.getApplicationContext());
        this.allFeeds = this.feedRepository.getAllFeedsObservable();
        this.itemSelectedEvent = new SingleLiveEvent<>();
    }

    public LiveData<List<FeedWithItems>> getAllFeedsObservable() {
        return allFeeds;
    }

//    public void insert(Feed feed) {
//        this.feedRepository.insert(feed);
//    }

    public SingleLiveEvent<Integer> getItemSelectedEventObservable() {
        return itemSelectedEvent;
    }

    public void setItemSelected(long feedId) {
        this.itemSelectedEvent.setValue((int) feedId);
    }
}
