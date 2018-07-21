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

    private final String TAG = "LocalDataScource";

    private static volatile LocalDataSource INSTANCE;

    private TrackersDao mTrackersDao;

    private AppExecutors mAppExecutors;


    // Prevent direct instantiation.
    private LocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TrackersDao trackersDao) {
        mAppExecutors = appExecutors;
        mTrackersDao = trackersDao;
    }

    public static LocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TrackersDao trackersDao) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
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
    public void getTrackers(final boolean runOnUiThread, @NonNull final GetTrackersCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Tracker> trackers = mTrackersDao.getTrackers();
                Runnable getTrackersRunnable = new Runnable() {
                    @Override
                    public void run() {
                        callback.onTrackersLoaded(trackers);
                    }
                };
                if (runOnUiThread) mAppExecutors.mainThread().execute(getTrackersRunnable);
                else getTrackersRunnable.run();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTracker(final boolean runOnUiThread, @NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Tracker tracker = mTrackersDao.getTrackerById(trackerId);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (tracker != null) {
                            callback.onTrackerLoaded(tracker);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                };
                if (runOnUiThread) mAppExecutors.mainThread().execute(runnable);
                else runnable.run();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
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
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.updateTracker(tracker);
            }
        };
        mAppExecutors.diskIO().execute(updateRunnable);
    }

    @Override
    public void refreshAllCache() {
        /** Not required because the {@link Repository} handles the logic of refreshing the
        / from all the available data sources.
        **/
    }

    @Override
    public void deleteAllTrackers() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTrackers();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public boolean deleteTracker(@NonNull final String trackerId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTrackerById(trackerId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
        return true;
    }

    /**
     * Note: {@link GetTargetsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTargets(final boolean runOnUiThread, @NonNull final GetTargetsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Target> targets = mTrackersDao.getTargets();
                Runnable callbackRunnable = new Runnable() {
                    @Override
                    public void run() {
                        callback.onTargetsLoaded(targets);
                    }
                };
                if(runOnUiThread) mAppExecutors.mainThread().execute(callbackRunnable);
                else callbackRunnable.run();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }


    /**
     * Note: {@link GetTargetCallback#onDataNotAvailable()} is fired if the {@link Tracker} isn't
     * found.
     */
    @Override
    public void getTarget(final boolean runOnUiThread, @NonNull final String targetId, @NonNull final GetTargetCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Target target = mTrackersDao.getTargetById(targetId);

                Runnable callbackRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (target != null) {
                            callback.onTargetLoaded(target);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                };
                if (runOnUiThread) mAppExecutors.mainThread().execute(callbackRunnable);
                else callbackRunnable.run();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
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
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.updateTarget(target);
            }
        };
        mAppExecutors.diskIO().execute(updateRunnable);
    }


    @Override
    public void deleteAllTargets() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTargets();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public boolean deleteTarget(@NonNull final String targetId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteTargetById(targetId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
        return true;
    }

    @Override
    public List<ScoreEntry> getEntries() {
        //todo (implement callback)
        return mTrackersDao.getEntries();
    }

    @Override
    public void getDaysTargetWasMet(final String targetId1, final String targetId2, final String targetId3, final GetDaysTargetsMetCallback callback) {
        Runnable getDaysRunnable = new Runnable() {
            @Override
            public void run() {
                final CalendarTriple cals = new CalendarTriple();

                if(targetId1 != null) cals.list1 = mTrackersDao.getDaysTargetCompleted(targetId1);
                if(targetId2 != null) cals.list2 = mTrackersDao.getDaysTargetCompleted(targetId2);
                if(targetId3 != null) cals.list3 = mTrackersDao.getDaysTargetCompleted(targetId3);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDaysLoaded(cals);
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(getDaysRunnable);
    }

    @Override
    public void deleteAllEntries() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.deleteEntries();
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void incrementScore(@NonNull final String trackerId, final int score) {
        Runnable incrementScoreRunnable = new Runnable() {
            @Override
            public void run() {
                mTrackersDao.upsertScore(trackerId, score);
            }
        };
        mAppExecutors.diskIO().execute(incrementScoreRunnable);
    }

    @Override
    public void getTrackerTotalScore(@NonNull final String trackerId, @NonNull final GetNumberCallback callback) {
        Runnable getTotalRunnable = new Runnable() {
            @Override
            public void run() {
                final int num = mTrackersDao.getTotalScore(trackerId);
                Runnable callbackRunnable = new Runnable() {
                    @Override
                    public void run() {
                        callback.onNumberLoaded(num);
                    }
                };
                mAppExecutors.mainThread().execute(callbackRunnable);
            }
        };
        mAppExecutors.diskIO().execute(getTotalRunnable);
    }

    @Override
    public void getScoreOnDay(@NonNull final String trackerId, final Calendar day, @NonNull final GetNumberCallback callback) {
        final int num = mTrackersDao.getScoreOnSpecificDay(trackerId, day);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnWeek(@NonNull final String trackerId, final Calendar week, @NonNull final GetNumberCallback callback) {
        final int num = mTrackersDao.getScoreOnSpecificWeek(trackerId, week);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnMonth(@NonNull final String trackerId, final Calendar month, @NonNull final GetNumberCallback callback) {
        final int num = mTrackersDao.getScoreOnSpecificMonth(trackerId, month);
        callback.onNumberLoaded(num);
    }

    @Override
    public void getScoreOnYear(@NonNull final String trackerId, final Calendar year, @NonNull final GetNumberCallback callback) {
        final int num = mTrackersDao.getScoreOnSpecificYear(trackerId, year);
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
