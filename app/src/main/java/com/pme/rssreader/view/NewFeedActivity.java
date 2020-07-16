package com.pme.rssreader.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;

public class NewFeedActivity extends AppCompatActivity {

    private static final String LOG_TAG = "NewFeedActivity";
    private EditText nameText;
    private EditText linkText;

    FeedRepository feedRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed);

        // Link Button with Click Listener
        Button saveFeed = findViewById(R.id.btn_save_feed);
        saveFeed.setOnClickListener(this.buttonClickListener);

        // Text
        nameText = findViewById(R.id.text_input_new_feed_name);
        linkText = findViewById(R.id.text_input_new_feed_link);

        // Repo
        feedRepository = FeedRepository.getRepository(getApplication());
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

    private void saveNewFeed() {
        if (nameText.getText().toString().equals("") || linkText.getText().toString().equals("")) {
            showSnackbar("Fill");
            return;
        }
        Feed f = new Feed(nameText.getText().toString(), linkText.getText().toString());
        feedRepository.insert(f);
        Log.i(LOG_TAG, "FEED SAVED");
        showSnackbar("Feed saved!");
    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                text,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
