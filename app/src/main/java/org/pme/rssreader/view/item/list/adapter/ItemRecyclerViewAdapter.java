package org.pme.rssreader.view.item.list.adapter;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import org.pme.rssreader.R;
import org.pme.rssreader.storage.model.Item;
import org.pme.rssreader.view.item.ContainerFragment;
import org.pme.rssreader.view.item.ItemViewModel;

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
    private boolean allItemsMode;

    public ItemRecyclerViewAdapter(Context context, ItemViewModel viewModel, NavController navController, String feedTitle, boolean allItemsMode) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.navController = navController;
        this.feedTitle = feedTitle;
        this.allItemsMode = allItemsMode;
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
            holder.date.setText(viewModel.getRelativeDateString(pubDate));

            holder.itemView.setOnClickListener(view -> {
                viewModel.setItemSelected(current);
                // Check if navigation needed
                navigateMaybe();
            });

        } else {
            holder.title.setText(R.string.generic_placeholder);
            holder.content.setText(R.string.generic_placeholder);
            holder.date.setText(R.string.generic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return this.items != null ? this.items.size() : 0;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        // Set selected as the first one
        if (items.size() > 0) {
            viewModel.setItemSelected(items.get(0));
        }
        notifyDataSetChanged();
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
            if (allItemsMode) {
                bundle.putString("FEED_TITLE", "News");
                navController.navigate(R.id.action_nav_all_items_to_itemDetailsFragment, bundle);
            } else {
                bundle.putString("FEED_TITLE", feedTitle);
                navController.navigate(R.id.action_containerFragment_to_itemDetailsFragment, bundle);
            }
        }
    }

}
