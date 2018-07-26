package com.fraserbrooks.progresstracker.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */

public interface DataSource {


    interface GetTrackersCallback {

        // Use one or the other but not both
        void onTrackersLoaded(List<Tracker> trackers);
        void onTrackerLoaded(Tracker tracker);

        void onDataNotAvailable();
    }


    interface GetTargetsCallback {

        // Use one or the other but not both
        void onTargetsLoaded(List<Target> targets);
        void onTargetLoaded(Target target);

        void onDataNotAvailable();
    }

    interface GetNumberCallback {

        void onNumberLoaded(Integer number);

        void onDataNotAvailable();

    }

    class CalendarTriple{
        public List<Calendar> list1;
        public List<Calendar> list2;
        public List<Calendar> list3;
    }

    interface GetDaysTargetsMetCallback{

        void onDaysLoaded(CalendarTriple calendars);

        void onDataNotAvailable();
    }

    void getTrackers(@NonNull GetTrackersCallback callback, boolean staggeredLoad);

    void getTracker(@NonNull String trackerId, @NonNull GetTrackersCallback callback);

    boolean saveTracker(@NonNull Tracker tracker);

    void saveTrackers(@NonNull List<Tracker> trackers);

    void updateTracker(@NonNull Tracker tracker);

    void refreshAllCache();

    void deleteAllTrackers();

    boolean deleteTracker(@NonNull String trackerId);

    void getTargets(@NonNull GetTargetsCallback callback, boolean staggeredLoad);

    void getTarget(@NonNull String targetId, @NonNull GetTargetsCallback callback);

    boolean saveTarget(@NonNull Target target);

    void saveTargets(@NonNull List<Target> targets);

    void updateTarget(@NonNull Target target);

    void deleteAllTargets();

    boolean deleteTarget(@NonNull String targetId);

    List<ScoreEntry> getEntries();

    void saveEntries(List<ScoreEntry> entries);

    void getDaysTargetsMet(@Nullable String targetId1, @Nullable String targetId2, @Nullable String targetId3, Calendar range,
                           GetDaysTargetsMetCallback callback);

    void deleteAllEntries();

    // Increment Score (int)
    void incrementScore(@NonNull String trackerId, int score);
    //
    // Get total score
    void getTrackerTotalScore(@NonNull String trackerId, @NonNull GetNumberCallback callback);
    //
    // Get a tracker score for a particular day
    void getScoreOnDay(@NonNull String trackerId, Calendar day, @NonNull GetNumberCallback callback);
    //
    // Get a tracker score for a particular day
    void getScoreOnWeek(@NonNull String trackerId, Calendar week, @NonNull GetNumberCallback callback);
    //
    // Get a tracker score for a particular day
    void getScoreOnMonth(@NonNull String trackerId, Calendar month, @NonNull GetNumberCallback callback);
    //
    // Get a tracker score for a particular day
    void getScoreOnYear(@NonNull String trackerId, Calendar year, @NonNull GetNumberCallback callback);
    //
    // Get average score
    void getTargetAverageCompletion(@NonNull String targetId, @NonNull GetNumberCallback callback);
    //

//    void getTrackerScores(@NonNull GetTotalsCallback callback);





}