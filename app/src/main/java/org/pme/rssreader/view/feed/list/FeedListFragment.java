package org.pme.rssreader.view.feed.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.pme.rssreader.R;
import org.pme.rssreader.storage.FeedRepository;
import org.pme.rssreader.storage.model.Feed;
import org.pme.rssreader.view.feed.list.adapter.FeedRecyclerViewAdapter;
import org.pme.rssreader.view.feed.newfeed.NewFeedFragment;
import org.pme.rssreader.view.item.list.ItemListFragment;

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

        feedListViewModel.getItemSelectedEventObservable().observe(this, feed -> {
            Bundle bundle = new Bundle();
            // Pass feed id
            bundle.putInt(ItemListFragment.EXTRA_FEED_ID, feed.getFeedId());
            // Pass feed title for custom actionbar title: Argument name at mobile_navigation.xml
            bundle.putString("FEED_TITLE", feed.getName());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_feed_list_to_containerFragment, bundle);
        });


        // Swipe to refresh
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            feedListViewModel.refreshFeeds();
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(requireActivity(), R.string.updating_feeds,
                    Toast.LENGTH_LONG).show();
        });

        // Fab button for new feed dialog
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(_view -> {
            DialogFragment dialogFragment = new NewFeedFragment();
            dialogFragment.show(getParentFragmentManager(), NewFeedFragment.FRAGMENT_TAG);
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

        alertDialogBuilder.setMessage(R.string.feed_delete_alert)
                .setTitle(R.string.warning);

        alertDialogBuilder.setPositiveButton(R.string.yes,
                (dialogInterface, i) -> {
                    FeedRepository.getRepository(requireContext()).deleteFeed(feed);
                    Toast.makeText(requireActivity(), R.string.feed_deleted,
                            Toast.LENGTH_LONG).show();
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialogInterface, i) -> {});

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            // Set text color for both buttons, can only be done after .show()
            // FIXME: XML override would be better but couldn't find the correct id.
            int color = ContextCompat.getColor(requireContext(), R.color.dialogButtonColor);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        });

        alertDialog.show();
    }

}