package com.fraserbrooks.progresstracker.datasource;

import com.fraserbrooks.progresstracker.targets.domain.model.Target;
import com.fraserbrooks.progresstracker.datasource.source.TargetDataSource;
import com.fraserbrooks.progresstracker.datasource.source.utils.ApiResponse;
import com.fraserbrooks.progresstracker.datasource.source.utils.NetworkBoundResource;
import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

public class TargetRepository {

    private final String TAG = "TargetRepository";

    private static volatile TargetRepository INSTANCE = null;

    private final TargetDataSource mRemoteTargetSource;
    private final TargetDataSource mLocalTargetSource;

    private final AppExecutors mAppExecutors;

    // Prevent direct instantiation
    private TargetRepository(@NonNull TargetDataSource targetRemoteDataSource,
                             @NonNull TargetDataSource targetLocalDataSource,
                             @NonNull AppExecutors appExecutors) {
        mRemoteTargetSource = targetRemoteDataSource;
        mLocalTargetSource = targetLocalDataSource;
        mAppExecutors = appExecutors;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource the backend data source
     * @param localDataSource  the device storage data source
     * @param appExecutors     the app executors for networkIO/diskIO
     * @return the {@link TrackerRepository} instance
     */
    public static TargetRepository getInstance(@NonNull TargetDataSource remoteDataSource,
                                                @NonNull TargetDataSource localDataSource,
                                                @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new TargetRepository(remoteDataSource, localDataSource, appExecutors);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TargetDataSource, TargetDataSource, AppExecutors)}
     * to create a new instance next time it's called.
     */
    @SuppressWarnings("unused")
    public static void destroyInstance() {
        INSTANCE = null;
    }

    public LiveData<Resource<List<Target>>> getData(){

        return new NetworkBoundResource<List<Target>, List<Target>>(mAppExecutors){

            @Override
            protected void saveCallResult(@NonNull List<Target> item) {
                mLocalTargetSource.saveData(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Target> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Target>> loadFromDb() {
                return mLocalTargetSource.getData();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Target>>> createCall() {
                return null;
            }
        }.asLiveData();

    }

    public LiveData<Resource<Target>> getItem(String id){
        return new NetworkBoundResource<Target, Target>(mAppExecutors){

            @Override
            protected void saveCallResult(@NonNull Target item) {
                mLocalTargetSource.saveItem(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Target data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Target> loadFromDb() {
                return mLocalTargetSource.getItem(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Target>> createCall() {
                return null;
            }
        }.asLiveData();
    }

    public void deleteAllTargets() {
        mLocalTargetSource.deleteAllItems();
    }

    public void deleteTarget(@NonNull Target target){
        mLocalTargetSource.deleteItem(target);
    }

    public LiveData<Resource<List<Date>>> getDaysTargetMet(@NonNull String targetId, @NonNull Calendar month){
        return new NetworkBoundResource<List<Date>, List<Date>>(mAppExecutors){

            @Override
            protected void saveCallResult(@NonNull List<Date> item) {
                // do nothing
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Date> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Date>> loadFromDb() {
                return mLocalTargetSource.getDaysTargetMet(targetId, month);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Date>>> createCall() {
                return null;
            }
        }.asLiveData();
    }




}
