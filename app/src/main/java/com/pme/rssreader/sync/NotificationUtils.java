package com.pme.rssreader.sync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;
import com.pme.rssreader.view.item.list.ItemListActivity;

import java.util.List;
import java.util.Locale;

public class NotificationUtils {

    private static final String CHANNEL_ID = "RSS_READER_CHANNEL_ID";
    private static final String CHANNEL_NAME = "RSS Reader";

    public static void createNotificationForNewItems(Context context, List<Item> items) {
        Log.e("createNotificationForNewItems", String.valueOf(items.size()));

        if (items.size() > 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

            // Feed name on notification
            String feedName = items.get(0).getFeedName();
            builder.setContentTitle(String.format("%s", feedName));

            if (items.size() > 1) {
                // Multiple new items on feed
                builder.setContentText(String.format(Locale.US ,"%d new items", items.size()));
            } else {
                builder.setContentText("1 new item");
            }

            builder.setSmallIcon(R.mipmap.ic_launcher);

            // Intent for when the user taps on the notification
            int feedId = items.get(0).getFeedId(); // FeedId gets passed to ItemListActivity as extra
            Log.e("createNotificationForNewItems feedId ", String.valueOf(feedId));
            Intent intent = new Intent(context, ItemListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(ItemListActivity.INTENT_EXTRA, feedId);
            // Request code is generated from feed name to allow one PendingIntent per feed
            PendingIntent pendingIntent = PendingIntent.getActivity(context, feedName.hashCode(), intent, 0);

            // Set content intent of notification
            builder.setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            if (notificationManager != null) {
                // Notification id is generated from feed name to allow one notification per feed
                notificationManager.notify(feedName.hashCode(), builder.build());
            }
        }
    }

}
