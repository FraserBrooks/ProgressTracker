package com.fraserbrooks.progresstracker.datasource.source;

import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

public interface TrackerDataSource extends BaseDataSource<Tracker> {

    /**
     *  Trackers  -------------------------------------------------------------------------
     */

    void incrementTracker(@NonNull String trackerId, int score);

    void clearWeek(String trackerId);

    void clearMonth(String trackerId);


}
