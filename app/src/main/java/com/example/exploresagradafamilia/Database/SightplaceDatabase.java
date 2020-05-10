package com.example.exploresagradafamilia.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.exploresagradafamilia.Sightplace;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Sightplace.class}, version = 1, exportSchema = false)
abstract class SightplaceDatabase extends RoomDatabase {
    abstract SightplaceDAO SightplaceDAO();

    //Singleton instance
    private static volatile SightplaceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    //ExecutorService with a fixed thread pool that you will use to run database operations
    // asynchronously on a background thread.
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //get the singleton instance
    static SightplaceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SightplaceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SightplaceDatabase.class, "sightplace_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}