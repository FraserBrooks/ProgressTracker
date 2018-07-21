package com.fraserbrooks.progresstracker.data.source.local;

/**
 * Created by Fraser on 07/04/2018.
 */


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;


/**
 * The Room Database that contains the Task table.
 */
@Database(entities = {Tracker.class, Target.class, ScoreEntry.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class ProgressTrackerDatabase extends RoomDatabase {

    private static ProgressTrackerDatabase INSTANCE;

    public abstract TrackersDao trackersDao();

    private static final Object sLock = new Object();

    public static ProgressTrackerDatabase getInstance(Context context){
        synchronized (sLock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ProgressTrackerDatabase.class, "ProgressTracker.db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }

}
