package org.pme.rssreader.storage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.pme.rssreader.storage.model.Feed;
import org.pme.rssreader.storage.model.Item;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room database class with a getter and a callback that gets called after opening the database.
 */
@Database(entities = {Feed.class, Item.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG_DB = "RoomDB";

    public abstract FeedDao feedDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseThreadExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        Log.i( LOG_TAG_DB, "getDatabase() called" );

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .addCallback(openCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * Database gets populated with default feeds for test purposes.
     * "Test Feed" will have a new item available each minute.
     */
    private static RoomDatabase.Callback openCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.i( LOG_TAG_DB, "onOpen() called" );
            databaseThreadExecutor.execute(() -> {
                FeedDao dao = INSTANCE.feedDao();
                // dao.deleteAll();
                Feed f = new Feed("FHE AI Schwarzes Brett", "https://www.ai.fh-erfurt.de/rss.schwarzesbrett");
                dao.insert(f);
                Feed f3 = new Feed("Spiegel Online", "https://www.spiegel.de/schlagzeilen/tops/index.rss");
                dao.insert(f3);
                Feed f2 = new Feed("Test Feed", "https://lorem-rss.herokuapp.com/feed");
                dao.insert(f2);
            });
        }
    };

}
