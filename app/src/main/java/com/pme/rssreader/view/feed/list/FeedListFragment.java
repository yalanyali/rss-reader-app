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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.view.feed.list.adapter.FeedRecyclerViewAdapter;
import com.pme.rssreader.view.item.list.ItemListActivity;
import com.pme.rssreader.view.item.list.ItemListFragment;

/**
 * A fragment representing a list of feeds.
 */
public class FeedListFragment extends Fragment implements FeedRecyclerViewAdapter.ItemDeleteCallback {

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
        RecyclerView recyclerView = view.findViewById(R.id.fragment_feed_list_recycler_view);
        TextView placeholderText = view.findViewById(R.id.text_placeholder);

        FeedListViewModel feedListViewModel = new ViewModelProvider(this).get(FeedListViewModel.class);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        // Set the adapter
        final FeedRecyclerViewAdapter adapter = new FeedRecyclerViewAdapter(context, feedListViewModel, this);
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
//            Intent i = new Intent(requireContext(), ItemListActivity.class);
//            i.putExtra(ItemListActivity.INTENT_EXTRA, feedId);
//            startActivity(i);
            Bundle bundle = new Bundle();
            bundle.putInt(ItemListFragment.INTENT_EXTRA, feedId);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_feed_list_to_containerFragment, bundle);
        });


        // Swipe to refresh
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedListViewModel.refreshFeeds();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireActivity(), "Feeds updated!",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Fab
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_feed_list_to_newFeedFragment);
        });

        return view;
    }

    /**
     * Implementation of the callback method from the adapter.
     * Adapter passes the feed to be deleted.
     */
    @Override
    public void itemDeleteRequest(Feed feed) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());

        alertDialogBuilder.setMessage("Do you really want to delete this feed?")
                .setTitle("Warning!");

        alertDialogBuilder.setPositiveButton("Yes",
                (dialogInterface, i) -> {
                    Log.e("DELETE", "DIYOR");
                    FeedRepository.getRepository(requireContext()).deleteFeed(feed);
                    Toast.makeText(requireActivity(), "Feed deleted!",
                            Toast.LENGTH_LONG).show();
                });
        alertDialogBuilder.setNegativeButton("No",
                (dialogInterface, i) -> {
                    Log.e("DELETE", "DEMIYOR");
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}