package com.pme.rssreader.sync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pme.rssreader.R;
import com.pme.rssreader.storage.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationUtils {

    private static final String CHANNEL_ID = "TEST_CHANNEL_ID";

    public static void createNotificationForNewItems(Context context, List<Item> items) {
        Log.e("createNotificationForNewItems", String.valueOf(items.size()));

        if (items.size() > 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

            // TODO

            builder.setContentTitle("New items available")
                    .setContentText("New Notification From Demo App..")
                    .setSmallIcon(R.mipmap.ic_launcher);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "NotificationDemo",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            if (notificationManager != null) {
                notificationManager.notify(0, builder.build());
            }
        }
    }

}
