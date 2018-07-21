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

    // todo: parameterize these with <T>

    interface GetTrackersCallback {

        void onTrackersLoaded(List<Tracker> trackers);

        void onDataNotAvailable();
    }

    interface GetTrackerCallback {

        void onTrackerLoaded(Tracker tracker);

        void onDataNotAvailable();
    }


    interface GetTargetsCallback {

        void onTargetsLoaded(List<Target> targets);

        void onDataNotAvailable();
    }

    interface GetTargetCallback {

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

    interface GetDayTargetNamesCallback{

        void onNamesLoaded(List<String> names);

        void onDataNotAvailable();
    }

    void getTrackers(boolean runOnUiThread, @NonNull GetTrackersCallback callback);

    void getTracker(boolean runOnUiThread, @NonNull String trackerId, @NonNull GetTrackerCallback callback);

    boolean saveTracker(@NonNull Tracker tracker);

    void updateTracker(@NonNull Tracker tracker);

    void refreshAllCache();

    void deleteAllTrackers();

    boolean deleteTracker(@NonNull String trackerId);

    void getTargets(boolean runOnUiThread, @NonNull GetTargetsCallback callback);

    void getTarget(boolean runOnUiThread, @NonNull String targetId, @NonNull GetTargetCallback callback);


//    void getDayTargetNames(@NonNull GetDayTargetNamesCallback callback);

    boolean saveTarget(@NonNull Target target);

    void updateTarget(@NonNull Target target);

    void deleteAllTargets();

    boolean deleteTarget(@NonNull String targetId);

    List<ScoreEntry> getEntries();

    void getDaysTargetWasMet(@Nullable String targetId1,@Nullable String targetId2,@Nullable String targetId3,
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