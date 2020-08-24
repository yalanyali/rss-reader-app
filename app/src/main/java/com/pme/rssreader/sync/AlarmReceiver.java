package com.pme.rssreader.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.pme.rssreader.core.Constants;
import com.pme.rssreader.storage.FeedRepository;

import java.util.Calendar;
import java.util.Objects;


/**
 * BroadcastReceiver is a class for receiving and handling the broadcast sent to your application.
 * In system alarm case, it receives broadcast from the system alarm service when alarm goes off.
 * It has to be either registered dynamically in activity or statically declared at AndroidManifest.xml.
 *
 * Also using Run command on Android Studio won't work for pending alarms. Need to sideload APK to test properly.
 * See: https://issuetracker.google.com/issues/150080941
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Constants.ALARM_RECEIVER_ACTION)) {
            if (conditionsPassed(context)) {
                FeedRepository feedRepository = FeedRepository.getRepository(context);
                feedRepository.refreshFeedsInBackground();
            }
        }
    }

    private boolean conditionsPassed(Context context) {
        Log.e("AlarmReceiver/conditionsPassed", "START");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean onlyWithinHours = sp.getBoolean(Constants.SETTING_SYNC_WITHIN_HOURS_ENABLED, false);
        boolean syncButDontNotify = sp.getBoolean(Constants.SYNC_WITHIN_HOURS_BUT_DONT_NOTIFY, false);

        if (!onlyWithinHours) { Log.e("AlarmReceiver", "!onlyWithinHours"); return true; }

        if (syncButDontNotify) { return true; }

        // Check if current time is between selected hours

        Calendar currentTimeCal = Calendar.getInstance();

        int startTimeInSeconds = sp.getInt(Constants.SETTING_SYNC_TIME_START_SECONDS, -1);
        int endTimeInSeconds = sp.getInt(Constants.SETTING_SYNC_TIME_END_SECONDS, -1);

        // Today's date but the time is startTime
        Calendar startTimeCal = getCurrentDateMidnightPlusOffset(startTimeInSeconds);

        // Today's date but the time is endTime
        Calendar endTimeCal = getCurrentDateMidnightPlusOffset(endTimeInSeconds);

        // If endTime < startTime, then endTime must mean "tomorrow"
        if (endTimeInSeconds < startTimeInSeconds) {
            endTimeCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Now compare
        return currentTimeCal.getTimeInMillis() >= startTimeCal.getTimeInMillis()
                && currentTimeCal.getTimeInMillis()  <= endTimeCal.getTimeInMillis() ;

    }

    private Calendar getCurrentDateMidnightPlusOffset(int offsetSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.SECOND, offsetSeconds);
        return cal;
    }
}
