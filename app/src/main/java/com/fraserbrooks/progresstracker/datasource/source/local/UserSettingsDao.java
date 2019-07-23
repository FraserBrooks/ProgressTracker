package com.fraserbrooks.progresstracker.datasource.source.local;

import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.TypeConverters;
@Dao
public abstract class UserSettingsDao implements BaseDao<UserSetting> {

    private static final String TAG = "UserSettingsDao";

    /**
     * Get a setting value
     *
     * @param setting
     * @return live data containing the current setting
     */
    @Query("SELECT settingvalue FROM settings WHERE setting = :setting")
    @TypeConverters({Converters.class})
    public abstract LiveData<String> getSettingValue(UserSetting.Setting setting);

    /**
     * Delete all settings.
     */
    @Query("DELETE FROM settings")
    abstract void deleteAllSettings();

}
