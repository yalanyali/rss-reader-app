package com.pme.rssreader.view.item.list;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.item.details.DetailsActivity;
import com.pme.rssreader.view.item.details.DetailsFragment;

public class ItemListActivity extends AppCompatActivity {

    private Fragment itemListFragment;
    private String ITEM_LIST_FRAGMENT_TAG = "ITEM_LIST";

    private Fragment itemDetailFragment;
    private String ITEM_DETAILS_FRAGMENT_TAG = "ITEM_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_fragment_layout);

        Integer currentFeedId = getIntent().getIntExtra("SELECTED_FEED_ID", 1);
        Log.w("ItemViewActivity feed id:", String.valueOf(currentFeedId));

        FragmentManager fm = getSupportFragmentManager();

        // Get ItemListFragment by tag
        Fragment fragment = fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);

        if (fragment == null)
        {
            // Create fragment
            itemListFragment = ItemListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("SELECTED_ITEM_ID", currentFeedId);
            itemListFragment.setArguments(bundle);

            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.item_list_container, itemListFragment, ITEM_LIST_FRAGMENT_TAG);
            ft.commit();
        } else {
            itemListFragment = fragment;
        }
    }

    public void showDetailsContainer(Item item) {
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsContainer = findViewById(R.id.item_detail_container);
        boolean dualPane = detailsContainer != null && detailsContainer.getVisibility() == View.VISIBLE;

        if (dualPane) {
            // TODO: Highlight item

            // Check what fragment is currently shown, replace if needed
            DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(ITEM_DETAILS_FRAGMENT_TAG);
            if (detailsFragment == null) {
                detailsFragment = DetailsFragment.newInstance(item);

                // Replace any existing fragment
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.item_detail_container, detailsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Launch a new activity to display fragment
            Intent i = new Intent(this, DetailsActivity.class);
            i.putExtra(DetailsActivity.CURRENT_ITEM, item);
            startActivity(i);
        }

    }

}