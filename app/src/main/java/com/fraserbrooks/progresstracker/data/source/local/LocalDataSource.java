package com.fraserbrooks.progresstracker.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.UserSetting;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void getTrackers(@NonNull final GetTrackersCallback callback) {

        Runnable runnable = () -> {
            final List<Tracker> trackers = mTrackersDao.getTrackers();

            if(trackers != null){
                for(Tracker tracker : trackers){
                    prepareTrackerForCache(tracker);
                }
            }

            mAppExecutors.mainThread().execute(() -> {
                if(trackers == null || trackers.isEmpty()) callback.onDataNotAvailable();
                else callback.onTrackersLoaded(trackers);
            });
        };
        mAppExecutors.diskIO().execute(runnable);
    }


    /**
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {

        Runnable runnable = () -> {

            final Tracker tracker = mTrackersDao.getTrackerById(trackerId);
            if(tracker != null) prepareTrackerForCache(tracker);

            mAppExecutors.mainThread().execute(() -> {
                if (tracker != null) {
                    callback.onTrackerLoaded(tracker);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    private void prepareTrackerForCache(Tracker tracker) {
        int total = mTrackersDao.getTotalScore(tracker.getId());
        tracker.setScoreSoFar(total);
        tracker.setUiValues();

        int[] pastEightDays = new int[8];
        int[] pastEightWeeks = new int[8];
        int[] pastEightMonths = new int[8];

        Calendar calendar = Calendar.getInstance();

        for(int i = 0; i < 8; i++){

            // Get recent day values for cache
            calendar.add(Calendar.DAY_OF_YEAR, -(7-i));
            pastEightDays[i] = mTrackersDao.getScoreOnSpecificDay(tracker.getId(),
                    calendar);
            calendar.add(Calendar.DAY_OF_YEAR, (7-i));

            // Get recent week values for cache
            calendar.add(Calendar.WEEK_OF_YEAR, -(7-i));
            pastEightWeeks[i] = mTrackersDao.getScoreOnSpecificWeek(tracker.getId(),
                    calendar);
            calendar.add(Calendar.WEEK_OF_YEAR, (7-i));

            // Get recent month values for cache
            calendar.add(Calendar.MONTH, -(7-i));
            pastEightMonths[i] = mTrackersDao.getScoreOnSpecificMonth(tracker.getId(),
                    calendar);
            calendar.add(Calendar.MONTH, (7-i));
        }

        tracker.setPastEightDaysCounts(pastEightDays);
        tracker.setPastEightWeekCounts(pastEightWeeks);
        tracker.setPastEightMonthCounts(pastEightMonths);
    }

    @Override
    public boolean saveTracker(@NonNull final Tracker tracker) {
        checkNotNull(tracker);

        Runnable saveRunnable = () -> mTrackersDao.insertTracker(tracker);
        mAppExecutors.diskIO().execute(saveRunnable);
        return true;
    }

    @Override
    public void saveTrackers(@NonNull final List<Tracker> trackers) {
        checkNotNull(trackers);

        Runnable saveRunnable = () -> {
            for(Tracker t : trackers){
                mTrackersDao.insertTracker(t);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);

    }

    @Override
    public void updateTracker(@NonNull final Tracker tracker) {
        Runnable updateRunnable = () -> mTrackersDao.updateTracker(tracker);
        mAppExecutors.diskIO().execute(updateRunnable);
    }


    /** Not required because the repository handles the logic of refreshing the
     / cache from all the available data sources.
     **/
    @Override
    public void refreshAllCache() {

    }

    @Override
    public void deleteAllTrackers() {
        Runnable deleteRunnable = mTrackersDao::deleteTrackers;
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public boolean deleteTracker(@NonNull final String trackerId) {
        checkNotNull(trackerId);
        Runnable runnable = () -> mTrackersDao.deleteTrackerById(trackerId);
        mAppExecutors.diskIO().execute(runnable);
        return true;
    }

    /**
     * Note: {@link GetTargetsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTargets(@NonNull final GetTargetsCallback callback) {

        Runnable runnable = () -> {
            final List<Target> targets = mTrackersDao.getTargets();

            if(targets != null){
                for(Target target : targets) prepareTargetForCache(target);
            }

            mAppExecutors.mainThread().execute(() -> {
                if (targets == null || targets.isEmpty()) callback.onDataNotAvailable();
                else callback.onTargetsLoaded(targets);
            });
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetTargetsCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTarget(@NonNull final String targetId, @NonNull final GetTargetCallback callback) {

        Runnable runnable = () -> {

            final Target target = mTrackersDao.getTargetById(targetId);
            if(target != null) prepareTargetForCache(target);

            mAppExecutors.mainThread().execute(() -> {
                if (target != null) {
                    callback.onTargetLoaded(target);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.diskIO().execute(runnable);

    }


    private void prepareTargetForCache(Target target){

        Tracker tracker = mTrackersDao.getTrackerById(target.getTrackId());

        if(tracker != null) target.setTrackerName(tracker.getTitle());
        else target.setTrackerName("ERROR");

        int average = getTargetAverage(target);
        target.setAverageOverTime(average);

        int score = 0;

        Calendar calendar = Calendar.getInstance();

        if (target.isRollingTarget()) {
            switch (target.getInterval()) {
                case Target.EVERY_DAY:
                    score = mTrackersDao.getScoreOnSpecificDay(target.getTrackId(), calendar);
                    break;
                case Target.EVERY_WEEK:
                    score = mTrackersDao.getScoreOnSpecificWeek(target.getTrackId(), calendar);
                    break;
                case Target.EVERY_MONTH:
                    score = mTrackersDao.getScoreOnSpecificMonth(target.getTrackId(), calendar);
                    break;
                case Target.EVERY_YEAR:
                    score = mTrackersDao.getScoreOnSpecificYear(target.getTrackId(), calendar);
                    break;
                default:
                    // Should never happen
                    break;
            }
        }

        int percentage = (score * 100) /target.getNumberToAchieve();
        percentage = (percentage > 100) ? 100 : percentage;

        target.setCurrentProgressPercentage(percentage);

    }

    @Override
    public boolean saveTarget(@NonNull final Target target) {
        checkNotNull(target);
        Runnable saveRunnable = () -> mTrackersDao.insertTarget(target);
        mAppExecutors.diskIO().execute(saveRunnable);
        return true;
    }

    @Override
    public void saveTargets(@NonNull final List<Target> targets) {
        checkNotNull(targets);

        Runnable saveRunnable = () -> {
            for(Target t : targets) mTrackersDao.insertTarget(t);
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void updateTarget(@NonNull final Target target) {
        checkNotNull(target);

        Runnable updateRunnable = () -> mTrackersDao.updateTarget(target);
        mAppExecutors.diskIO().execute(updateRunnable);
    }


    @Override
    public void deleteAllTargets() {
        Runnable deleteRunnable = mTrackersDao::deleteTargets;
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public boolean deleteTarget(@NonNull final String targetId) {
        Runnable runnable = () -> mTrackersDao.deleteTargetById(targetId);
        mAppExecutors.diskIO().execute(runnable);
        return true;
    }


    @Override
    public void getEntries(@NonNull final GetEntriesCallback callback) {

        Runnable runnable = () -> {
            final List<ScoreEntry> entries = mTrackersDao.getEntries();
            mAppExecutors.mainThread().execute(() -> {
                if(entries == null || entries.isEmpty()) callback.onDataNotAvailable();
                else callback.onEntriesLoaded(entries);
            });
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    @Override
    public void saveEntries(@NonNull final List<ScoreEntry> entries) {

        Runnable saveRunnable = () -> {
            for(ScoreEntry e :  entries) mTrackersDao.insertEntry(e);
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAllEntries() {
        Runnable deleteRunnable = mTrackersDao::deleteEntries;
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void setSetting(@NonNull UserSetting.Setting setting,@NonNull String value) {
        Runnable runnable = () -> mTrackersDao.insertUserSetting(new UserSetting(setting, value));
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSettingValue(UserSetting.Setting setting, GetSettingCallback callback) {
        Runnable runnable = () -> {
            String settingValue = mTrackersDao.getSettingValue(setting);
            mAppExecutors.mainThread().execute(() -> callback.onSettingLoaded(settingValue));
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getDaysTargetMet(@NonNull String targetId, @NonNull final Calendar month, @NonNull final GetDaysTargetMetCallback callback) {

         Runnable runnable = () -> {
             Calendar twoAndAHalfMonthsAgo = Calendar.getInstance();
             Calendar twoAndAHalfMonthsAhead = Calendar.getInstance();

             twoAndAHalfMonthsAgo.setTime(month.getTime());
             twoAndAHalfMonthsAgo.add(Calendar.MONTH, -2);
             twoAndAHalfMonthsAgo.add(Calendar.WEEK_OF_YEAR, -2);
             twoAndAHalfMonthsAgo.set(Calendar.DAY_OF_MONTH, twoAndAHalfMonthsAgo.getActualMinimum(Calendar.DAY_OF_MONTH));

             twoAndAHalfMonthsAhead.setTime(month.getTime());
             twoAndAHalfMonthsAhead.add(Calendar.MONTH, 2);
             twoAndAHalfMonthsAhead.add(Calendar.WEEK_OF_YEAR, 2);
             twoAndAHalfMonthsAhead.set(Calendar.DAY_OF_MONTH, twoAndAHalfMonthsAhead.getActualMaximum(Calendar.DAY_OF_MONTH));

             Set<Date> dates = new HashSet<>(mTrackersDao.getDaysTargetCompleted(targetId, twoAndAHalfMonthsAgo, twoAndAHalfMonthsAhead));

             mAppExecutors.mainThread().execute(() -> {
                 if(dates.isEmpty()) callback.onDataNotAvailable();
                 else callback.onDaysLoaded(dates);
             });
         };
         mAppExecutors.diskIO().execute(runnable);

    }


    @Override
    public void incrementTracker(@NonNull final String trackerId, final int score) {
        Runnable runnable = () -> mTrackersDao.upsertScore(trackerId, score);
        mAppExecutors.diskIO().execute(runnable);
    }


    private int getTargetAverage(Target target) {

        if (target == null || !target.isRollingTarget()){
            return -1;
        }

        int intervalPeriod;
        switch (target.getInterval()){
            case Target.EVERY_DAY: intervalPeriod = Calendar.DAY_OF_YEAR;
                break;
            case Target.EVERY_WEEK: intervalPeriod = Calendar.WEEK_OF_YEAR;
                break;
            case Target.EVERY_MONTH: intervalPeriod = Calendar.MONTH;
                break;
            case Target.EVERY_YEAR: intervalPeriod = Calendar.YEAR;
                break;
            default:
                Log.e(TAG, "getTargetAverageCompletion: couldn't find match for interval: " + target.getInterval());
                intervalPeriod = Calendar.DAY_OF_YEAR;
        }

        Calendar now = Calendar.getInstance();
        Calendar startDate = target.getStartDate();

        now.setTimeZone(startDate.getTimeZone());

        float maxPossible = 0;
        while(now.compareTo(startDate) > 0){
            maxPossible += 1;
            now.add(intervalPeriod, -1);
        }

        int achieved = 0;
        now = Calendar.getInstance();

        switch (target.getInterval()){
            case Target.EVERY_DAY: achieved = mTrackersDao.getCountScoresOverDayTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_WEEK: achieved = mTrackersDao.getCountScoresOverWeekTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_MONTH: achieved = mTrackersDao.getCountScoresOverMonthTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_YEAR: achieved = mTrackersDao.getCountScoresOverYearTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
        }
        int avg = (int) (achieved/maxPossible * 100f);
        avg = (avg > 100) ? 100 : avg;
        return avg;
    }


    @VisibleForTesting
    static void clearInstance(){
        INSTANCE = null;
    }


}
