package org.pme.rssreader.view.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import me.philio.preferencecompatextended.PreferenceFragmentCompat;

import org.pme.rssreader.R;
import org.pme.rssreader.core.App;
import org.pme.rssreader.core.Constants;
import org.pme.rssreader.sync.AlarmUtils;


/**
 * Settings view using PreferenceFragment. Settings are in root_preferences.xml, stored in default shared preferences.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        switch (key) {
            case Constants.SETTING_AUTO_DARK_MODE_ENABLED:
            case Constants.SETTING_DARK_MODE_ENABLED:
                App.checkAndApplyDarkMode(requireContext());
                break;
            case Constants.SETTING_SYNC_INTERVAL:
            case Constants.SETTING_SYNC_ENABLED:
                AlarmUtils.updateAlarmUsingSharedPrefs(requireContext(), sharedPreferences);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide help button from top bar
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}