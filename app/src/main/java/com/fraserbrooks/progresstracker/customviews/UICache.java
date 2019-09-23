package com.fraserbrooks.progresstracker.customviews;

import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UICache {

    public final String TAG = "UICache";

    private static final boolean ENABLED = true;

    private static UICache INSTANCE = null;

    private Map<String, VectorMasterDrawable> mTrackerIconCache;
    private Map<String, VectorMasterDrawable> mTrackerNextLevelIconCache;
    private Map<String, GradientDrawable> mTrackerGradientCache;
    //private Map<String, TrackerTimeGraphView> mTrackerTimeGraphCache;
    private Map<String, Integer> mTrackerColorCache;
    private Map<String, Integer> mTrackerNextLevelColorCache;

    private Map<String, VectorMasterDrawable> mTrackerFilledRadioButtonCache;
    private VectorMasterDrawable mTrackerEmptyRadioButtonDrawable;

    private VectorMasterDrawable mMaxLevelDrawable;

    // Prevent direct instantiation
    private UICache() {

        mTrackerIconCache = new ConcurrentHashMap<>();
        mTrackerNextLevelIconCache = new ConcurrentHashMap<>();
        mTrackerGradientCache = new ConcurrentHashMap<>();
        //mTrackerTimeGraphCache = new ConcurrentHashMap<>();
        mTrackerColorCache = new ConcurrentHashMap<>();
        mTrackerNextLevelColorCache = new ConcurrentHashMap<>();
        mTrackerFilledRadioButtonCache = new ConcurrentHashMap<>();

    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     */
    protected static UICache getInstance() {


        if (INSTANCE == null) {
            INSTANCE = new UICache();
        }
        return INSTANCE;

    }

    @Nullable
    protected VectorMasterDrawable getIconVectorDrawable(String trackerID){
        if (ENABLED)return mTrackerIconCache.get(trackerID);
        return null;
    }

    @Nullable
    protected VectorMasterDrawable getNextLevelVectorDrawable(String trackerID){
        if (ENABLED)return mTrackerNextLevelIconCache.get(trackerID);
        return null;
    }

    @Nullable
    protected VectorMasterDrawable getTrackerFilledRadioButtonDrawable(String trackerID){
        if(ENABLED) return mTrackerFilledRadioButtonCache.get(trackerID);
        return null;
    }

    @Nullable
    protected VectorMasterDrawable getTrackerRadioButtonEmpty(){
        if (ENABLED) return mTrackerEmptyRadioButtonDrawable;
        return null;
    }

    @Nullable
    protected GradientDrawable getGradientDrawable(String trackerID){
        if (ENABLED)return mTrackerGradientCache.get(trackerID);
        return null;
    }

    /*
    @Nullable
    public TrackerTimeGraphView getTimeGraphView(String trackerID){
        if (ENABLED)return mTrackerTimeGraphCache.get(trackerID);
        return null;
    }
    */

    @Nullable
    protected Integer getTrackerColor(String trackerID){
        if (ENABLED)return mTrackerColorCache.get(trackerID);
        return null;
    }

    @Nullable
    protected Integer getTrackerNextLevelColor(String trackerID){
        if (ENABLED)return mTrackerNextLevelColorCache.get(trackerID);
        return null;
    }

    @Nullable
    protected VectorMasterDrawable getMaxLevelDrawable(){
        if (ENABLED)return mMaxLevelDrawable;
        return null;
    }

    protected void storeIconVectorDrawable(@NonNull String trackerID,@NonNull VectorMasterDrawable drawable){
        mTrackerIconCache.put(trackerID, drawable);
    }

    protected void storeNextLevelIconVectorDrawable(@NonNull String trackerID,
                                                 @NonNull VectorMasterDrawable drawable){
        mTrackerNextLevelIconCache.put(trackerID, drawable);
    }

    protected void storeTrackerFilledRadioButtonDrawable(@NonNull String trackerID,
                                                         @NonNull VectorMasterDrawable drawable){
        mTrackerFilledRadioButtonCache.put(trackerID, drawable);
    }


    protected void storeGradientDrawable(@NonNull String trackerID, @NonNull GradientDrawable drawable){
        mTrackerGradientCache.put(trackerID, drawable);
    }

    /*
    public void storeTimeGraphView(@NonNull String trackerID, @NonNull TrackerTimeGraphView graphView){
        mTrackerTimeGraphCache.put(trackerID,  graphView);
    }
    */

    protected void storeTrackerColor(@NonNull String trackerID, int color){
        mTrackerColorCache.put(trackerID, color);
    }

    protected void storeTrackerNextLevelColor(@NonNull String trackerID, int color){
        mTrackerNextLevelColorCache.put(trackerID, color);
    }

    protected void storeMaxLevelDrawable(@NonNull VectorMasterDrawable drawable){
        mMaxLevelDrawable = drawable;
    }

    protected void storeTrackerEmptyRadioButton(@NonNull VectorMasterDrawable drawable){
        mTrackerEmptyRadioButtonDrawable = drawable;
    }

    protected void clearTracker(@NonNull String trackerID){

        mTrackerIconCache.remove(trackerID);
        mTrackerNextLevelIconCache.remove(trackerID);
        //mTrackerTimeGraphCache.remove(trackerID);
        mTrackerGradientCache.remove(trackerID);
        mTrackerColorCache.remove(trackerID);
        mTrackerNextLevelColorCache.remove(trackerID);


    }

    protected void clearAll(){

        mTrackerIconCache.clear();
        mTrackerNextLevelIconCache.clear();
        //mTrackerTimeGraphCache.clear();
        mTrackerGradientCache.clear();
        mTrackerColorCache.clear();
        mTrackerNextLevelColorCache.clear();

    }



}
