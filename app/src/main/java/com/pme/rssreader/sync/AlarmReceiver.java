package com.pme.rssreader.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import com.pme.rssreader.core.Constants;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        // FIXME: Hardcoded
        if (Objects.equals(intent.getAction(), Constants.ALARM_RECEIVER_ACTION)) {
            Log.e("ALARMA", String.format("Geldi %d", Calendar.getInstance().getTimeInMillis()));

            FeedRepository feedRepository = FeedRepository.getRepository(context);
            feedRepository.refreshFeedsInBackground();
        }
    }

//    private void checkConditions
}
