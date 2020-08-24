package org.pme.rssreader.view.item.list;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.pme.rssreader.R;
import org.pme.rssreader.view.item.ItemViewModel;
import org.pme.rssreader.view.item.list.adapter.ItemRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class ItemListFragment extends Fragment {

    public static String EXTRA_FEED_ID = "SELECTED_FEED_ID";
    public static String EXTRA_FEED_TITLE = "SELECTED_FEED_TITLE";

    private ItemViewModel itemViewModel;

    private int currentId;
    private String currentTitle;

    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentId = requireArguments().getInt(EXTRA_FEED_ID);
        currentTitle = requireArguments().getString("FEED_TITLE");
        Log.e("ItemListFragment", String.valueOf(currentId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_item_list_recycler_view);

        // Init the shared view model for item/detail fragments.
        // Passing the activity context to let the view model live throughout the main activity lifecycle.
        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        itemViewModel.setFeedId(currentId);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set the adapter
        NavController navController = NavHostFragment.findNavController(this);
        final ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context, itemViewModel, navController, requireArguments().getString("FEED_TITLE"));
        recyclerView.setAdapter(adapter);

        itemViewModel.getAllItems().observe(getViewLifecycleOwner(), adapter::setItems);

//        Log.e("TEST", itemViewModel.);

        // Swipe to refresh
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemViewModel.refreshFeed(currentId);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireActivity(), "Feed updated!",
                        Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

}