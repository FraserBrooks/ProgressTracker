package com.fraserbrooks.progresstracker.data.source.remote;

/**
 * Created by Fraser on 07/04/2018.
 */


import android.os.Handler;
import android.support.annotation.NonNull;


import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
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

    private static void addTracker(String title, int toMaxLevel){
        Tracker newTracker = new Tracker(title, toMaxLevel);
        TRACKERS_SERVICE_DATA.put(newTracker.getId(), newTracker);
    }

    private static void addTracker(String title, int toMaxLevel, String counterName){
        Tracker newTracker = new Tracker(title, toMaxLevel, counterName);
        TRACKERS_SERVICE_DATA.put(newTracker.getId(), newTracker);
    }

    /**
     * Note: {@link LoadTrackersCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getTrackers(@NonNull final LoadTrackersCallback callback) {
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
     * Note: {@link GetTrackerCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getTracker(@NonNull String trackerId, @NonNull final GetTrackerCallback callback) {
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
    public void saveTracker(@NonNull Tracker tracker) {
        TRACKERS_SERVICE_DATA.put(tracker.getId(), tracker);
    }

    @Override
    public void refreshTrackers() {
        // Not required because the {@link Repository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTrackers() {
        TRACKERS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTracker(@NonNull String trackerId) {
        TRACKERS_SERVICE_DATA.remove(trackerId);
    }
}
