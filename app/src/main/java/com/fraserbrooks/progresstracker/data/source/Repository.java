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
import com.fraserbrooks.progresstracker.data.UserSetting;
import com.fraserbrooks.progresstracker.util.AppExecutors;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;


public class Repository implements DataSource {

    private final String TAG = "Repository";

    private static Repository INSTANCE = null;

    private final DataSource mRemoteDataSource;
    private final DataSource mLocalDataSource;

    private final AppExecutors mAppExecutors;

    private Map<String, Tracker> mCachedTrackers;
    private Map<String, Target> mCachedTargets;

    // Listeners for changes to the cached targets/trackers
    private List<DeleteTargetListener> mDeleteTargetListeners;
    private List<UpdateOrAddTargetListener> mUpdateOrAddTargetListeners;
    private List<DeleteTrackerListener> mDeleteTrackerListeners;
    private List<UpdateOrAddTrackerListener> mUpdateOrAddTrackerListeners;

    // Sync all data with remoteDataSource
    private boolean mSyncEnabled = false;

    // Prevent direct instantiation.
    private Repository(@NonNull DataSource tasksRemoteDataSource,
                            @NonNull DataSource tasksLocalDataSource,
                       @NonNull AppExecutors appExecutors) {
        mRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mLocalDataSource = checkNotNull(tasksLocalDataSource);
        mAppExecutors = checkNotNull(appExecutors);

        // todo: make thread safe to be on the safe side
        mDeleteTargetListeners = new ArrayList<>();
        mUpdateOrAddTargetListeners = new ArrayList<>();
        mDeleteTrackerListeners = new ArrayList<>();
        mUpdateOrAddTrackerListeners = new ArrayList<>();

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
        mCachedTargets = null;
    }

