package org.pme.rssreader.sync;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import org.pme.rssreader.R;
import org.pme.rssreader.core.Constants;
import org.pme.rssreader.storage.model.Item;
import org.pme.rssreader.MainActivity;

import java.util.List;
import java.util.Locale;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class NotificationUtils {

    private static final String CHANNEL_ID = "RSS_READER_CHANNEL_ID";
    private static final String CHANNEL_NAME = "RSS Reader";

    public static void createNotificationForNewItems(Context context, List<Item> items) {
        Log.e("createNotificationForNewItems", String.valueOf(items.size()));

        if (!conditionsPassed(context)) { return; }

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
            int feedId = items.get(0).getFeedId(); // FeedId gets passed to MainActivity as extra
            Log.e("createNotificationForNewItems feedId ", String.valueOf(feedId));
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(MainActivity.INTENT_EXTRA_NAVIGATE_TO_FEED_ID, feedId);
            intent.putExtra(MainActivity.INTENT_EXTRA_NAVIGATE_TO_FEED_NAME, feedName);
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

    public static boolean runningInForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public static boolean conditionsPassed(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean syncButDontNotify = sp.getBoolean(Constants.SYNC_WITHIN_HOURS_BUT_DONT_NOTIFY, false);

        if (syncButDontNotify || runningInForeground()) { return false; }

        return true;
    }

}
