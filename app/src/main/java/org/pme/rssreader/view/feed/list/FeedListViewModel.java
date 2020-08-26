package org.pme.rssreader.view.feed.list;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.pme.rssreader.storage.FeedRepository;
import org.pme.rssreader.storage.model.Feed;
import org.pme.rssreader.storage.model.FeedWithItems;
import org.pme.rssreader.util.SingleLiveEvent;

import java.util.List;

public class FeedListViewModel extends AndroidViewModel {

    private FeedRepository feedRepository;
    private LiveData<List<FeedWithItems>> allFeeds;

    private SingleLiveEvent<Feed> selectedFeedEvent;

    public FeedListViewModel(Application application) {
        super(application);
        this.feedRepository = FeedRepository.getRepository(application.getApplicationContext());
        this.allFeeds = this.feedRepository.getAllFeedsObservable();
        this.selectedFeedEvent = new SingleLiveEvent<>();
    }

    public LiveData<List<FeedWithItems>> getAllFeedsObservable() {
        return allFeeds;
    }

    public SingleLiveEvent<Feed> getItemSelectedEventObservable() {
        return selectedFeedEvent;
    }

    public void refreshFeeds() {
        this.feedRepository.refreshAllFeeds();
    }

    public void setItemSelected(Feed selectedFeed) {
        this.selectedFeedEvent.setValue(selectedFeed);
    }

}
