package com.fraserbrooks.progresstracker.datasource;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.datasource.source.TrackerDataSource;
import com.fraserbrooks.progresstracker.datasource.source.utils.ApiResponse;
import com.fraserbrooks.progresstracker.datasource.source.utils.NetworkBoundResource;
import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TrackerRepository {

    private final String TAG = "TrackerRepository";

    private static volatile TrackerRepository INSTANCE = null;

    private final TrackerDataSource mRemoteTrackerSource;
    private final TrackerDataSource mLocalTrackerSource;

    private final NetworkBoundResource<List<Tracker>, List<Tracker>> mData;

    private final AppExecutors mAppExecutors;


    // Prevent direct instantiation.
    private TrackerRepository(@NonNull TrackerDataSource trackerRemoteDataSource,
                              @NonNull TrackerDataSource trackerLocalDataSource,
                              @NonNull AppExecutors appExecutors) {

        mRemoteTrackerSource = checkNotNull(trackerRemoteDataSource);
        mLocalTrackerSource = checkNotNull(trackerLocalDataSource);
        mAppExecutors = checkNotNull(appExecutors);

        mData = new NetworkBoundResource<List<Tracker>, List<Tracker>>(mAppExecutors){

            @Override
            protected void saveCallResult(@NonNull List<Tracker> item) {
                mLocalTrackerSource.saveData(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Tracker> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Tracker>> loadFromDb() {
                return mLocalTrackerSource.getData();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Tracker>>> createCall() {
                return null;
            }
        };

    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource the backend data source
     * @param localDataSource  the device storage data source
     * @param appExecutors     the app executors for networkIO/diskIO
     * @return the {@link TrackerRepository} instance
     */
    public static TrackerRepository getInstance(@NonNull TrackerDataSource remoteDataSource,
                                                @NonNull TrackerDataSource localDataSource,
                                                @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new TrackerRepository(remoteDataSource, localDataSource, appExecutors);
        }
        return INSTANCE;
    }
    /**
     * Used to force {@link #getInstance(TrackerDataSource, TrackerDataSource, AppExecutors)}
     * to create a new instance next time it's called.
     */
    @SuppressWarnings("unused")
    public static void destroyInstance() {
        INSTANCE = null;
    }



    public LiveData<Resource<List<Tracker>>> getData() {
        return mData.asLiveData();
    }

    public LiveData<Resource<Tracker>> getItem(String id) {
        return new NetworkBoundResource<Tracker, Tracker>(mAppExecutors){

            @Override
            protected void saveCallResult(@NonNull Tracker item) {
                mLocalTrackerSource.saveItem(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Tracker data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Tracker> loadFromDb() {
                return mLocalTrackerSource.getItem(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Tracker>> createCall() {
                return null;
            }
        }.asLiveData();
    }


    public void incrementTracker(@NonNull String trackerId, int score) {
        Log.d(TAG, "asdff incrementTracker:");
        mLocalTrackerSource.incrementTracker(trackerId, score);
    }


    public void clearWeek(String trackerId) {

    }


    public void clearMonth(String trackerId) {

    }


    public void saveData(@NonNull List<Tracker> data) {

    }


    public boolean saveItem(@NonNull Tracker item) {
        return false;
    }


    public void updateItem(@NonNull Tracker item) {
        mLocalTrackerSource.updateItem(item);
    }


    public void deleteItem(@NonNull Tracker item) {

    }


    public void deleteAllItems() {

    }
}
