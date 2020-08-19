package com.pme.rssreader.core;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.pme.rssreader.sync.AlarmReceiver;

import java.util.Calendar;


public class App extends Application {

    private static final String LOG_TAG = "Application";

    private static App INSTANCE;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "Application onCreate.");
        loadPreferences();

        // Alarm manager tests
        this.setUpdateInterval(true, 60);
    }

    public Application getApplication()
    {
        return getApplication();
    }

    private void loadPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("darkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void setUpdateInterval(boolean isActive, int intervalSec) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("RSS_UPDATE_ACTION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int alarmDelay = 10;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, alarmDelay);

        assert alarmManager != null;
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalSec * 1_000L, pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