    /**
     * Gets trackers from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTrackers(@NonNull final GetTrackersCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTrackers != null ) {
            ArrayList trackers = new ArrayList<>(mCachedTrackers.values());
            if(trackers.isEmpty()) callback.onDataNotAvailable();
            else  callback.onTrackersLoaded(trackers);
        } else{
            // Query the local storage to fill the cache
            mLocalDataSource.getTrackers(new GetTrackersCallback() {
                @Override
                public void onTrackersLoaded(List<Tracker> trackers) {
                    refreshTrackersCache(trackers);
                    callback.onTrackersLoaded(trackers);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
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
    public void getTracker(@NonNull final String trackerId, @NonNull final GetTrackerCallback callback) {
        checkNotNull(trackerId);
        checkNotNull(callback);

        Tracker cachedTracker = getTrackerWithId(trackerId);

        // Respond immediately with cache if available
        if (cachedTracker != null) {
                callback.onTrackerLoaded(cachedTracker);
        }else if(mCachedTrackers == null){
            // Query the local storage to fill the cache
            mLocalDataSource.getTrackers(new GetTrackersCallback() {
                @Override
                public void onTrackersLoaded(List<Tracker> trackers) {
                    refreshTrackersCache(trackers);
                    // recurse
                    getTracker(trackerId, callback);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }else{
            mLocalDataSource.getTracker(trackerId, callback);
        }
    }


    @Override
    public boolean saveTracker(@NonNull Tracker tracker) {
        checkNotNull(tracker);

        // Send to local database
        boolean local = mLocalDataSource.saveTracker(tracker);

        mRemoteDataSource.saveTracker(tracker);

        // Retrieve from the local database ready for cache/ui
        mLocalDataSource.getTracker(tracker.getId(), new GetTrackerCallback() {
            @Override
            public void onTrackerLoaded(Tracker tracker) {
                // Do in memory cache update to keep the app UI up to date
                if(mCachedTrackers != null) mCachedTrackers.put(tracker.getId(), tracker);
                notifyTrackerChangeListeners(tracker);
            }

            @Override
            public void onDataNotAvailable() {
                // Should not happen
                Log.e(TAG, "onDataNotAvailable: retrieving tracker");
            }
        });

        return local;
    }

    @Override
    public void saveTrackers(@NonNull List<Tracker> trackers) {
        mLocalDataSource.saveTrackers(trackers);
    }

    @Override
    public void updateTracker(@NonNull Tracker tracker) {

        mLocalDataSource.updateTracker(tracker);

        if(mSyncEnabled) {
            mRemoteDataSource.updateTracker(tracker);
        }

        // Retrieve from the local database ready for cache/ui
        refreshTracker(tracker.getId());


    }

    @Override
    public void incrementTracker(@NonNull final String trackerId, int increment) {
        mLocalDataSource.incrementTracker(trackerId, increment);

        // Retrieve from the local database ready for cache/ui
        refreshTracker(trackerId);

        // Update targets and trackers in cache and in the UI via any active listeners
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mCachedTargets != null){
                    for(final Target target : mCachedTargets.values()){
                        if(target.getTrackId().equals(trackerId)){
                            mLocalDataSource.getTarget(target.getId(), new GetTargetCallback() {
                                @Override
                                public void onTargetLoaded(Target updatedTarget) {
                                    mCachedTargets.put(updatedTarget.getId(), updatedTarget);
                                    // Update target in UI
                                    mAppExecutors.mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyTargetChangeListeners(target);
                                        }
                                    });
                                }

                                @Override
                                public void onDataNotAvailable() {
                                    Log.d(TAG, "onDataNotAvailable: target not found in local DB");
                                    mCachedTargets.remove(target.getId());
                                }
                            });

                        }
                    }
                }



            }
        });

    }

    private void refreshTracker(String trackerId) {

        mLocalDataSource.getTracker(trackerId, new GetTrackerCallback() {
            @Override
            public void onTrackerLoaded(Tracker tracker) {
                // Do in memory cache update to keep the app UI up to date
                if(mCachedTrackers != null) mCachedTrackers.put(tracker.getId(), tracker);
                notifyTrackerChangeListeners(tracker);
            }

            @Override
            public void onDataNotAvailable() {
                // Should not happen
                Log.e(TAG, "onDataNotAvailable: could not find tracker that exists in UI");
            }
        });
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
        mLocalDataSource.deleteTracker(checkNotNull(trackerId));


        if(mCachedTrackers != null){
            Tracker tracker = mCachedTrackers.get(trackerId);
            if(tracker != null) notifyDeleteTrackerListeners(tracker);
            mCachedTrackers.remove(trackerId);
        }


        // delete/remove targets with that tracker id
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {

                // Get all targets
                ArrayList<Target> ts = new ArrayList<>(mCachedTargets.values());

                // Search all targets for any that pertain to the deleted tracker
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
                                // Update UI through subscribed listeners
                                notifyDeleteTargetListeners(targetToDelete);
                            }
                        });

                    }
                }
            }
        });

        return local;
    }


    @Override
    public void getTargets(@NonNull final GetTargetsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available
        if (mCachedTargets != null) {
            ArrayList<Target> targets =  new ArrayList<>(mCachedTargets.values());
            if(targets.isEmpty()) callback.onDataNotAvailable();
            else callback.onTargetsLoaded(targets);
        } else{
            // Query the local storage to fill the cache
            mLocalDataSource.getTargets(new GetTargetsCallback() {
                @Override
                public void onTargetsLoaded(List<Target> targets) {
                    refreshTargetsCache(targets);
                    callback.onTargetsLoaded(targets);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }


    @Override
    public void getTarget(@NonNull final String targetId, @NonNull final GetTargetCallback callback) {
        checkNotNull(targetId);
        checkNotNull(callback);

        Target cachedTarget = getTargetWithId(targetId);

        // Respond immediately with cache if available
        if (cachedTarget != null) {
            callback.onTargetLoaded(cachedTarget);
        }else if(mCachedTargets == null){
            // Query the local storage to fill the cache
            mLocalDataSource.getTargets(new GetTargetsCallback() {
                @Override
                public void onTargetsLoaded(List<Target> targets) {
                    refreshTargetsCache(targets);
                    //recurse
                    getTarget(targetId, callback);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }else{
            mLocalDataSource.getTarget(targetId, callback);
        }
    }

    @Override
    public boolean saveTarget(@NonNull final Target target) {
        checkNotNull(target);

        // Send to local database
        boolean local = mLocalDataSource.saveTarget(target);

        mRemoteDataSource.saveTarget(target);

        // Retrieve from the local database ready for cache/ui
        mLocalDataSource.getTarget(target.getId(), new GetTargetCallback() {
            @Override
            public void onTargetLoaded(Target target) {
                // Do in memory cache update to keep the app UI up to date
                mCachedTargets.put(target.getId(), target);
                notifyTargetChangeListeners(target);
            }

            @Override
            public void onDataNotAvailable() {
                // Should not happen
                Log.e(TAG, "onDataNotAvailable: retrieving target");
            }
        });

        return local;
        
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
        mLocalDataSource.deleteAllTargets();
        mRemoteDataSource.deleteAllTargets();


        if (mCachedTargets == null) {
            mCachedTargets = new ConcurrentHashMap<>();
        }
        mCachedTargets.clear();
    }

    @Override
    public boolean deleteTarget(@NonNull String targetId) {
        // todo
        return false;
    }

    @Override
    public void getEntries(@NonNull GetEntriesCallback callback) {
        // Shouldn't be used elsewhere in the app.
    }

    @Override
    public void saveEntries(@NonNull List<ScoreEntry> entries) {
        mLocalDataSource.saveEntries(entries);
}

    @Override
    public void getDaysTargetMet(@NonNull String targetId, @NonNull Calendar month,
                                 @NonNull GetDaysTargetMetCallback callback) {

        mLocalDataSource.getDaysTargetMet(targetId, month, new GetDaysTargetMetCallback() {
            @Override
            public void onDaysLoaded(Set<Date> successfulDays) {
                callback.onDaysLoaded(successfulDays);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteAllEntries() {
        // do nothing
    }

    @Override
    public void setSetting(@NonNull UserSetting.Setting setting,@NonNull String value) {
        mLocalDataSource.setSetting(setting, value);
    }

    @Override
    public void getSettingValue(UserSetting.Setting setting, GetSettingCallback callback) {
        mLocalDataSource.getSettingValue(setting, callback);
    }


    private void refreshTrackersCache(List<Tracker> trackers){
        
        if(mCachedTrackers == null){
            mCachedTrackers = new ConcurrentHashMap<>();
        }
        mCachedTrackers.clear();
        
        for(Tracker t : trackers) mCachedTrackers.put(t.getId(), t);
        
    }
    
    private void refreshTargetsCache(List<Target> targets){

        if(mCachedTargets == null){
            mCachedTargets = new ConcurrentHashMap<>();
        }
        mCachedTargets.clear();

        for(Target t : targets) mCachedTargets.put(t.getId(), t);
        
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

    private void notifyTargetChangeListeners(Target target) {
        for(int i = 0; i < mUpdateOrAddTargetListeners.size() ; i++){
            if(mUpdateOrAddTargetListeners.get(i).isActive()){
                // Update target
                mUpdateOrAddTargetListeners.get(i).targetUpdated(target);
            }else{
                // Listener no longer active so remove it
                mUpdateOrAddTargetListeners.remove(i);
                i--;
            }
        }
    }

    private void notifyDeleteTargetListeners(Target targetToDelete) {
        for(int j = 0; j< mDeleteTargetListeners.size(); j++){
            if(mDeleteTargetListeners.get(j).isActive()){
                mDeleteTargetListeners.get(j).targetDeleted(targetToDelete);
            }else{
                mDeleteTargetListeners.remove(j);
                j--;
            }
        }
    }

    private void notifyTrackerChangeListeners(Tracker tracker) {
        for(int i = 0; i < mUpdateOrAddTrackerListeners.size() ; i++){
            if(mUpdateOrAddTrackerListeners.get(i).isActive()){
                // Update tracker
                mUpdateOrAddTrackerListeners.get(i).trackerUpdated(tracker);
            }else{
                // Listener no longer active so remove it
                mUpdateOrAddTrackerListeners.remove(i);
                i--;
            }
        }
    }

    private void notifyDeleteTrackerListeners(Tracker trackerToDelete) {
        for(int j = 0; j< mDeleteTrackerListeners.size(); j++){
            if(mDeleteTrackerListeners.get(j).isActive()){
                mDeleteTrackerListeners.get(j).trackerDeleted(trackerToDelete);
            }else{
                mDeleteTrackerListeners.remove(j);
                j--;
            }
        }
    }


    public void addDeleteTargetListener(DeleteTargetListener listener){
        mDeleteTargetListeners.add(listener);
    }

    public void addUpdateOrAddTargetListener(UpdateOrAddTargetListener listener){
        mUpdateOrAddTargetListeners.add(listener);
    }

    public void addDeleteTrackerListener(DeleteTrackerListener listener){
        mDeleteTrackerListeners.add(listener);
    }

    public void addUpdateOrAddTrackerListener(UpdateOrAddTrackerListener listener){
        mUpdateOrAddTrackerListeners.add(listener);
    }


}