package com.fraserbrooks.progresstracker.data.source;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */

public interface DataSource {

    interface LoadTrackersCallback {

        void onTrackersLoaded(List<Tracker> trackers);

        void onDataNotAvailable();
    }

    interface GetTrackerCallback {

        void onTrackerLoaded(Tracker tracker);

        void onDataNotAvailable();
    }


//    interface LoadTargetsCallback {
//
//        void onTargetsLoaded(List<Target> targets);
//
//        void onDataNotAvailable();
//    }
//
//    interface GetTargetCallback {
//
//        void onTaskLoaded(Target target);
//
//        void onDataNotAvailable();
//    }

    void getTrackers(@NonNull LoadTrackersCallback callback);

    void getTracker(@NonNull String trackerId, @NonNull GetTrackerCallback callback);

    void saveTracker(@NonNull Tracker tracker);

    void refreshTrackers();

    void deleteAllTrackers();

    void deleteTracker(@NonNull String trackerId);

}