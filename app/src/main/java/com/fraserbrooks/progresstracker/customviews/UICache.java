package com.fraserbrooks.progresstracker.customviews;

import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fraserbrooks.progresstracker.trackers.view.trackergraphs.TrackerTimeGraphView;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UICache {

    public final String TAG = "UICache";

    private final boolean ENABLED = true;

    private static UICache INSTANCE = null;

    private Map<String, VectorMasterDrawable> mTrackerIconCache;
    private Map<String, VectorMasterDrawable> mTrackerNextLevelIconCache;
    private Map<String, GradientDrawable> mTrackerGradientCache;
    private Map<String, TrackerTimeGraphView> mTrackerTimeGraphCache;
    private Map<String, Integer> mTrackerColorCache;
    private Map<String, Integer> mTrackerNextLevelColorCache;

    private VectorMasterDrawable mMaxLevelDrawable;

    // Prevent direct instantiation
    private UICache() {

        mTrackerIconCache = new ConcurrentHashMap<>();
        mTrackerNextLevelIconCache = new ConcurrentHashMap<>();
        mTrackerGradientCache = new ConcurrentHashMap<>();
        mTrackerTimeGraphCache = new ConcurrentHashMap<>();
        mTrackerColorCache = new ConcurrentHashMap<>();
        mTrackerNextLevelColorCache = new ConcurrentHashMap<>();

    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     */
    public static UICache getInstance() {


        if (INSTANCE == null) {
            INSTANCE = new UICache();
        }
        return INSTANCE;

    }

    @Nullable
    public VectorMasterDrawable getIconVectorDrawable(String trackerID){
        if (ENABLED)return mTrackerIconCache.get(trackerID);
        return null;
    }

    @Nullable
    public VectorMasterDrawable getNextLevelVectorDrawable(String trackerID){
        if (ENABLED)return mTrackerNextLevelIconCache.get(trackerID);
        return null;
    }

    @Nullable
    public GradientDrawable getGradientDrawable(String trackerID){
        if (ENABLED)return mTrackerGradientCache.get(trackerID);
        return null;
    }

    @Nullable
    public TrackerTimeGraphView getTimeGraphView(String trackerID){
        if (ENABLED)return mTrackerTimeGraphCache.get(trackerID);
        return null;
    }

    @Nullable
    public Integer getTrackerColor(String trackerID){
        if (ENABLED)return mTrackerColorCache.get(trackerID);
        return null;
    }

    @Nullable
    public Integer getTrackerNextLevelColor(String trackerID){
        if (ENABLED)return mTrackerNextLevelColorCache.get(trackerID);
        return null;
    }

    @Nullable
    public VectorMasterDrawable getMaxLevelDrawable(){
        if (ENABLED)return mMaxLevelDrawable;
        return null;
    }

    public void storeIconVectorDrawable(@NonNull String trackerID,@NonNull VectorMasterDrawable drawable){
        mTrackerIconCache.put(trackerID, drawable);
    }

    public void storeNextLevelIconVectorDrawable(@NonNull String trackerID,
                                                 @NonNull VectorMasterDrawable drawable){
        mTrackerNextLevelIconCache.put(trackerID, drawable);
    }

    public void storeGradientDrawable(@NonNull String trackerID, @NonNull GradientDrawable drawable){
        mTrackerGradientCache.put(trackerID, drawable);
    }

    public void storeTimeGraphView(@NonNull String trackerID, @NonNull TrackerTimeGraphView graphView){
        mTrackerTimeGraphCache.put(trackerID,  graphView);
    }

    public void storeTrackerColor(@NonNull String trackerID, int color){
        mTrackerColorCache.put(trackerID, color);
    }

    public void storeTrackerNextLevelColor(@NonNull String trackerID, int color){
        mTrackerNextLevelColorCache.put(trackerID, color);
    }

    public void storeMaxLevelDrawable(@NonNull VectorMasterDrawable drawable){
        mMaxLevelDrawable = drawable;
    }


    public void clearTracker(@NonNull String trackerID){

        mTrackerIconCache.remove(trackerID);
        mTrackerNextLevelIconCache.remove(trackerID);
        mTrackerTimeGraphCache.remove(trackerID);
        mTrackerGradientCache.remove(trackerID);
        mTrackerColorCache.remove(trackerID);
        mTrackerNextLevelColorCache.remove(trackerID);


    }

    public void clearAll(){

        mTrackerIconCache.clear();
        mTrackerNextLevelIconCache.clear();
        mTrackerTimeGraphCache.clear();
        mTrackerGradientCache.clear();
        mTrackerColorCache.clear();
        mTrackerNextLevelColorCache.clear();

    }



}
