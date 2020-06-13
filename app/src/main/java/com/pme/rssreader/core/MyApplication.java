package com.pme.rssreader.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pme.rssreader.R;

public class MyApplication extends Application {

    private static final String LOG_TAG = "Application";

    private static final String DEFAULT_STRING_VALUE = "<no value>";
    private static final int DEFAULT_INT_VALUE = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "Application onCreate.");
    }

    /*
        Shared Preferences Handlers
     */
    private SharedPreferences getPreferences() {
        return this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
    }

    public void writeValue(String key, String value)
    {
        this.getPreferences().edit().putString(key, value).apply();
    }

    public void writeValue(String key, int value)
    {
        this.getPreferences().edit().putInt(key, value).apply();
    }

    public String getStringValue( String key )
    {
        return this.getPreferences().getString(key, DEFAULT_STRING_VALUE);
    }

    public int getIntValue( String key )
    {
        return this.getPreferences().getInt(key, DEFAULT_INT_VALUE );
    }
}
