package com.pme.rssreader.view.item.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.item.list.ItemViewModel;

public class DetailsFragment extends Fragment {

    public static DetailsFragment newInstance(Item currentItem) {
        DetailsFragment f = new DetailsFragment();

        // Pass current Item as argument
        Bundle args = new Bundle();
        args.putSerializable("CURRENT_ITEM", currentItem);
        f.setArguments(args);

        return f;
    }

    private Item getCurrentItem() {
        if (getArguments() != null) {
            return (Item) getArguments().getSerializable("CURRENT_ITEM");
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Don't return a view if it won't be used
        if (container == null) { return null; }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Item currentItem = getCurrentItem();

        if (currentItem != null) {
            // Remove placeholder text
            view.findViewById(R.id.text_placeholder).setVisibility(View.INVISIBLE);

            TextView title = view.findViewById(R.id.item_view_detail_title);
            TextView content = view.findViewById(R.id.item_view_detail_content);
            TextView date = view.findViewById(R.id.item_view_detail_date);
            Button button = view.findViewById(R.id.item_view_detail_button);

            title.setText(currentItem.getTitle());
            if (currentItem.getContent() != null) {
                content.setText(HtmlCompat.fromHtml(currentItem.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                content.setText(HtmlCompat.fromHtml(currentItem.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
            // To enable scrolling and clickable links
            content.setMovementMethod(LinkMovementMethod.getInstance());
            date.setText(currentItem.getPubDate().toString());

            button.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentItem.getLink()));
                startActivity(i);
            });

        }

        return view;

    }

}