package com.pme.rssreader.view.itemview;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.util.SingleLiveEvent;

import java.util.List;

public class ItemViewViewModel extends AndroidViewModel {
    private LiveData<List<Item>> allItems;
    private SingleLiveEvent<Item> itemSelectedEvent;

    public ItemViewViewModel(Application application, ItemViewViewModel itemViewViewModel, int feedId) {
        super(application);
        FeedRepository feedRepository = FeedRepository.getRepository(application);
        allItems = feedRepository.getFeedItems(feedId);
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

}
