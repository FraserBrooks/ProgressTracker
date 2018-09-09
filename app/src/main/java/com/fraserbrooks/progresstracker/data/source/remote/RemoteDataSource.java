package com.fraserbrooks.progresstracker.data.source.remote;

/**
 * Created by Fraser on 07/04/2018.
 */


import android.os.Handler;
import android.support.annotation.NonNull;


import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class RemoteDataSource implements DataSource{

    private static RemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Tracker> TRACKERS_SERVICE_DATA;


    static{
        TRACKERS_SERVICE_DATA = new LinkedHashMap<>();
        addTracker("Programming", 4800);
        addTracker("Painting", 100000);
        addTracker("JiuJitsu", 1000, "sessions");
    }

    public static RemoteDataSource getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource(){};

    private static void addTracker(String title, int progressionRate){
        Tracker newTracker = new Tracker(title,"hours", progressionRate, true, true, false);
        TRACKERS_SERVICE_DATA.put(newTracker.getId(), newTracker);
    }

    private static void addTracker(String title, int progressionRate, String counterName){
        Tracker newTracker = new Tracker(title,  counterName, progressionRate, false, true, false);
        TRACKERS_SERVICE_DATA.put(newTracker.getId(), newTracker);
    }

    /**
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getTrackers(@NonNull final GetTrackersCallback callback, boolean staggered) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTrackersLoaded(Lists.newArrayList(TRACKERS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }


    /**
     * Note: {@link GetTrackersCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getTracker(@NonNull String trackerId, @NonNull final GetTrackersCallback callback) {
        final Tracker tracker = TRACKERS_SERVICE_DATA.get(trackerId);

        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTrackerLoaded(tracker);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public boolean saveTracker(@NonNull Tracker tracker) {
        TRACKERS_SERVICE_DATA.put(tracker.getId(), tracker);
        return false;
    }

    @Override
    public void saveTrackers(@NonNull List<Tracker> trackers) {

    }

    @Override
    public void updateTracker(@NonNull Tracker tracker) {

    }

    @Override
    public void refreshAllCache() {
        // Not required because the {@link Repository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTrackers() {
        TRACKERS_SERVICE_DATA.clear();
    }

    @Override
    public boolean deleteTracker(@NonNull String trackerId) {
        TRACKERS_SERVICE_DATA.remove(trackerId);
        return false;
    }

    @Override
    public void getTargets(@NonNull GetTargetsCallback callback, boolean staggered) {

    }

    @Override
    public void getTarget(@NonNull String targetId, @NonNull GetTargetsCallback callback) {

    }

    @Override
    public boolean saveTarget(@NonNull Target target) {
        return false;
    }

    @Override
    public void saveTargets(@NonNull List<Target> targets) {

    }

    @Override
    public void updateTarget(@NonNull Target target) {

    }

    @Override
    public void deleteAllTargets() {

    }

    @Override
    public boolean deleteTarget(@NonNull String targetId) {
        return false;
    }

    @Override
    public List<ScoreEntry> getEntries() {
        return null;
    }

    @Override
    public void saveEntries(List<ScoreEntry> entries) {

    }

    @Override
    public void getDaysTargetsMet(String targetId1, String targetId2, String targetId3, Calendar month, GetDaysTargetsMetCallback callback) {

    }

    @Override
    public void deleteAllEntries() {

    }

    @Override
    public void incrementScore(@NonNull String trackerId, int score) {

    }

    @Override
    public void getTrackerTotalScore(@NonNull String trackerId, @NonNull GetNumberCallback callback) {

    }

    @Override
    public void getScoreOnDay(@NonNull String trackerId, Calendar day, @NonNull GetNumberCallback callback) {

    }

    @Override
    public void getScoreOnWeek(@NonNull String trackerId, Calendar week, @NonNull GetNumberCallback callback) {

    }

    @Override
    public void getScoreOnMonth(@NonNull String trackerId, Calendar month, @NonNull GetNumberCallback callback) {

    }

    @Override
    public void getScoreOnYear(@NonNull String trackerId, Calendar year, @NonNull GetNumberCallback callback) {

    }

    @Override
    public void getTargetAverageCompletion(@NonNull String targetId, @NonNull GetNumberCallback callback) {

    }
}
