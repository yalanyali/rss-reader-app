package org.pme.rssreader.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

/**
 * Android does not save any alarms when the device is turned off.
 * Therefore, all alarms will be missed when device is booted.
 * In order to maintain the previously set alarm, we have to listen to the system boot event
 * and manually set it back when device is turned on again.
 */
public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RebootReceiver", "START");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmUtils.updateAlarmUsingSharedPrefs(context, sp);
    }
}
