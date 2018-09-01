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


    public interface DeleteTargetListener{

        boolean isActive();

        void trackerDeleted(Target targetToDelete);

    }
    private List<DeleteTargetListener> mDeleteTargetListeners;

    public interface TargetChangeListener{
        boolean isActive();

        void targetUpdated(Target target);
    }
    private final List<TargetChangeListener> mTargetChangeListeners;

    private boolean mSyncEnabled = false;

    // Prevent direct instantiation.
    private Repository(@NonNull DataSource tasksRemoteDataSource,
                            @NonNull DataSource tasksLocalDataSource,
                       @NonNull AppExecutors appExecutors) {
        mRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mLocalDataSource = checkNotNull(tasksLocalDataSource);
        mAppExecutors = checkNotNull(appExecutors);
        mDeleteTargetListeners = new ArrayList<>();
        mTargetChangeListeners = new ArrayList<>();
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
        mCachedTrackers = null;
        mCachedTrackers = null;
    }

    /**
     * Gets trackers from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTrackers(@NonNull final GetTrackersCallback callback, boolean staggeredLoad) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTrackers != null ) {
            ArrayList<Tracker> trackers =  new ArrayList<>(mCachedTrackers.values());
            if(staggeredLoad){
                for(Tracker t : trackers){
                    callback.onTrackerLoaded(t);
                }
            }else{
                callback.onTrackersLoaded(trackers);
            }

        } else{
            // Query the local storage to fill the cache
            refreshCachedTrackersAndTotals(callback, null, staggeredLoad);
        }

    }

    /**
     * Gets tracker from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackersCallback callback) {
        checkNotNull(trackerId);
        checkNotNull(callback);

        Tracker cachedTracker = getTrackerWithId(trackerId);

        // Respond immediately with cache if available
        if (cachedTracker != null) {
            callback.onTrackerLoaded(cachedTracker);
        }else if(mCachedTrackers == null){
            // Query the local storage to fill the cache
            refreshCachedTrackersAndTotals(callback, trackerId, false);
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
    public void saveTrackers(@NonNull List<Tracker> trackers) {
        mLocalDataSource.saveTrackers(trackers);
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
    public boolean deleteTracker(@NonNull final String trackerId) {
        boolean local = mRemoteDataSource.deleteTracker(checkNotNull(trackerId));
        boolean remote = mLocalDataSource.deleteTracker(checkNotNull(trackerId));

        mCachedTrackers.remove(trackerId);

        // delete/remove targets with that tracker id
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Target> ts = new ArrayList<>(mCachedTargets.values());
                for(int i = 0;  i < ts.size() ; i++){
                    if(ts.get(i).getTrackId().equals(trackerId)){
                        final String idOfTargetToDelete = ts.get(i).getId();
                        final Target targetToDelete = mCachedTargets.get(idOfTargetToDelete);
                        mCachedTargets.remove(idOfTargetToDelete);
                        ts.remove(i);
                        i--;
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                for(int j = 0; j< mDeleteTargetListeners.size(); j++){
                                    if(mDeleteTargetListeners.get(j).isActive()){
                                        mDeleteTargetListeners.get(j).trackerDeleted(targetToDelete);
                                    }else{
                                        mDeleteTargetListeners.remove(j);
                                        j--;
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });



        return local;
    }



    @Override
    public void getTargets(@NonNull final GetTargetsCallback callback, boolean staggeredLoad) {
        checkNotNull(callback);

        // Respond immediately with cache if available
        if (mCachedTargets != null) {
            ArrayList<Target> targets =  new ArrayList<>(mCachedTargets.values());
            if(staggeredLoad){
                for(Target t : targets){
                    callback.onTargetLoaded(t);
                }
            }else{
                callback.onTargetsLoaded(targets);
            }

        } else{
            // Query the local storage to fill the cache
            refreshCachedTargets(callback, null, staggeredLoad);
        }
    }


    @Override
    public void getTarget(@NonNull final String targetId, @NonNull final GetTargetsCallback callback) {
        checkNotNull(targetId);
        checkNotNull(callback);

        Target cachedTarget = getTargetWithId(targetId);

        // Respond immediately with cache if available
        if (cachedTarget != null) {
            callback.onTargetLoaded(cachedTarget);
        }else if(mCachedTargets == null){
            // Query the local storage to fill the cache
            refreshCachedTargets(callback,targetId,false);
        }else{
            callback.onDataNotAvailable();
        }
    }

    @Override
    public boolean saveTarget(@NonNull final Target target) {
        checkNotNull(target);

        boolean remote = mRemoteDataSource.saveTarget(target);
        boolean local = mLocalDataSource.saveTarget(target);

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTargets == null) {
                    mCachedTargets = new ConcurrentHashMap<>();
                }else{
                    loadOrRefreshTarget(target);
                }
            }
        });
        return true;
    }

    @Override
    public void saveTargets(@NonNull List<Target> targets) {
        mLocalDataSource.saveTargets(targets);
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
        // Shouldn't be used elsewhere.
        return null;
    }

    @Override
    public void saveEntries(List<ScoreEntry> entries) {
        mLocalDataSource.saveEntries(entries);
    }

    @Override
    public void getDaysTargetsMet(String targetId1, String targetId2, String targetId3, Calendar month, GetDaysTargetsMetCallback callback) {
        mLocalDataSource.getDaysTargetsMet(targetId1, targetId2, targetId3, month, callback);
    }


    @Override
    public void deleteAllEntries() {
        // do nothing
    }

    @Override
    public void incrementScore(@NonNull final String trackerId, int increment) {
        mLocalDataSource.incrementScore(trackerId, increment);

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for(Target target : mCachedTargets.values()){
                    if(target.getTrackId().equals(trackerId)){
                        loadOrRefreshTarget(target, new GetTargetsCallback() {
                            @Override
                            public void onTargetsLoaded(List<Target> targets) {
                                // Not used
                            }

                            @Override
                            public void onTargetLoaded(final Target target) {
                                mAppExecutors.mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        synchronized (mTargetChangeListeners){
                                            for(TargetChangeListener listener: mTargetChangeListeners){
                                                if(listener.isActive()) listener.targetUpdated(target);
                                                else mTargetChangeListeners.remove(listener);
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onDataNotAvailable() {
                                // Not used
                            }
                        });
                    }
                }
            }
        });

        if(mCachedTrackers != null){
            if(mCachedTrackers.get(trackerId) != null){
                Tracker t = mCachedTrackers.get(trackerId);
                t.setCountSoFar(t.getCountSoFar() + increment);
                t.setUiValues();
            }
        }
        if(mSyncEnabled){
            //todo
        }




    }

    @Override
    public void getTrackerTotalScore(@NonNull final String trackerId, @NonNull final GetNumberCallback callback) {
        // Only local DataSource needs to implement this.
        // Shouldn't be used elsewhere.
        Log.e(TAG, "getTrackerTotalScore: called");
    }

    @Override
    public void getTargetAverageCompletion(@NonNull final String targetId, @NonNull final GetNumberCallback callback) {
        // Only local DataSource needs to implement this.
        // Shouldn't be used elsewhere.
        Log.e(TAG, "getTargetAverageCompletion: called");
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


    private void refreshCachedTrackersAndTotals(@NonNull final GetTrackersCallback  trackerCallback,
                                                @Nullable final String trackerId,
                                                final boolean staggeredLoad) {

        mLocalDataSource.getTrackers(new GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(final List<Tracker> trackers) {
                if (mCachedTrackers == null) {
                    mCachedTrackers = new ConcurrentHashMap<>();
                }
                mCachedTrackers.clear();

                for (final Tracker tracker : trackers) {
                    mLocalDataSource.getTrackerTotalScore(tracker.getId(), new GetNumberCallback() {
                        @Override
                        public void onNumberLoaded(Integer number) {
                            Log.d(TAG, "onNumberLoaded: tracker " + tracker.getTitle() + " total = " + number);
                            tracker.setCountSoFar(number);
                            tracker.setExpanded(false);
                            tracker.setUiValues();
                            mCachedTrackers.put(tracker.getId(), tracker);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            // shouldn't happen
                            Log.e(TAG, "SQL Error, tracker total not available");
                        }
                    });

                    if(trackerId == null && staggeredLoad){
                        // Callback wants all trackers but not loaded all at once
                        trackerCallback.onTrackerLoaded(tracker);
                    }

                }
                if(trackerId != null){
                    // Callback wants a specific tracker
                    Tracker toReturn = mCachedTrackers.get(trackerId);
                    if(toReturn != null) trackerCallback.onTrackerLoaded(toReturn);
                }else if(!staggeredLoad) trackerCallback.onTrackersLoaded(trackers);
            }

            @Override
            public void onTrackerLoaded(Tracker t){
                // Shouldn't be used by localDataSource
                Log.e(TAG, "onTrackerLoaded called while refreshing cache");
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "Error, unable to retrieve Trackers");
                if (mCachedTrackers == null) {
                    mCachedTrackers = new ConcurrentHashMap<>();
                }
            }
        }, false);
    }

    private void refreshCachedTargets(@NonNull final GetTargetsCallback targetCallback,
                                      @Nullable final String targetId,
                                      final boolean staggeredLoad) {

        mLocalDataSource.getTargets(new GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {
                if (mCachedTargets == null) {
                    mCachedTargets = new ConcurrentHashMap<>();
                }
                mCachedTargets.clear();

                for (final Target target : targets) {

                    loadOrRefreshTarget(target);

                    if(targetId == null && staggeredLoad){
                        // Callback wants all the targets but not loaded all at once
                        targetCallback.onTargetLoaded(target);
                    }
                }
                if(targetId != null){
                    // Callback wants a specific target
                    Target t = mCachedTargets.get(targetId);
                    if(t != null) targetCallback.onTargetLoaded(t);
                }else if(!staggeredLoad)targetCallback.onTargetsLoaded(targets);
            }

            @Override
            public void onTargetLoaded(Target target) {
                // Shouldn't be used by localDataSource
                Log.e(TAG, "onTargetLoaded called when refreshing cache ");
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "Error, unable to retrieve Targets");
                if (mCachedTargets == null) {
                    mCachedTargets = new ConcurrentHashMap<>();
                }
                targetCallback.onDataNotAvailable();
            }
        }, false);
    }

    private void loadOrRefreshTarget(final Target target) {
        loadOrRefreshTarget(target, null);
    }

    private void loadOrRefreshTarget(final Target target, @Nullable GetTargetsCallback targetCallback){
        // Never call on the main thread
        mLocalDataSource.getTracker(target.getTrackId(), new GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {
                // Shouldn't be used
                Log.e(TAG, "onTrackersLoaded called while refreshing Target cache");
            }

            @Override
            public void onTrackerLoaded(Tracker tracker) {
                target.setTrackerName(tracker.getTitle());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

        mLocalDataSource.getTargetAverageCompletion(target.getId(), new GetNumberCallback() {
            @Override
            public void onNumberLoaded(Integer number) {
                Log.d(TAG, "onNumberLoaded: calculated average for '" + target.getId()  + "' = " + number);
                target.setAverageOverTime(number);
            }

            @Override
            public void onDataNotAvailable() {
                // shouldn't happen
                Log.e(TAG, "SQL Error, target average not available");
            }
        });

        GetNumberCallback callback = new GetNumberCallback() {
            @Override
            public void onNumberLoaded(Integer number) {

                int percentage = (number * 100) / target.getNumberToAchieve();
                percentage = (percentage > 100) ? 100 : percentage;

                target.setCurrentProgressPercentage(percentage);

            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "onDataNotAvailable: could not load target progress");
            }
        };

        if(target.isRollingTarget()){
            // Get progress for this day/week/month
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
            // todo: progress when not a rolling target
            target.setCurrentProgressPercentage(5);
        }
        mCachedTargets.put(target.getId(), target);

        if(targetCallback != null) targetCallback.onTargetLoaded(target);
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


    public void addDeleteTargetListener(DeleteTargetListener listener){
        mDeleteTargetListeners.add(listener);
    }

    public void addTargetChangeListener(TargetChangeListener listener){
        mTargetChangeListeners.add(listener);
    }






}