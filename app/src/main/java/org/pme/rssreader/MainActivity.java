package org.pme.rssreader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;

import org.pme.rssreader.core.App;
import org.pme.rssreader.core.Constants;
import org.pme.rssreader.storage.FeedRepository;
import org.pme.rssreader.view.item.list.ItemListFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_NAVIGATE_TO_FEED_ID = "NAVIGATE_TO_FEED_ID";
    public static final String INTENT_EXTRA_NAVIGATE_TO_FEED_NAME = "NAVIGATE_TO_FEED_NAME";

    private AppBarConfiguration appBarConfiguration;

    private String helpDialogContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set status bar color according to dark mode
        // Workaround for Theme.MaterialComponents.DayNight defaulting to primaryDark regardless of night mode.
        if (App.shouldEnableDarkMode(getApplicationContext())) {
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.statusBarDark));
        }

        // Init help dialog content text
        resetHelpDialogContent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_feed_list, R.id.nav_settings, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // MainActivity can be called from a background alarm.
        // The idea is to notify the user of new items on a specific feed.
        // So the alarm triggers an intent with extras and MainActivity navigates after it gets opened.
        if (getIntent().getExtras() != null) {
            int feedIdToNavigate = getIntent().getIntExtra(INTENT_EXTRA_NAVIGATE_TO_FEED_ID, -1);
            String feedName = getIntent().getStringExtra(INTENT_EXTRA_NAVIGATE_TO_FEED_NAME);
            if (feedIdToNavigate != -1 && feedName != null) {
                Bundle bundle = new Bundle();
                bundle.putInt(ItemListFragment.EXTRA_FEED_ID, feedIdToNavigate);
                bundle.putString("FEED_TITLE", feedName);
                navController.navigate(R.id.action_nav_feed_list_to_containerFragment, bundle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            activateHelpOverlay();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    /**
//     * Returns true if the app was opened by the user and not automatically
//     */
//    private boolean shouldDoInitialUpdate() {
//        return !Objects.equals(getIntent().getAction(), Constants.ALARM_RECEIVER_ACTION);
//    }

    private void activateHelpOverlay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tips);
        builder.setMessage(helpDialogContent);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.OK, (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setHelpDialogContent(String helpDialogContent) {
        this.helpDialogContent = helpDialogContent;
    }

    public void resetHelpDialogContent() {
        this.helpDialogContent = getString(R.string.help_dialog_content_list_view);
    }
}