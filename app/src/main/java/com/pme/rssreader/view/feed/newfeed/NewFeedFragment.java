package com.pme.rssreader.view.feed.newfeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.view.feed.list.FeedListViewModel;
import com.pme.rssreader.view.feed.list.adapter.FeedRecyclerViewAdapter;
import com.pme.rssreader.view.item.list.ItemListActivity;

import java.util.Objects;

public class NewFeedFragment extends Fragment {

    private static final String LOG_TAG = "NewFeedActivity";
    private EditText nameText;
    private EditText linkText;

    private View view;

    FeedRepository feedRepository;

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

        View view = inflater.inflate(R.layout.fragment_new_feed, container, false);

        // Link Button with Click Listener
        Button saveFeed = view.findViewById(R.id.btn_save_feed);
        saveFeed.setOnClickListener(this.buttonClickListener);

        // Text
        nameText = view.findViewById(R.id.text_input_new_feed_name);
        linkText = view.findViewById(R.id.text_input_new_feed_link);

        // Repo
        feedRepository = FeedRepository.getRepository(requireContext());

        return view;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.i( LOG_TAG, "Button tapped");

            // Check which button was clicked
            if( v.getId() == R.id.btn_save_feed)
            {
                saveNewFeed();
            }
        }
    };

    private void goBack() {
        requireActivity().onBackPressed();
    }

    private void saveNewFeed() {
        if (nameText.getText().toString().equals("") || linkText.getText().toString().equals("")) {
            showSnackbar("Fill");
            return;
        }
        Feed f = new Feed(nameText.getText().toString(), linkText.getText().toString());
        feedRepository.insert(f);
        Log.i(LOG_TAG, "FEED SAVED");
        showSnackbar("Feed saved!");

        // Wait for snackbar feedback
        (new Handler()).postDelayed(this::goBack, 1500);

    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
                text,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }


}
