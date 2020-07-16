package com.pme.rssreader.view.itemview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.widget.Toast;

import com.pme.rssreader.R;

public class ItemViewActivity extends AppCompatActivity {

    private ItemViewViewModel itemViewViewModel;

    private int currentId;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        currentId = getIntent().getIntExtra("SELECTED_ITEM_ID", 0);
        Log.w("ItemViewActivity feed id:", String.valueOf(currentId));

        this.itemViewViewModel =  new ViewModelProvider(this, factory).get(ItemViewViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.item_view_recycler_view);
        final ItemViewAdapter adapter = new ItemViewAdapter(this, itemViewViewModel, currentId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemViewViewModel.getAllItems().observe(this, adapter::setItems);

        swipeRefreshLayout = findViewById(R.id.item_view_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
//            itemViewViewModel.refreshFeed();
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(ItemViewActivity.this, "Feed refreshed!", Toast.LENGTH_SHORT).show();
        });

        itemViewViewModel.getItemSelectedEvent().observe(this, item -> {
            Intent i = new Intent(this, ItemViewDetailActivity.class);
            i.putExtra(ItemViewDetailActivity.CURRENT_ITEM, item);
            startActivity(i);
        });

    }

    ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ItemViewViewModel(getApplication(), itemViewViewModel, currentId);
        }
    };


}