package com.pme.rssreader.view.item.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pme.rssreader.R;
import com.pme.rssreader.view.item.list.adapter.ItemRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class ItemListFragment extends Fragment {

    public static String INTENT_EXTRA = "SELECTED_FEED_ID";

    private ItemViewModel itemViewModel;

    private int currentId;

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

        if (getArguments() != null) {
            currentId = getArguments().getInt(INTENT_EXTRA);
            Log.e("ItemListFragment", String.valueOf(currentId));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_item_list_recycler_view);

        itemViewModel = new ViewModelProvider(requireActivity(), factory).get(ItemViewModel.class);


        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set the adapter
        final ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context, itemViewModel, currentId);
        recyclerView.setAdapter(adapter);

        itemViewModel.getAllItems().observe(getViewLifecycleOwner(), adapter::setItems);

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

        itemViewModel.getItemSelectedEvent().observe(getViewLifecycleOwner(), item -> {
            // Call activity method to show details container accordingly
            ((ItemListActivity)requireActivity()).showDetailsContainer(item);
        });

        return view;
    }

    private final ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ItemViewModel(requireActivity().getApplication(), currentId);
        }
    };

}