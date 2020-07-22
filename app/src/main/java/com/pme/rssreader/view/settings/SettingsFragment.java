package com.pme.rssreader.view.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.pme.rssreader.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        Log.e("SETTINGS", key);
        switch (key) {
            case "darkMode":
//                Log.e("SETTINGS", String.valueOf(sharedPreferences.getBoolean(key, true)));
                int nightMode = sharedPreferences.getBoolean(key, false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                AppCompatDelegate.setDefaultNightMode(nightMode);
                break;
            case "colorAccent":
            case "syncInterval":
                Log.e("SETTINGS", sharedPreferences.getString(key, ""));
                break;
            case "sync":
                Log.e("SETTINGS", String.valueOf(sharedPreferences.getBoolean(key, true)));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}