package com.fraserbrooks.progresstracker.datasource.source.local;

import android.os.Looper;
import android.util.Log;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.datasource.source.TrackerDataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class TrackersLocalDataSource implements TrackerDataSource {

    private static final String TAG = "TrackersLocalDataSource";

    private static volatile TrackersLocalDataSource INSTANCE;

    private TrackersDao mTrackersDao;
    private AppExecutors mAppExecutors;

    private MediatorLiveData<List<Tracker>> mProcessedData;
    private MediatorLiveData<Tracker> mProcessedItem;
    private LiveData<Tracker> mOldItem;

    // Prevent direct instantiation
    private TrackersLocalDataSource(@NonNull AppExecutors appExecutors,
                                    @NonNull TrackersDao trackersDao){

        mAppExecutors = appExecutors;
        mTrackersDao = trackersDao;

        mProcessedData = new MediatorLiveData<>();
        mProcessedData.addSource(mTrackersDao.getTrackers(), trackers -> {
            Log.d(TAG, "TrackersLocalDataSource: asdff processing data");
            mAppExecutors.diskIO().execute(() -> {

                Log.d(TAG, "asdff getData: persisted data change detected");

                if (Looper.getMainLooper() == Looper.myLooper())
                    Log.d(TAG, "asdff getData: runningOnMainThread");
                else Log.d(TAG, "asdff getData: runningOffMainThread");

                for (Tracker t : trackers) processTracker(t);
                mProcessedData.postValue(trackers);
            });
        });

        mProcessedItem = new MediatorLiveData<>();


    }

    public static TrackersLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                      @NonNull TrackersDao trackersDao){
        if(INSTANCE == null){
            synchronized (TrackersLocalDataSource.class){
                if(INSTANCE == null){
                    INSTANCE = new TrackersLocalDataSource(appExecutors, trackersDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void incrementTracker(@NonNull String trackerId, int score) {
        Log.d(TAG, "asdff : incrementTracker:");
        mAppExecutors.diskIO().execute(() -> mTrackersDao.insertOrIncrementScore(trackerId, score));
    }

    @Override
    public void clearWeek(String trackerId) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.clearWeek(trackerId));
    }

    @Override
    public void clearMonth(String trackerId) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.clearMonth(trackerId));
    }

    @Override
    public LiveData<List<Tracker>> getData() {
        return mProcessedData;
    }

    @Override
    public LiveData<Tracker> getItem(String id) {

        LiveData<Tracker> persistedItem = mTrackersDao.getTrackerById(id);

        if(mOldItem != null) mProcessedItem.removeSource(mOldItem);
        mProcessedItem.addSource(persistedItem, t -> {
            mAppExecutors.diskIO().execute(() -> {
                processTracker(t);
                mProcessedItem.postValue(t);
            });
        });

        mOldItem = persistedItem;

        return mProcessedItem;
    }

    private void processTracker(Tracker tracker) {

        // TODO: shouldn't need this
        int total = mTrackersDao.getTrackerTotalScore(tracker.getId());
        tracker.setScoreSoFar(total);

        tracker.setUiValues();

        int[] pastEightDays = new int[8];
        int[] pastEightWeeks = new int[8];
        int[] pastEightMonths = new int[8];

        Calendar day = Calendar.getInstance();
        Calendar week = (Calendar) day.clone();
        Calendar month = (Calendar) day.clone();

        for (int i = 0; i < 8; i++) {

            // Get recent day values for cache
            day.add(Calendar.DAY_OF_YEAR, -(7 - i));
            pastEightDays[i] = mTrackersDao.getScoreOnSpecificDay(tracker.getId(), day);
            day.add(Calendar.DAY_OF_YEAR, (7 - i));

            // Get recent week values for cache
            week.add(Calendar.DAY_OF_YEAR, 7 * (-(7 - i)));
            pastEightWeeks[i] = mTrackersDao.getScoreOnSpecificWeek(tracker.getId(), week);
            week.add(Calendar.DAY_OF_YEAR, 7 * ((7 - i)));

            // Get recent month values for cache
            month.add(Calendar.MONTH, -(7 - i));
            pastEightMonths[i] = mTrackersDao.getScoreOnSpecificMonth(tracker.getId(), month);
            month.add(Calendar.MONTH, (7 - i));
        }

        tracker.setPastEightDaysCounts(pastEightDays);
        tracker.setPastEightWeekCounts(pastEightWeeks);
        tracker.setPastEightMonthCounts(pastEightMonths);
    }

    @Override
    public void saveData(@NonNull List<Tracker> data) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.insertItems(data));
    }

    @Override
    public void saveItem(@NonNull Tracker item) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.insertItemOnConflictReplace(item));
    }

    @Override
    public void updateItem(@NonNull Tracker item) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.updateItem(item));
    }

    @Override
    public void deleteItem(@NonNull Tracker item) {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.deleteItem(item));
    }

    @Override
    public void deleteAllItems() {
        mAppExecutors.diskIO().execute(() -> mTrackersDao.deleteAllTrackers());
    }

}
