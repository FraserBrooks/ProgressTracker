package com.fraserbrooks.progresstracker.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */

public class LocalDataSource implements DataSource{

    private static volatile LocalDataSource INSTANCE;

    private TrackersDao mTrackersDao;

    private AppExecutors mAppExecutors;


    // Prevent direct instantiation.
    private LocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TrackersDao trackersDao) {
        mAppExecutors = appExecutors;
        mTrackersDao = trackersDao;
    }

    public static LocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TrackersDao trackersDao) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource(appExecutors, trackersDao);
                }
            }
        }
        return INSTANCE;
    }


    /**
     * Note: {@link LoadTrackersCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTrackers(@NonNull final LoadTrackersCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Tracker> trackers = mTrackersDao.getTrackers();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (trackers.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTrackersLoaded(trackers);
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Tracker task = mTrackersDao.getTrackerById(trackerId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null) {
                            callback.onTrackerLoaded(task);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTracker(@NonNull final Tracker tracker) {
        checkNotNull(tracker);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.insertTracker(tracker);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshTrackers() {
        // Not required because the {@link Repository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTrackers() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTrackers();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteTracker(@NonNull final String trackerId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTrackerById(trackerId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance(){
        INSTANCE = null;
    }

    private <T> T checkNotNull(T reference){
        if(reference == null){
            throw new NullPointerException();
        }else{
            return reference;
        }
    }

}
