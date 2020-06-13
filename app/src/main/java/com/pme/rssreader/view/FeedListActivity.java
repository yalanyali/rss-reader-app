package com.pme.rssreader.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.network.NetworkApi;
import com.pme.rssreader.network.NetworkController;
import com.pme.rssreader.network.model.XmlFeed;
import com.pme.rssreader.network.model.XmlItem;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.adapter.FeedListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedListActivity extends AppCompatActivity {

    private FeedListViewModel feedListViewModel;

    public static final String LOG_TAG_NETWORK = "Network";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        // Init RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_view_feed);
        final FeedListAdapter adapter = new FeedListAdapter(new FeedListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FeedWithItems feed) {
                Toast.makeText(getApplicationContext(), feed.getFeed().getLink(), Toast.LENGTH_LONG).show();

                Log.i("ANAN", "FOR START");
                for (Item i : feed.getItems()) {
                    Log.i("ANAN", i.getTitle());
                }
                Log.i("ANAN", "FOR END");

                // NETWORK TEST
                NetworkApi api = NetworkController.getApi();
                api.getFeed(feed.getFeed().getLink()).enqueue(new Callback<XmlFeed>() {
                    @Override
                    public void onResponse(Call<XmlFeed> call, Response<XmlFeed> response) {
                        if (response.isSuccessful()) {
                            Log.i(LOG_TAG_NETWORK, "Network call: success.");
//                            Log.d("FEED:", response.body().channel.item.get(0).toString());
                            Log.d("FEED", String.valueOf(feed.getFeed().getFeedId()));

                            //
                            List<Item> newItems = new ArrayList<>();
                            for (XmlItem xmlItem : response.body().channel.item) {
                                newItems.add(xmlItem.toItem());
                            }
                            FeedRepository feedRepository = new FeedRepository(getApplication());
                            feedRepository.insertItemsForFeed(feed.getFeed(), newItems);
                            //
                        }
                    }

                    @Override
                    public void onFailure(Call<XmlFeed> call, Throwable t) {
                        Log.i(LOG_TAG_NETWORK, "Network call: error.");
                        Log.i(LOG_TAG_NETWORK, t.getMessage());
                    }
                });

                // GO TO ITEM LIST

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Init ViewModel
        this.feedListViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
            ).get(FeedListViewModel.class);

        // Observe
        this.feedListViewModel.getAllFeeds().observe(this,
                new Observer<List<FeedWithItems>>() {
                    @Override
                    public void onChanged(List<FeedWithItems> feeds) {
                        adapter.setFeeds(feeds);
                    }
                });
    }
}
