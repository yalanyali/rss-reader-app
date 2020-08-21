package com.pme.rssreader.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Item;

import java.util.List;

public class UpdateTask extends AsyncTask<Context, Void, Void> {
    @Override
    protected Void doInBackground(Context... contexts) {
        Log.e("ALARMA UpdateTask", "ISTEK GELDI");
        FeedRepository feedRepository = FeedRepository.getRepository(contexts[0]);
        feedRepository.refreshFeedsInBackground();
//        List<Item> newItems = feedRepository.refreshFeedsInBackground();
//        if (newItems != null) {
//            Log.e("doInBackground", String.valueOf(newItems.size()));
//        }
        return null;
    }
}
