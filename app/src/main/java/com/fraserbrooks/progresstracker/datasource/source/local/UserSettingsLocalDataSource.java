package com.fraserbrooks.progresstracker.datasource.source.local;

import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;
import com.fraserbrooks.progresstracker.datasource.source.UserSettingDataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class UserSettingsLocalDataSource implements UserSettingDataSource {

    private static final String TAG = "UserSettingsLocalSource";

    private static volatile UserSettingsLocalDataSource INSTANCE;

    private UserSettingsDao mUserSettingsDao;
    private AppExecutors   mAppExecutors;


    // Prevent direct instantiation
    private UserSettingsLocalDataSource(@NonNull AppExecutors appExecutors,
                                        @NonNull UserSettingsDao settingDao){
        mAppExecutors = appExecutors;
        mUserSettingsDao = settingDao;
    }

    public static UserSettingsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                          @NonNull UserSettingsDao settingDao){
        if(INSTANCE == null){
            INSTANCE = new UserSettingsLocalDataSource(appExecutors, settingDao);
        }
        return INSTANCE;
    }

    @Deprecated // Not needed
    @Override
    public MediatorLiveData<List<UserSetting>> getData() {
        return null;
    }

    @Deprecated // Not usable as we need setting
    @Override
    public MediatorLiveData<UserSetting> getItem(String id) {
        return null;
    }

    @Override
    public LiveData<String> getSetting(UserSetting.Setting setting) {
        return mUserSettingsDao.getSettingValue(setting);
    }

    @Override
    public void setSetting(UserSetting.Setting setting, String value) {
        mAppExecutors.diskIO().execute(() ->
                mUserSettingsDao.insertItemOnConflictReplace(new UserSetting(setting,value)));
    }

    @Override
    public void saveData(@NonNull List<UserSetting> data) {
        mAppExecutors.diskIO().execute(() ->  mUserSettingsDao.insertItems(data));
    }

    @Override
    public void saveItem(@NonNull UserSetting item) {
        mAppExecutors.diskIO().execute(() -> mUserSettingsDao.insertItemOnConflictReplace(item));
    }

    @Override
    public void updateItem(@NonNull UserSetting item) {
        mAppExecutors.diskIO().execute(() -> mUserSettingsDao.updateItem(item));
    }

    @Override
    public void deleteItem(@NonNull UserSetting item) {
        mAppExecutors.diskIO().execute(() -> mUserSettingsDao.deleteItem(item));
    }

    @Override
    public void deleteAllItems() {
        mAppExecutors.diskIO().execute(() -> mUserSettingsDao.deleteAllSettings() );
    }


}
