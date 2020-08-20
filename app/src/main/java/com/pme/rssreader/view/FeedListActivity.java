package com.pme.rssreader.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.view.adapter.FeedListAdapter;
import com.pme.rssreader.view.item.list.ItemListActivity;

public class FeedListActivity extends AppCompatActivity {

    private FeedListViewModel feedListViewModel;
    private TextView placeholderText;

    public static final String LOG_TAG_NETWORK = "Network";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        placeholderText = findViewById(R.id.text_placeholder);

        // Init ViewModel
        this.feedListViewModel =  new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(FeedListViewModel.class);

        // Init RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_view_feed);
        final FeedListAdapter adapter = new FeedListAdapter(this, this.feedListViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (adapter.getItemCount() == 0) {
            placeholderText.setVisibility(View.INVISIBLE);
        }

//        FeedRepository.getRepository(getApplication()).refreshFeeds();

//        this.feedListViewModel.

//        final FeedListAdapter adapter = new FeedListAdapter(new FeedListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(FeedWithItems feed) {
//                Toast.makeText(getApplicationContext(), feed.getFeed().getLink(), Toast.LENGTH_LONG).show();
//
//                Log.i("ANAN", "FOR START");
//                for (Item i : feed.getItems()) {
//                    Log.i("ANAN", i.getTitle());
//                }
//                Log.i("FEED ID", String.valueOf(feed.getFeed().getFeedId()));
//                Log.i("ANAN", "FOR END");
//
//                // NETWORK TEST
//                NetworkApi api = NetworkController.getApi();
//                api.getFeed(feed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
//                    @Override
//                    public void onResponse(Call<XmlFeed> call, Response<XmlFeed> response) {
//                        if (response.isSuccessful()) {
//                            Log.i(LOG_TAG_NETWORK, "Network call: success.");
////                            Log.d("FEED:", response.body().channel.item.get(0).toString());
//                            Log.d("FEED", String.valueOf(feed.getFeed().getFeedId()));
//
//                            //
//                            List<Item> newItems = new ArrayList<>();
//                            for (XmlItem xmlItem : response.body().channel.item) {
//                                newItems.add(xmlItem.toItem());
//                            }
//                            FeedRepository feedRepository = new FeedRepository(getApplication());
//                            feedRepository.insertItemsForFeed(feed.getFeed(), newItems);
//                            //
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<XmlFeed> call, Throwable t) {
//                        Log.i(LOG_TAG_NETWORK, "Network call: error.");
//                        Log.i(LOG_TAG_NETWORK, t.getMessage());
//                    }
//                });
//
//                // GO TO ITEM LIST
//                if (feed.getItems().size() > 0) {
//                    Intent i = new Intent(FeedListActivity.this, ItemListActivity.class);
//                    i.putExtra("SELECTED_FEED", feed);
//
//                    startActivity(i);
//                }
//            }
//        });


//        Log.i("TEST", feedRepository.getAllFeeds().getValue().get(0).getItems().get(0).getTitle());


        this.feedListViewModel.getAllFeedsObservable().observe(this, adapter::setFeeds);
        this.feedListViewModel.getItemSelectedEventObservable().observe(this, feedId -> {
            Log.w("SELECTED_FEED_ID", String.valueOf(feedId));
            FeedRepository.getRepository(getApplication()).refreshFeeds(); // FIXME: TEMP
            Intent i = new Intent(this, ItemListActivity.class);
            i.putExtra("SELECTED_FEED_ID", feedId);
            startActivity(i);
        });


    }
}
