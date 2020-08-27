package org.pme.rssreader.view.item;

import android.app.Application;
import android.text.format.DateUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.pme.rssreader.storage.FeedRepository;
import org.pme.rssreader.storage.model.Item;
import org.pme.rssreader.util.SingleLiveEvent;

import java.util.Date;
import java.util.List;

/**
 * ViewModel for feed items, which gets shared by ItemListFragment and ItemDetailsFragment.
 */
public class ItemViewModel extends AndroidViewModel {

    private LiveData<List<Item>> allItems;
    private SingleLiveEvent<Item> itemSelectedEvent;

    private FeedRepository feedRepository;

    public ItemViewModel(Application application) {
        super(application);
        feedRepository = FeedRepository.getRepository(application);
        itemSelectedEvent = new SingleLiveEvent<>();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public SingleLiveEvent<Item> getItemSelectedEvent() {
        return itemSelectedEvent;
    }

    public void setItemSelected(Item selectedItem) {
        this.getItemSelectedEvent().setValue(selectedItem);
    }

    public void refreshFeed(int feedId, FeedRepository.RefreshFeedCallback refreshFeedCallback) {
        this.feedRepository.refreshFeed(feedId, refreshFeedCallback);
    }

    public void refreshAllFeeds() {
        this.feedRepository.refreshAllFeeds();
    }

    public String getRelativeDateString(Date date) {
        Date currentDate = new Date();
        //noinspection UnnecessaryLocalVariable
        String relativeTimeSpanString = (String) DateUtils.getRelativeTimeSpanString(date.getTime(), currentDate.getTime(), DateUtils.SECOND_IN_MILLIS);
        return relativeTimeSpanString;
    }

    /**
     * Initializes the items observable of selected feed.
     * @param feedId Selected feed id
     */
    public void setFeedId(int feedId) {
        allItems = feedRepository.getFeedItemsObservable(feedId);
    }

    public LiveData<List<Item>> getAllItemsOnEveryFeed() {
        return feedRepository.getAllItemsObservable();
    }

}
