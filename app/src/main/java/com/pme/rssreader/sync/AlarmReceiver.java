package com.pme.rssreader.sync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.pme.rssreader.R;
import com.pme.rssreader.core.App;
import com.pme.rssreader.storage.FeedRepository;

import java.util.Calendar;
import java.util.Objects;

/**
 * BroadcastReceiver is a class for receiving and handling the broadcast sent to your application.
 * In system alarm case, it receives broadcast from the system alarm service when alarm goes off.
 * It has to be either registered dynamically in activity or statically declared at AndroidManifest.xml.
 *
 * Also using Run command on Android Studio won't work for pending Alarms. Need to sideload APK to test properly.
 * See: https://issuetracker.google.com/issues/150080941
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "TEST_CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        // FIXME: Hardcoded
        if (Objects.equals(intent.getAction(), "RSS_UPDATE_ACTION")) {
            Log.e("ALARMA", String.format("Geldi %d", Calendar.getInstance().getTimeInMillis()));
            new UpdateTask().execute(context);
//            FeedRepository feedRepository = FeedRepository.getRepository(context);
//            feedRepository.refreshFeeds();
            fireNotification(context);
        }
    }

    private void fireNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setContentTitle("Demo App Notification")
                .setContentText("New Notification From Demo App..")
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build());
    }
}
