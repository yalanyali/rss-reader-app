package com.pme.rssreader.view.item.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pme.rssreader.R;
import com.pme.rssreader.view.item.list.adapter.ItemRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class ItemListFragment extends Fragment {

    private ItemViewModel itemViewModel;

    private int currentId;

    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        if (savedInstanceState != null) {
            // Restore last state for higlighted position
//        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentId = getArguments().getInt("SELECTED_ITEM_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        itemViewModel = new ViewModelProvider(requireActivity(), factory).get(ItemViewModel.class);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            final ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context, itemViewModel, currentId);
            recyclerView.setAdapter(adapter);

            itemViewModel.getAllItems().observe(getViewLifecycleOwner(), adapter::setItems);

            // Swipe layout?

            itemViewModel.getItemSelectedEvent().observe(getViewLifecycleOwner(), item -> {
                // Call activity method to show details container accordingly
                ((ItemListActivity)requireActivity()).showDetailsContainer(item);
            });

        }
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