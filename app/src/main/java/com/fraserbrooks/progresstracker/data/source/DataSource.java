package com.fraserbrooks.progresstracker.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.UserSetting;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Fraser on 07/04/2018.
 */

public interface DataSource {


    // --------------------------------------------------------
    // Callbacks  ---------------------------------------------
    interface GetTrackerCallback {

        void onTrackerLoaded(Tracker tracker);

        void onDataNotAvailable();
    }

    interface GetTrackersCallback{

        void onTrackersLoaded(List<Tracker> trackers);

        void onDataNotAvailable();
    }


    interface GetTargetsCallback {

        void onTargetsLoaded(List<Target> targets);

        void onDataNotAvailable();
    }

    interface GetTargetCallback{

        void onTargetLoaded(Target target);

        void onDataNotAvailable();

    }

    interface GetEntriesCallback{

        void onEntriesLoaded(List<ScoreEntry> entries);

        void onDataNotAvailable();

    }

    interface GetDaysTargetMetCallback {

        void onDaysLoaded(Set<Date> successfulDays);

        void onDataNotAvailable();

    }

    interface GetNumberCallback {

        void onNumberLoaded(Integer number);

    }

    interface GetSettingCallback {

        void onSettingLoaded(@Nullable String string);

    }
    //-----------------------------------------------------------------------------

    // ----------------------------------------------------------------------------
    // Listeners ------------------------------------------------------------------
    interface DeleteTargetListener{

        boolean isActive();

        void targetDeleted(Target targetToDelete);

    }

    interface DeleteTrackerListener{

        boolean isActive();

        void trackerDeleted(Tracker trackerToDelete);

    }

    interface UpdateOrAddTargetListener {
        boolean isActive();

        void targetUpdated(Target targetToUpdate);
    }

    interface UpdateOrAddTrackerListener {

        boolean isActive();

        void trackerUpdated(Tracker tracker);

    }

    interface TargetDateUpdateListener {

        boolean isActive();

        void updateInCalendar(Date date);

    }

    // -----------------------------------------------------------------------------

    void refreshAllCache();

    // -------------------------------------------------------------------------------------------
    // Trackers ----------------------------------------------------------------------------------

    void getTrackers(@NonNull GetTrackersCallback callback);

    void getTracker(@NonNull String trackerId, @NonNull GetTrackerCallback callback);

    boolean saveTracker(@NonNull Tracker tracker);

    void saveTrackers(@NonNull List<Tracker> trackers);

    void updateTracker(@NonNull Tracker tracker);

    void deleteAllTrackers();

    boolean deleteTracker(@NonNull String trackerId);

    void incrementTracker(@NonNull String trackerId, int score);
    //
    // -------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    // Targets -----------------------------------------------------------------------------------

    void getTargets(@NonNull GetTargetsCallback callback);

    void getTarget(@NonNull String targetId, @NonNull GetTargetCallback callback);

    boolean saveTarget(@NonNull Target target);

    void saveTargets(@NonNull List<Target> targets);

    void updateTarget(@NonNull Target target);

    void deleteAllTargets();

    boolean deleteTarget(@NonNull String targetId);

    void getDaysTargetMet(String targetId, @NonNull Calendar month, @NonNull GetDaysTargetMetCallback callback);

    // ------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    // Score entries -----------------------------------------------------------------------------

    void getEntries(@NonNull GetEntriesCallback callback);

    void saveEntries(@NonNull List<ScoreEntry> entries);

    void deleteAllEntries();

    // -------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------
    // User Settings

    void setSetting(@NonNull UserSetting.Setting setting, @NonNull String value);

    void getSettingValue(UserSetting.Setting setting, GetSettingCallback callback);


}