package com.pme.rssreader.view.item.list.adapter;


import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.item.ContainerFragment;
import com.pme.rssreader.view.item.ItemViewModel;

import java.util.Date;
import java.util.List;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView content;

        private ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.row_item_title);
            this.date = itemView.findViewById(R.id.row_item_date);
            this.content = itemView.findViewById(R.id.row_item_content);
        }
    }

    private final LayoutInflater inflater;
    private List<Item> items;
    private ItemViewModel viewModel;

    private Context context;
    private NavController navController;

    private String feedTitle;

    public ItemRecyclerViewAdapter(Context context, ItemViewModel viewModel, NavController navController, String feedTitle) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.navController = navController;
        this.feedTitle = feedTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (this.items != null) {
            Item current = items.get(position);
            holder.title.setText(current.getTitle());
            if (current.getContent() != null) {
                holder.content.setText(HtmlCompat.fromHtml(current.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                holder.content.setText(HtmlCompat.fromHtml(current.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }

            Date pubDate = current.getPubDate();
            Date currentDate = new Date();
            String relativeTimeSpanString = (String) DateUtils.getRelativeTimeSpanString(pubDate.getTime(), currentDate.getTime(), DateUtils.SECOND_IN_MILLIS);
            holder.date.setText(relativeTimeSpanString);

            holder.itemView.setOnClickListener(view -> {
                viewModel.setItemSelected(current);
                // Check if navigation needed
                navigateMaybe();
            });

        } else {
            holder.title.setText("???");
            holder.content.setText("???");
            holder.date.setText("???");
        }
    }


    public void setItems(List<Item> items) {
        this.items = items;
        // Set selected as the first one
        if (items.size() > 0) {
            viewModel.setItemSelected(items.get(0));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.items != null ? this.items.size() : 0;
    }

    /**
     * If details pane is not shown, navigates to details fragment
     */
    private void navigateMaybe() {
        Fragment detailsFragment = ((AppCompatActivity)context).getSupportFragmentManager()
                .findFragmentByTag(ContainerFragment.ITEM_DETAILS_FRAGMENT_TAG);

        if (detailsFragment == null || !detailsFragment.isVisible()) {
            // Details fragment is not shown, navigate.
            // Pass custom title
            Bundle bundle = new Bundle();
            bundle.putString("FEED_TITLE", feedTitle);
            navController.navigate(R.id.action_containerFragment_to_itemDetailsFragment, bundle);
        }
    }

}
