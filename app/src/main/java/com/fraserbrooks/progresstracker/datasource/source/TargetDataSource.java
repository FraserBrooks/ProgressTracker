package com.fraserbrooks.progresstracker.datasource.source;

import com.fraserbrooks.progresstracker.targets.domain.model.Target;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public interface TargetDataSource extends BaseDataSource<Target> {

    LiveData<List<Date>> getDaysTargetMet(@NonNull String targetId, @NonNull final Calendar month);


}
