package com.pme.rssreader.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pme.rssreader.R;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG_LIFE_CYCLES = "LifecycleCallbacks";
    private static final String LOG_TAG_EVENTS = "EventCallbacks";

    /*
        Definition of a new click listener instance as anonymous class
        Will handle all buttons from this activity
    */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.i( LOG_TAG_EVENTS, "Button tapped");

            // Check which button was clicked
            if(v.getId() == R.id.btn_to_feed_list) {
                Log.i(LOG_TAG_EVENTS, "Go to Second Activity Button tapped");
                goToFeedListActivity();
            } else if (v.getId() == R.id.btn_new_feed) {
                Log.i(LOG_TAG_EVENTS, "btn_new_feed tapped");
                goToNewFeedActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link Buttons with Click Listener
        Button toFeedList = findViewById(R.id.btn_to_feed_list);
        toFeedList.setOnClickListener(this.buttonClickListener);
        Button toNewFeed = findViewById(R.id.btn_new_feed);
        toNewFeed.setOnClickListener(this.buttonClickListener);
    }

    private void goToFeedListActivity() {
        Log.i(LOG_TAG_EVENTS, "Switching to Second Activity, Feed List");
        Intent i = new Intent(MainActivity.this, FeedListActivity.class);
        startActivity(i);
    }

    private void goToNewFeedActivity() {
        Log.i(LOG_TAG_EVENTS, "Switching to New Feed Activity");
        Intent i = new Intent(MainActivity.this, NewFeedActivity.class);
        startActivity(i);
    }

    /*
        Life Cycle Callback Methods
    */
    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOG_TAG_LIFE_CYCLES, "onStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(LOG_TAG_LIFE_CYCLES, "onStop() called");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(LOG_TAG_LIFE_CYCLES, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG_LIFE_CYCLES, "onResume() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG_LIFE_CYCLES, "onDestroy() called");
    }

}