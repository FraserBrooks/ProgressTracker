package com.fraserbrooks.progresstracker.data.source;

/**
 * Created by Fraser on 07/04/2018.
 */


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fraserbrooks.progresstracker.data.Tracker;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


public class Repository implements DataSource {

    private static Repository INSTANCE = null;

    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;


    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Tracker> mCachedTrackers;


    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private Repository(@NonNull DataSource tasksRemoteDataSource,
                            @NonNull DataSource tasksLocalDataSource) {
        mRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource the backend data source
     * @param localDataSource  the device storage data source
     * @return the {@link Repository} instance
     */
    public static Repository getInstance(DataSource remoteDataSource,
                                              DataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DataSource, DataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets trackers from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadTrackersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTrackers(@NonNull final LoadTrackersCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTrackers != null && !mCacheIsDirty) {
            callback.onTrackersLoaded(new ArrayList<>(mCachedTrackers.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTrackersFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mLocalDataSource.getTrackers(new LoadTrackersCallback() {
                @Override
                public void onTrackersLoaded(List<Tracker> trackers) {
                    refreshCache(trackers);
                    callback.onTrackersLoaded(new ArrayList<>(mCachedTrackers.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTrackersFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void saveTracker(@NonNull Tracker tracker) {
        checkNotNull(tracker);
        mRemoteDataSource.saveTracker(tracker);
        mLocalDataSource.saveTracker(tracker);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTrackers == null) {
            mCachedTrackers = new LinkedHashMap<>();
        }
        mCachedTrackers.put(tracker.getId(), tracker);
    }
    

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {
        checkNotNull(trackerId);
        checkNotNull(callback);

        Tracker cachedTracker = getTrackerWithId(trackerId);

        // Respond immediately with cache if available
        if (cachedTracker != null) {
            callback.onTrackerLoaded(cachedTracker);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mLocalDataSource.getTracker(trackerId, new GetTrackerCallback() {
            @Override
            public void onTrackerLoaded(Tracker tracker) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTrackers == null) {
                    mCachedTrackers = new LinkedHashMap<>();
                }
                mCachedTrackers.put(tracker.getId(), tracker);
                callback.onTrackerLoaded(tracker);
            }

            @Override
            public void onDataNotAvailable() {
                mRemoteDataSource.getTracker(trackerId, new GetTrackerCallback() {
                    @Override
                    public void onTrackerLoaded(Tracker tracker) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedTrackers == null) {
                            mCachedTrackers = new LinkedHashMap<>();
                        }
                        mCachedTrackers.put(tracker.getId(), tracker);
                        callback.onTrackerLoaded(tracker);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshTrackers() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTrackers() {
        mRemoteDataSource.deleteAllTrackers();
        mLocalDataSource.deleteAllTrackers();

        if (mCachedTrackers == null) {
            mCachedTrackers = new LinkedHashMap<>();
        }
        mCachedTrackers.clear();
    }

    @Override
    public void deleteTracker(@NonNull String taskId) {
        mRemoteDataSource.deleteTracker(checkNotNull(taskId));
        mLocalDataSource.deleteTracker(checkNotNull(taskId));

        mCachedTrackers.remove(taskId);
    }

    private void getTrackersFromRemoteDataSource(@NonNull final LoadTrackersCallback callback) {
        mRemoteDataSource.getTrackers(new LoadTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {
                refreshCache(trackers);
                refreshLocalDataSource(trackers);
                callback.onTrackersLoaded(new ArrayList<>(mCachedTrackers.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Tracker> trackers) {
        if (mCachedTrackers == null) {
            mCachedTrackers = new LinkedHashMap<>();
        }
        mCachedTrackers.clear();
        for (Tracker tracker : trackers) {
            mCachedTrackers.put(tracker.getId(), tracker);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Tracker> trackers) {
        mLocalDataSource.deleteAllTrackers();
        for (Tracker tracker : trackers) {
            mLocalDataSource.saveTracker(tracker);
        }
    }

    @Nullable
    private Tracker getTrackerWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTrackers == null || mCachedTrackers.isEmpty()) {
            return null;
        } else {
            return mCachedTrackers.get(id);
        }
    }


}