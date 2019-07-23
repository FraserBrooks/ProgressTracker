package com.fraserbrooks.progresstracker.datasource.source;

import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;

import androidx.lifecycle.LiveData;

public interface UserSettingDataSource extends BaseDataSource<UserSetting> {

    LiveData<String> getSetting(UserSetting.Setting setting);

    void setSetting(UserSetting.Setting setting, String value);

}
