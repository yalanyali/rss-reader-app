package com.pme.rssreader.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.FeedWithItems;

import java.util.List;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FeedWithItems feed);
    }

//    private final LayoutInflater inflater;
    private final OnItemClickListener listener;
    private List<FeedWithItems> feeds;


    static class FeedViewHolder extends RecyclerView.ViewHolder {

        private final TextView feedListItemNameTextView;
        private final TextView feedListItemLinkTextView;

        public FeedViewHolder(View itemView) {
            super(itemView);
            this.feedListItemNameTextView = itemView.findViewById(R.id.list_item_feed_name_text);
            this.feedListItemLinkTextView = itemView.findViewById(R.id.list_item_feed_link_text);
        }

        public void bind(final FeedWithItems current, final OnItemClickListener listener) {
            feedListItemNameTextView.setText(current.getFeed().getName());
            feedListItemLinkTextView.setText(current.getFeed().getLink());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(current);
                }
            });

        }
    }

    public FeedListAdapter(OnItemClickListener listener) {
//        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false); //inflater.inflate(R.layout.list_item_feed, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int index) {
        holder.bind(feeds.get(index), listener);
//        if (this.feeds != null) {
//            Feed current = feeds.get(index);
//            holder.feedListItemNameTextView.setText(current.getName());
//            holder.feedListItemLinkTextView.setText(current.getLink());
//        } else {
//            holder.feedListItemNameTextView.setText("NO_NAME");
//            holder.feedListItemLinkTextView.setText("NO_LINK");
//        }
    }

    @Override
    public int getItemCount() {
        return this.feeds != null ? this.feeds.size() : 0;
    }

    public void setFeeds(List<FeedWithItems> feeds) {
        this.feeds = feeds;
        notifyDataSetChanged();
    }

}
