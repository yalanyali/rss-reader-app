package com.pme.rssreader.view.feed.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.FeedWithItems;
import com.pme.rssreader.view.feed.list.FeedListViewModel;

import java.util.List;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.FeedViewHolder> {

    static class FeedViewHolder extends RecyclerView.ViewHolder {

        private final TextView feedListItemNameTextView;
        private final TextView feedListItemLinkTextView;

        public FeedViewHolder(View itemView) {
            super(itemView);
            this.feedListItemNameTextView = itemView.findViewById(R.id.list_item_feed_name_text);
            this.feedListItemLinkTextView = itemView.findViewById(R.id.list_item_feed_link_text);
        }

    }

    private final LayoutInflater inflater;
    private final FeedListViewModel viewModel;
    private List<FeedWithItems> feeds;

    public FeedRecyclerViewAdapter(Context context, FeedListViewModel viewModel) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_feed, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int index) {
        if (this.feeds != null) {
            FeedWithItems current = feeds.get(index);
            holder.feedListItemLinkTextView.setText(current.getFeed().getLink());
            holder.feedListItemNameTextView.setText(current.getFeed().getName());

            holder.itemView.setOnClickListener(view ->
                viewModel.setItemSelected(current.getFeed().getFeedId())
            );
        } else {
            holder.feedListItemNameTextView.setText("NO_NAME");
            holder.feedListItemLinkTextView.setText("NO_LINK");
        }
    }

    public void setFeeds(List<FeedWithItems> feeds) {
        this.feeds = feeds;
        notifyDataSetChanged();
    }

    public void refreshFeeds() {
        this.viewModel.refreshFeeds();
    }

    @Override
    public int getItemCount() {
        return this.feeds != null ? this.feeds.size() : 0;
    }

}
