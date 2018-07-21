package com.fraserbrooks.progresstracker.data.source;

/*
  Created by Fraser on 07/04/2018.
 */


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;


public class Repository implements DataSource {

    private final String TAG = "REPOSITORY>";

    private static Repository INSTANCE = null;

    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;

    private final AppExecutors mAppExecutors;


    /**
     * These variable has package local visibility so it can be accessed from tests.
     */

    private Map<String, Tracker> mCachedTrackers;
    private Map<String, Target> mCachedTargets;


    private boolean mSyncEnabled = false;


    // Prevent direct instantiation.
    private Repository(@NonNull DataSource tasksRemoteDataSource,
                            @NonNull DataSource tasksLocalDataSource,
                       @NonNull AppExecutors appExecutors) {
        mRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mLocalDataSource = checkNotNull(tasksLocalDataSource);
        mAppExecutors = checkNotNull(appExecutors);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param remoteDataSource the backend data source
     * @param localDataSource  the device storage data source
     * @return the {@link Repository} instance
     */
    public static Repository getInstance(DataSource remoteDataSource,
                                              DataSource localDataSource,
                                         AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(remoteDataSource, localDataSource, appExecutors);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DataSource, DataSource, AppExecutors)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void refreshAllCache() {
        //todo
    }

    /**
     * Gets trackers from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTrackers(boolean runOnUiThread, @NonNull final GetTrackersCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTrackers != null ) {
            callback.onTrackersLoaded(new ArrayList<>(mCachedTrackers.values()));

        } else{
            // Query the local storage to fill the cache
            refreshCachedTrackersAndTotals(callback, null, null);
        }

    }

    /**
     * Gets tracker from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getTracker(boolean runOnUiThread, @NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {
        checkNotNull(trackerId);
        checkNotNull(callback);

        Tracker cachedTracker = getTrackerWithId(trackerId);

        // Respond immediately with cache if available
        if (cachedTracker != null) {
            callback.onTrackerLoaded(cachedTracker);
        }else if(mCachedTrackers == null){
            // Query the local storage to fill the cache
            refreshCachedTrackersAndTotals(null, callback, trackerId);
        }else{
            callback.onDataNotAvailable();
        }

    }


    @Override
    public boolean saveTracker(@NonNull Tracker tracker) {
        checkNotNull(tracker);
        boolean local = mLocalDataSource.saveTracker(tracker);
        boolean remote = mRemoteDataSource.saveTracker(tracker);


        int total = 0;

        // Do in memory cache update to keep the app UI up to date
        mCachedTrackers.put(tracker.getId(), tracker);

        return local;
    }

    @Override
    public void updateTracker(@NonNull Tracker tracker) {

        mLocalDataSource.updateTracker(tracker);

        // todo
        if(mSyncEnabled) {
            mRemoteDataSource.updateTracker(tracker);
        }

        if(mCachedTrackers != null){
            for(Tracker t : mCachedTrackers.values()){
                if(t.getId().equals(tracker.getId())){
                    mCachedTrackers.remove(t.getId());
                    mCachedTrackers.put(tracker.getId(), tracker);
                }
            }
        }
    }


    @Override
    public void deleteAllTrackers() {
        mRemoteDataSource.deleteAllTrackers();
        mLocalDataSource.deleteAllTrackers();

        if (mCachedTrackers == null) {
            mCachedTrackers = new ConcurrentHashMap<>();
        }
        mCachedTrackers.clear();
    }

    @Override
    public boolean deleteTracker(@NonNull String trackerId) {
        boolean local = mRemoteDataSource.deleteTracker(checkNotNull(trackerId));
        boolean remote = mLocalDataSource.deleteTracker(checkNotNull(trackerId));

        mCachedTrackers.remove(trackerId);

        return local;
    }



    @Override
    public void getTargets(boolean runOnUiThread, @NonNull final GetTargetsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available
        if (mCachedTargets != null) {
            callback.onTargetsLoaded(new ArrayList<>(mCachedTargets.values()));

        } else{
            // Query the local storage to fill the cache
            refreshCachedTargets(true, callback);
        }
    }

    public void getDayTargets(@NonNull final GetTargetsCallback callback) {
        getTargets(false, new GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {
                final ArrayList<Target> dayTargets = new ArrayList<>();
                for(Target t: targets){
                    if(t.isRollingTarget() && t.getInterval().equals("DAY")) dayTargets.add(t);
                }
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onTargetsLoaded(dayTargets);
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void getTarget(boolean runOnUiThread, @NonNull final String targetId, @NonNull final GetTargetCallback callback) {
        checkNotNull(targetId);
        checkNotNull(callback);

        Target cachedTarget = getTargetWithId(targetId);

        // Respond immediately with cache if available
        if (cachedTarget != null) {
            callback.onTargetLoaded(cachedTarget);
        }else if(mCachedTargets == null){
            // Query the local storage to fill the cache
            refreshCachedTargets(false,null);
            mLocalDataSource.getTarget(runOnUiThread, targetId, callback);
        }else{
            callback.onDataNotAvailable();
        }
    }

    @Override
    public boolean saveTarget(@NonNull Target target) {
        checkNotNull(target);
        boolean remote = mRemoteDataSource.saveTarget(target);
        boolean local = mLocalDataSource.saveTarget(target);

        int average = 0;

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTargets == null) {
            mCachedTargets = new ConcurrentHashMap<>();
        }else{
            mCachedTargets.put(target.getId(), target);
        }

        return local;
    }

    @Override
    public void updateTarget(@NonNull final Target target) {
        mLocalDataSource.updateTarget(target);

        // todo
        if(mSyncEnabled) {
            mRemoteDataSource.updateTarget(target);
        }

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTargets == null) {
            mCachedTargets = new ConcurrentHashMap<>();
        }else{
            mCachedTargets.put(target.getId(), target);
        }
    }

    @Override
    public void deleteAllTargets() {
        // todo
        mLocalDataSource.deleteAllTargets();
    }

    @Override
    public boolean deleteTarget(@NonNull String targetId) {
        // todo
        return false;
    }

    @Override
    public List<ScoreEntry> getEntries() {
        // never used
        return null;
    }

    @Override
    public void getDaysTargetWasMet(String targetId1, String targetId2, String targetId3, GetDaysTargetsMetCallback callback) {
        mLocalDataSource.getDaysTargetWasMet(targetId1, targetId2, targetId3, callback);
    }


    @Override
    public void deleteAllEntries() {
        // do nothing
    }

    @Override
    public void incrementScore(@NonNull String trackerId, int increment) {
        mLocalDataSource.incrementScore(trackerId, increment);
        if(mCachedTrackers != null){
            if(mCachedTrackers.get(trackerId) != null){
                Tracker t = mCachedTrackers.get(trackerId);
                t.setCountSoFar(t.getCountSoFar() + increment);
            }
        }
        if(mSyncEnabled){
            //todo
        }
    }

    @Override
    public void getTrackerTotalScore(@NonNull final String trackerId, @NonNull final GetNumberCallback callback) {

        // todo? OR do nothing?

        String s = null;

        if (s.isEmpty()) Log.d(TAG, "dgsdfgsdfgdfg");

//        // Respond immediately with cache unless it is null in which case fill it
//        if (mCachedTrackers == null) {
//            // Query the local storage to fill the cache
//            refreshCachedTrackersAndTotals(null, null, null);
//            mLocalDataSource.getTrackerTotalScore(trackerId, callback);
//        }else{
//            Tracker t = mCachedTrackers.get(trackerId);
//            if(t != null) callback.onNumberLoaded(t.getCountSoFar());
//            else callback.onDataNotAvailable();
//        }
        
    }

    @Override
    public void getTargetAverageCompletion(@NonNull final String targetId, @NonNull final GetNumberCallback callback) {

        // todo? OR do nothing?

        String s = null;

        if (s.isEmpty()) Log.d(TAG, "dgsdfgsdfgdfg");

//        // Respond immediately with cache unless it is null in which case fill it
//        if (mCachedTargets == null) {
//            // Query the local storage to fill the cache
//            refreshCachedTargets();
//            mLocalDataSource.getTargetAverageCompletion(targetId, callback);
//        }else{
//            mLocalDataSource.getTargetAverageCompletion(targetId, new GetNumberCallback() {
//                @Override
//                public void onNumberLoaded(Integer number) {
//                    Target t = mCachedTargets.get(targetId);
//                    if(t != null) t.setAverageOverTime(number);
//                    callback.onNumberLoaded(number);
//                }
//
//                @Override
//                public void onDataNotAvailable() {
//                    Log.e(TAG, "onDataNotAvailable: couldn't load average from local db");
//                }
//            });
//        }
    }

    @Override
    public void getScoreOnDay(@NonNull String trackerId, Calendar day, @NonNull GetNumberCallback callback) {
        mLocalDataSource.getScoreOnDay(trackerId, day, callback);
    }

    @Override
    public void getScoreOnWeek(@NonNull String trackerId, Calendar week, @NonNull GetNumberCallback callback) {
        mLocalDataSource.getScoreOnWeek(trackerId, week, callback);
    }

    @Override
    public void getScoreOnMonth(@NonNull String trackerId, Calendar month, @NonNull GetNumberCallback callback) {
        mLocalDataSource.getScoreOnMonth(trackerId, month, callback);
    }

    @Override
    public void getScoreOnYear(@NonNull String trackerId, Calendar year, @NonNull GetNumberCallback callback) {
        mLocalDataSource.getScoreOnYear(trackerId, year, callback);
    }


    private void refreshCachedTrackersAndTotals(@Nullable final GetTrackersCallback trackersCallback,
                                                @Nullable final GetTrackerCallback  trackerCallback,
                                                @Nullable final String trackerId) {

        mLocalDataSource.getTrackers(false, new GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(final List<Tracker> trackers) {
                if (mCachedTrackers == null) {
                    mCachedTrackers = new ConcurrentHashMap<>();
                }
                mCachedTrackers.clear();

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(trackersCallback != null){
                            trackersCallback.onTrackersLoaded(trackers);
                        }else if(trackerCallback != null && trackerId != null){
                            Tracker t = mCachedTrackers.get(trackerId);
                            if(t != null) trackerCallback.onTrackerLoaded(t);
                        }
                    }
                });

                for (final Tracker tracker : trackers) {
                    mLocalDataSource.getTrackerTotalScore(tracker.getId(), new GetNumberCallback() {
                        @Override
                        public void onNumberLoaded(Integer number) {
                            Log.d(TAG, "onNumberLoaded: tracker " + tracker.getTitle() + " total = " + number);
                            tracker.setCountSoFar(number);
                            tracker.setExpanded(false);
                            mCachedTrackers.put(tracker.getId(), tracker);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            // shouldn't happen
                            Log.e(TAG, "SQL Error, tracker total not available");
                        }
                    });

                    tracker.setUiValues();
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "Error, unable to retrieve Trackers");
                if (mCachedTrackers == null) {
                    mCachedTrackers = new ConcurrentHashMap<>();
                }
            }
        });


    }

    private void refreshCachedTargets(final boolean runOnUiThread, @Nullable final GetTargetsCallback targetsCallback) {

        mLocalDataSource.getTargets(false, new GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {
                if (mCachedTargets == null) {
                    mCachedTargets = new ConcurrentHashMap<>();
                }
                mCachedTargets.clear();

                for(Target t : targets){
                    mCachedTargets.put(t.getId(), t);
                }

                for (final Target target : targets) {

                    mLocalDataSource.getTracker(false, target.getTrackId(), new GetTrackerCallback() {
                        @Override
                        public void onTrackerLoaded(Tracker tracker) {
                            Target t = mCachedTargets.get(target.getId());
                            t.setTrackerName(tracker.getTitle());
                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });

                    mLocalDataSource.getTargetAverageCompletion(target.getId(), new GetNumberCallback() {
                        @Override
                        public void onNumberLoaded(Integer number) {
                            Target t = mCachedTargets.get(target.getId());
                            Log.d(TAG, "onNumberLoaded: calculated average for '" + t.getId()  + "' = " + number);
                            t.setAverageOverTime(number);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            // shouldn't happen
                            Log.e(TAG, "SQL Error, target average not available");
                        }
                    });

                    DataSource.GetNumberCallback callback = new DataSource.GetNumberCallback() {
                        @Override
                        public void onNumberLoaded(Integer number) {

                            int percentage = (number * 100) / target.getNumberToAchieve();
                            percentage = (percentage > 100) ? 100 : percentage;

                            Target t = mCachedTargets.get(target.getId());
                            t.setCurrentProgressPercentage(percentage);

                        }

                        @Override
                        public void onDataNotAvailable() {
                            Log.e(TAG, "onDataNotAvailable: could not load target progress");
                        }
                    };

                    if(target.isRollingTarget()){
                        Calendar cal = Calendar.getInstance();
                        switch (target.getInterval()){
                            case "DAY":
                                mLocalDataSource.getScoreOnDay(target.getTrackId(), cal, callback);
                                break;
                            case "WEEK":
                                mLocalDataSource.getScoreOnWeek(target.getTrackId(), cal, callback);
                                break;
                            case "MONTH":
                                mLocalDataSource.getScoreOnMonth(target.getTrackId(), cal, callback);
                                break;
                            case "YEAR":
                                mLocalDataSource.getScoreOnYear(target.getTrackId(), cal, callback);
                                break;
                            default:
                                mLocalDataSource.getScoreOnDay(target.getTrackId(), cal, callback);
                                break;
                        }
                    }else{
                        // todo
                        target.setCurrentProgressPercentage(5);
                    }

                }
                if(targetsCallback != null){
                    Runnable runnable =  new Runnable() {
                        @Override
                        public void run() {
                            targetsCallback.onTargetsLoaded(new ArrayList<>(mCachedTargets.values()));
                        }
                    };
                    if(runOnUiThread) mAppExecutors.mainThread().execute(runnable);
                    else runnable.run();
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "Error, unable to retrieve Targets");
                if (mCachedTargets == null) {
                    mCachedTargets = new ConcurrentHashMap<>();
                }
                if(targetsCallback != null){
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            targetsCallback.onDataNotAvailable();
                        }
                    });
                }
            }
        });




    }


    private void refreshLocalDataSourceTrackers(List<Tracker> trackers) {
        mLocalDataSource.deleteAllTrackers();
        for (Tracker tracker : trackers) {
            mLocalDataSource.saveTracker(tracker);
        }
    }

    private void refreshLocalDataSourceTargets(List<Target> targets) {
        mLocalDataSource.deleteAllTargets();
        for (Target target : targets) {
            mLocalDataSource.saveTarget(target);
        }
    }

    private void refreshLocalDataSourceEntries(List<ScoreEntry> entries){
        mLocalDataSource.deleteAllEntries();
    }


    @Nullable
    private Tracker getTrackerWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTrackers == null || mCachedTrackers.isEmpty()) {
            return null;
        } else {
            return mCachedTrackers.get(id);
        }
    }

    @Nullable
    private Target getTargetWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTargets == null || mCachedTargets.isEmpty()) {
            return null;
        } else {
            return mCachedTargets.get(id);
        }
    }







}