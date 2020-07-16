package com.pme.rssreader.view.itemview;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.FeedRepository;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.storage.model.Item;

import java.util.List;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewViewHolder> {

    class ItemViewViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView content;

        private ItemViewViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.row_item_title);
            this.date = itemView.findViewById(R.id.row_item_date);
            this.content = itemView.findViewById(R.id.row_item_content);
        }
    }

    private final LayoutInflater inflater;
    private List<Item> items;
    private ItemViewViewModel viewModel;

    public ItemViewAdapter(Context context, ItemViewViewModel viewModel, int feedId) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ItemViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_item, parent, false);
        return new ItemViewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewViewHolder holder, int position) {
        if (this.items != null) {
            Item current = items.get(position);
            holder.title.setText(current.getTitle());
            if (current.getContent() != null) {
                holder.content.setText(HtmlCompat.fromHtml(current.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                holder.content.setText(HtmlCompat.fromHtml(current.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
            holder.date.setText("31 h. ago");

            holder.itemView.setOnClickListener(view -> viewModel.setItemSelected(current));

        } else {
            holder.title.setText("???");
            holder.content.setText("???");
            holder.date.setText("???");
        }
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.items != null ? this.items.size() : 0;
    }


}
