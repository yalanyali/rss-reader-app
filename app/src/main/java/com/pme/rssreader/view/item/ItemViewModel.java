package com.pme.rssreader.view.item;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.util.SingleLiveEvent;

import java.util.List;

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

    public Item getDefaultItem() {
        if (allItems.getValue() != null && allItems.getValue().size() > 0) {
            return allItems.getValue().get(0);
        } else {
            return null;
        }
    }

    public SingleLiveEvent<Item> getItemSelectedEvent() {
        return itemSelectedEvent;
    }

    public void setItemSelected(Item selectedItem) {
        this.getItemSelectedEvent().setValue(selectedItem);
    }

    public void refreshFeed(int feedId) {
        this.feedRepository.refreshFeed(feedId);
    }

    /**
     * Initializes the items observable.
     * @param feedId
     */
    public void setFeedId(int feedId) {
        allItems = feedRepository.getFeedItemsObservable(feedId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e("ItemViewModel", "onCleared");
    }
}