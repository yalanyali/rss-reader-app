package com.pme.rssreader.view.itemview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.Feed;
import com.pme.rssreader.storage.model.Item;

public class ItemViewDetailActivity extends AppCompatActivity {

    public static final String CURRENT_ITEM = "CURRENT_ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view_detail);


        Item currentItem = (Item) getIntent().getSerializableExtra(CURRENT_ITEM);

        TextView title = findViewById(R.id.item_view_detail_title);
        TextView content = findViewById(R.id.item_view_detail_content);
        TextView date = findViewById(R.id.item_view_detail_date);
        Button button = findViewById(R.id.item_view_detail_button);

        if (currentItem != null) {
            title.setText(currentItem.getTitle());
            if (currentItem.getContent() != null) {
                content.setText(HtmlCompat.fromHtml(currentItem.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                content.setText(HtmlCompat.fromHtml(currentItem.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
            content.setMovementMethod(new ScrollingMovementMethod());
            date.setText(currentItem.getPubDate());

            button.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentItem.getLink()));
                startActivity(i);
            });
        }

    }
}