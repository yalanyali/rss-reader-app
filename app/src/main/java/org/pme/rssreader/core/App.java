package org.pme.rssreader.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * Application class for extension purposes.
 */
public class App extends Application {

    public static final String INTENT_EXTRA_INITIAL_UPDATE_DISABLED = "INTENT_EXTRA_INITIAL_UPDATE_DISABLED";

    @Override
    public void onCreate() {
        super.onCreate();
        checkAndApplyDarkMode(this);
    }

    /**
     * Checks if dark mode should be enabled depending on current time
     * @param context To access SharedPreferences
     */
    public static boolean shouldEnableDarkMode(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean autoDarkModeEnabled = sp.getBoolean(Constants.SETTING_AUTO_DARK_MODE_ENABLED, false);
        if (autoDarkModeEnabled) {
            int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            return !(hourOfDay > 7 && hourOfDay < 19);
        }

        //noinspection UnnecessaryLocalVariable
        boolean darkModeEnabled = sp.getBoolean(Constants.SETTING_DARK_MODE_ENABLED, false);

        return darkModeEnabled;
    }

    public static void setDarkMode(boolean enabled) {
        if (enabled) {
            // Light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void checkAndApplyDarkMode(Context context) {
        setDarkMode(shouldEnableDarkMode(context));
    }

    public static boolean runningInForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

}
