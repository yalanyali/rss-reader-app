package com.pme.rssreader.view.feed.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.view.drawer.DrawerActivity;
import com.pme.rssreader.view.feed.list.adapter.FeedRecyclerViewAdapter;
import com.pme.rssreader.view.item.list.ItemListActivity;

/**
 * A fragment representing a list of feeds.
 */
public class FeedListFragment extends Fragment {

    public static FeedListFragment newInstance() {
        return new FeedListFragment();
    }

    private boolean placeholderActive = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_item_list);
        TextView placeholderText = view.findViewById(R.id.text_placeholder);

        FeedListViewModel feedListViewModel = new ViewModelProvider(this).get(FeedListViewModel.class);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        // Set the adapter
        final FeedRecyclerViewAdapter adapter = new FeedRecyclerViewAdapter(context, feedListViewModel);
        recyclerView.setAdapter(adapter);


        feedListViewModel.getAllFeedsObservable().observe(getViewLifecycleOwner(), feeds -> {
            if (feeds.size() > 0) {
                if (placeholderActive) {
                    placeholderActive = false;
                    placeholderText.setVisibility(View.GONE);
                }
            } else {
                // No feeds
                placeholderActive = true;
                placeholderText.setVisibility(View.VISIBLE);
            }
            adapter.setFeeds(feeds);
        });

        // For rerenders
        if (!placeholderActive) {
            placeholderText.setVisibility(View.GONE);
        }

        feedListViewModel.getItemSelectedEventObservable().observe(this, feedId -> {
            Log.w("SELECTED_FEED_ID", String.valueOf(feedId));
            Intent i = new Intent(requireContext(), ItemListActivity.class);
            i.putExtra(ItemListActivity.INTENT_EXTRA, feedId);
            startActivity(i);
        });

        // Swipe layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refreshFeeds();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireActivity(), "Feeds updated!",
                        Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

}