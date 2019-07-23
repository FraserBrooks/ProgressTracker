package com.fraserbrooks.progresstracker.datasource.source.local;

import android.util.Log;

import com.fraserbrooks.progresstracker.targets.domain.model.Target;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.datasource.source.TargetDataSource;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class TargetsLocalDataSource implements TargetDataSource {

    private static final String TAG = "TargetsLocalDataSource";

    private static volatile TargetsLocalDataSource INSTANCE;

    private TargetsDao mTargetsDao;
    private TrackersDao mTrackersDao;
    private AppExecutors mAppExecutors;


    // Prevent direct instantiation
    private TargetsLocalDataSource(@NonNull AppExecutors appExecutors,
                                   @NonNull TargetsDao targetsDao,
                                   @NonNull TrackersDao trackersDao){
        mAppExecutors = appExecutors;
        mTargetsDao = targetsDao;
        mTrackersDao = trackersDao;
    }

    public static TargetsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull TargetsDao targetsDao,
                                                     @NonNull TrackersDao trackersDao){

        if(INSTANCE == null){
            synchronized (TargetsLocalDataSource.class){
                if(INSTANCE == null){
                    Log.d(TAG, "getInstance: new instance created");
                    INSTANCE = new TargetsLocalDataSource(appExecutors, targetsDao, trackersDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Target>> getData() {
        LiveData<List<Target>> persistedData = mTargetsDao.getTargets();
        MediatorLiveData<List<Target>> processedData = new MediatorLiveData<>();
        processedData.addSource(persistedData, targets -> {
            for (Target t : targets) processTarget(t);
            processedData.postValue(targets);
        });
        return processedData;
    }

    @Override
    public LiveData<Target> getItem(String id) {
        LiveData<Target> persistedData = mTargetsDao.getTargetById(id);
        MediatorLiveData<Target> processedData = new MediatorLiveData<>();
        processedData.addSource(persistedData, target -> {
            processTarget(target);
            processedData.postValue(target);
        });
        return processedData;
    }

    private void processTarget(Target target){

        Tracker tracker = mTrackersDao.getTrackerSync(target.getTrackId());

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
            case Target.EVERY_DAY: achieved = mTargetsDao.getCountScoresOverDayTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_WEEK: achieved = mTargetsDao.getCountScoresOverWeekTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_MONTH: achieved = mTargetsDao.getCountScoresOverMonthTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
            case Target.EVERY_YEAR: achieved = mTargetsDao.getCountScoresOverYearTarget(target.getId(), target.getNumberToAchieve(), startDate, now);
                break;
        }
        int avg = (int) (achieved/maxPossible * 100f);
        avg = (avg > 100) ? 100 : avg;
        return avg;
    }

    @Override
    public LiveData<List<Date>> getDaysTargetMet(@NonNull String targetId, @NonNull final Calendar month) {

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

        return  mTargetsDao.getDaysTargetCompleted(targetId, twoAndAHalfMonthsAgo, twoAndAHalfMonthsAhead);
    }

    @Override
    public void saveData(@NonNull List<Target> data) {
        mAppExecutors.diskIO().execute(() -> mTargetsDao.insertItems(data));
    }

    @Override
    public void saveItem(@NonNull Target item) {
        mAppExecutors.diskIO().execute(() -> mTargetsDao.insertItemOnConflictReplace(item));
    }

    @Override
    public void updateItem(@NonNull Target item) {
        mAppExecutors.diskIO().execute(() -> mTargetsDao.updateItem(item));
    }

    @Override
    public void deleteItem(@NonNull Target item) {
        mAppExecutors.diskIO().execute(() -> mTargetsDao.deleteItem(item));
    }

    @Override
    public void deleteAllItems() {
        mAppExecutors.diskIO().execute(() -> mTargetsDao.deleteAllTargets());
    }
}
