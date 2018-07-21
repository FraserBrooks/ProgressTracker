package com.fraserbrooks.progresstracker.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.Calendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Fraser on 07/04/2018.
 */

public class LocalDataSource implements DataSource{

    private static final String TAG = "LocalDataSource";

    private static volatile LocalDataSource INSTANCE;

    private TrackersDao mTrackersDao;
    private AppExecutors mAppExecutors;


    // Prevent direct instantiation.
    private LocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TrackersDao trackersDao) {
        mTrackersDao = trackersDao;
        mAppExecutors = appExecutors;
    }

    public static LocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TrackersDao trackersDao) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "getInstance: new instance created");
                    INSTANCE = new LocalDataSource(appExecutors, trackersDao);
                }
            }
        }
        return INSTANCE;
    }


    /**
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTrackers(@NonNull final GetTrackersCallback callback, boolean staggered) {
        List<Tracker> trackers = mTrackersDao.getTrackers();
        callback.onTrackersLoaded(trackers);
    }

    /**
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackersCallback callback) {

        Tracker tracker = mTrackersDao.getTrackerById(trackerId);

        if (tracker != null) {
            callback.onTrackerLoaded(tracker);
        } else {
            callback.onDataNotAvailable();
        }
    }


    @Override
    public boolean saveTracker(@NonNull final Tracker tracker) {
        checkNotNull(tracker);

        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.insertTracker(tracker);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
        return true;
    }

    @Override
    public void updateTracker(@NonNull final Tracker tracker) {
        mTrackersDao.updateTracker(tracker);
    }

    @Override
    public void refreshAllCache() {
        /** Not required because the {@link Repository} handles the logic of refreshing the
        / cache from all the available data sources.
        **/
    }

    @Override
    public void deleteAllTrackers() {
        mTrackersDao.deleteTrackers();
    }

    @Override
    public boolean deleteTracker(@NonNull final String trackerId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTrackerById(trackerId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
        return true;
    }

    /**
     * Note: {@link GetTargetsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTargets(@NonNull final GetTargetsCallback callback, boolean staggered) {
        List<Target> targets = mTrackersDao.getTargets();
        callback.onTargetsLoaded(targets);
    }


    /**
     * Note: {@link GetTargetsCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTarget(@NonNull final String targetId, @NonNull final GetTargetsCallback callback) {
        Target target = mTrackersDao.getTargetById(targetId);

        if (target != null) {
            callback.onTargetLoaded(target);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public boolean saveTarget(@NonNull final Target target) {
        checkNotNull(target);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.insertTarget(target);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
        return true;
    }

    @Override
    public void updateTarget(@NonNull final Target target) {
        mTrackersDao.updateTarget(target);
    }


    @Override
    public void deleteAllTargets() {
        mTrackersDao.deleteTargets();
    }

    @Override
    public boolean deleteTarget(@NonNull final String targetId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTargetById(targetId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
        return true;
    }

    @Override
    public List<ScoreEntry> getEntries() {
        //todo (implement callback)
        return mTrackersDao.getEntries();
    }

    @Override
    public void getDaysTargetWasMet(final String targetId1, final String targetId2, final String targetId3, final GetDaysTargetsMetCallback callback) {
        CalendarTriple calendars = new CalendarTriple();

        if (targetId1 != null) calendars.list1 = mTrackersDao.getDaysTargetCompleted(targetId1);
        if (targetId2 != null) calendars.list2 = mTrackersDao.getDaysTargetCompleted(targetId2);
        if (targetId3 != null) calendars.list3 = mTrackersDao.getDaysTargetCompleted(targetId3);

        callback.onDaysLoaded(calendars);
    }

    @Override
    public void deleteAllEntries() {
        mTrackersDao.deleteEntries();
    }

    @Override
    public void incrementScore(@NonNull final String trackerId, final int score) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.upsertScore(trackerId, score);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTrackerTotalScore(@NonNull final String trackerId, @NonNull final GetNumberCallback callback) {
        int num = mTrackersDao.getTotalScore(trackerId);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnDay(@NonNull final String trackerId, final Calendar day, @NonNull final GetNumberCallback callback) {
        int num = mTrackersDao.getScoreOnSpecificDay(trackerId, day);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnWeek(@NonNull final String trackerId, final Calendar week, @NonNull final GetNumberCallback callback) {
        int num = mTrackersDao.getScoreOnSpecificWeek(trackerId, week);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnMonth(@NonNull final String trackerId, final Calendar month, @NonNull final GetNumberCallback callback) {
        int num = mTrackersDao.getScoreOnSpecificMonth(trackerId, month);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnYear(@NonNull final String trackerId, final Calendar year, @NonNull final GetNumberCallback callback) {
        int num = mTrackersDao.getScoreOnSpecificYear(trackerId, year);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getTargetAverageCompletion(@NonNull final String targetId, @NonNull final GetNumberCallback callback) {

        Target t = mTrackersDao.getTargetById(targetId);
        if (!t.isRollingTarget()){
            callback.onNumberLoaded(-1);
        }

        int intervalPeriod;
        switch (t.getInterval()){
            case "DAY": intervalPeriod = Calendar.DAY_OF_YEAR;
                break;
            case "WEEK": intervalPeriod = Calendar.WEEK_OF_YEAR;
                break;
            case "MONTH": intervalPeriod = Calendar.MONTH;
                break;
            default:
                Log.e(TAG, "getTargetAverageCompletion: couldn't find match for interval: " + t.getInterval());
                intervalPeriod = Calendar.DAY_OF_YEAR;
        }

        Calendar now = Calendar.getInstance();
        Calendar startDate = t.getStartDate();

        now.setTimeZone(startDate.getTimeZone());

        float maxPossible = 0;
        while(now.compareTo(startDate) > 0){
            maxPossible += 1;
            now.add(intervalPeriod, -1);
        }
        Log.d(TAG, "getTargetAverageCompletion: " + targetId + " maxPoss calculated as " + maxPossible );

        int achieved = 0;
        now = Calendar.getInstance();

        switch (t.getInterval()){
            case "DAY": achieved = mTrackersDao.getCountScoresOverDayTarget(targetId, t.getNumberToAchieve(), startDate, now);
                break;
            case "WEEK": achieved = mTrackersDao.getCountScoresOverWeekTarget(targetId, t.getNumberToAchieve(), startDate, now);
                break;
            case "MONTH": achieved = mTrackersDao.getCountScoresOverMonthTarget(targetId, t.getNumberToAchieve(), startDate, now);
                break;
        }

        Log.d(TAG, "getTargetAverageCompletion: " + targetId + " achieved calculated as " + achieved);

        int avg = (int) (achieved/maxPossible * 100f);
        avg = (avg > 100) ? 100 : avg;
        callback.onNumberLoaded(avg);
    }


    @VisibleForTesting
    static void clearInstance(){
        INSTANCE = null;
    }


}
