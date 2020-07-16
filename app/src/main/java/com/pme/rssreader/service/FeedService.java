package com.pme.rssreader.service;

import android.app.Application;

import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.FeedWithItems;

import java.util.List;

public abstract class FeedService {

    private final String LOG_FEED_SERVICE = "FEED_SERVICE";

    private FeedService() {
        // no-op
    }

//    public static void refreshFeeds(Application application) {
//        FeedRepository feedRepository = new FeedRepository(application);
//        List<FeedWithItems> feeds = feedRepository.getAllFeeds();
//    }

}