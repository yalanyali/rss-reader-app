package com.pme.rssreader.view.feed.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.view.feed.list.adapter.FeedRecyclerViewAdapter;
import com.pme.rssreader.view.item.list.ItemListActivity;

public class FeedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_fragment_layout);
//        FeedRepository.getRepository(getApplication()).refreshAllFeeds();
    }

}
