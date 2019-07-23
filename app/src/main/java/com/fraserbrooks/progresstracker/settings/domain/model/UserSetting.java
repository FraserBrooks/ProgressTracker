package com.fraserbrooks.progresstracker.settings.domain.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.datasource.source.local.Converters;

@SuppressWarnings("unused")
@Entity(tableName = "settings")
public class UserSetting {

    public enum Setting {

        CALENDAR_TARGET_1,
        CALENDAR_TARGET_2,
        CALENDAR_TARGET_3

    }

    @PrimaryKey
    @ColumnInfo(name = "setting")
    @TypeConverters({Converters.class})
    private Setting mSetting;


    @NonNull
    @ColumnInfo(name = "settingvalue")
    private String mSettingValue;


    public UserSetting(@NonNull Setting setting,@NonNull String settingValue){

        this.mSetting = setting;
        this.mSettingValue = settingValue;

    }

    public Setting getSetting() {
        return mSetting;
    }

    @NonNull
    public String getSettingValue() {
        return mSettingValue;
    }



}
