package org.pme.rssreader.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.pme.rssreader.core.Constants;

import java.util.Calendar;

/**
 * Static alarm utilities.
 * For automatic background syncing we're using AlarmManager.
 * It's possible to set, update or cancel the repeating alarm.
 */
public class AlarmUtils {

    private static final int REQUEST_CODE = 3131; // Request code of reused pending intent
    private static final String DEFAULT_INTERVAL_MIN = "30";

    public static void setOrUpdateAlarm(Context context, int intervalSec) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, getAlarmIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);

        // Delay the first alarm for intervalSec
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, intervalSec);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalSec * 1_000L, pendingIntent);
        }
    }

    public static void cancelAlarm(Context context) {
        PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, getAlarmIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }

    public static void updateAlarmUsingSharedPrefs(Context context, SharedPreferences sp) {
        Log.i("ALARM", "SET");
        boolean syncEnabled = sp.getBoolean(Constants.SETTING_SYNC_ENABLED, false);
        if (!syncEnabled) {
            cancelAlarm(context);
        } else {
            int intervalMinutes = Integer.parseInt(sp.getString(Constants.SETTING_SYNC_INTERVAL, DEFAULT_INTERVAL_MIN));
            // Set/update alarm
            setOrUpdateAlarm(context, intervalMinutes * 60);
        }
    }

    private static Intent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Constants.ALARM_RECEIVER_ACTION);
        return intent;
    }

}
