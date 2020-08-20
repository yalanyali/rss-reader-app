package com.pme.rssreader.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;


public class App extends Application {

    private static final String LOG_TAG = "Application";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "Application onCreate.");
        loadPreferences();

        // Alarm manager tests
//        this.setUpdateInterval(true, 60);
//        AlarmService.setOrUpdateAlarm(getApplicationContext(), 60);
    }

    private void loadPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Handle dark mode switch
        if (sp.getBoolean(Constants.SETTING_DARK_MODE_ENABLED, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

}
