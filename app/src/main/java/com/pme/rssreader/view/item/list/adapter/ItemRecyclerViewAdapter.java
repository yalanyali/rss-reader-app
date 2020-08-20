package com.pme.rssreader.view.item.list.adapter;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.item.list.ItemViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    public ItemRecyclerViewAdapter(Context context, ItemViewModel viewModel, int feedId) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
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
