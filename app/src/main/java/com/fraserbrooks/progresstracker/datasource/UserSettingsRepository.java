package com.fraserbrooks.progresstracker.datasource;

import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;
import com.fraserbrooks.progresstracker.datasource.source.UserSettingDataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import androidx.annotation.NonNull;

public class UserSettingsRepository {

    private final String TAG = "UserSettingsRepository";

    private static volatile UserSettingsRepository INSTANCE;

    private final UserSettingDataSource mRemoteDataSource;
    private final UserSettingDataSource mLocalDataSource;

    private final AppExecutors mAppExecutors;


    // Prevent direct instantiation
    private UserSettingsRepository(@NonNull UserSettingDataSource remoteDataSource,
                                   @NonNull UserSettingDataSource localDataSource,
                                   @NonNull AppExecutors appExecutors){
        mAppExecutors = appExecutors;
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }


    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource the backend data source
     * @param localDataSource  the device storage data source
     * @param appExecutors     the app executors for networkIO/diskIO
     * @return the {@link TrackerRepository} instance
     */
    public static UserSettingsRepository getInstance(@NonNull UserSettingDataSource remoteDataSource,
                                                     @NonNull UserSettingDataSource localDataSource,
                                                     @NonNull AppExecutors appExecutors){
        if(INSTANCE == null){
            INSTANCE = new UserSettingsRepository(remoteDataSource, localDataSource, appExecutors);
        }
        return INSTANCE;
    }

    public void setSetting(@NonNull UserSetting.Setting setting, @NonNull String value){
        mLocalDataSource.setSetting(setting, value);
    }


}
