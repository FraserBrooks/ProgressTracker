package com.fraserbrooks.progresstracker.customviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

public class UIUtils {

    public final String TAG = "UIUtils";

    private static UICache uiCache = UICache.getInstance();

    public static GradientDrawable getTrackerGradientDrawable(Context c, Tracker t){

        // Check in cache, return if found

        GradientDrawable gd = uiCache.getGradientDrawable(t.getId());

        if(gd != null) return gd;
        // else create using color utils and add to cache

        int trackerColor = getTrackerColor(c, t);

        gd = ColorUtils.getGradientDrawable(c,trackerColor);
        uiCache.storeGradientDrawable(t.getId(), gd);
        return gd;
    }


    public static VectorMasterDrawable getTrackerIcon(Context c, Tracker t){

        // Check in cache, return if found
        VectorMasterDrawable trackerIcon = uiCache.getIconVectorDrawable(t.getId());

        if(trackerIcon != null) return trackerIcon;
        // else create using color utils and add to cache

        trackerIcon = ColorUtils.getTrackerIcon(c, t.getIcon());

        int trackerColor = getTrackerColor(c, t);
        ColorUtils.setVectorColor(trackerIcon, trackerColor);

        uiCache.storeIconVectorDrawable(t.getId(), trackerIcon);

        return trackerIcon;
    }

    public static VectorMasterDrawable getTrackerNextLevelIcon(Context c, Tracker t){

        // Check in cache, return if found
        VectorMasterDrawable trackerIcon = uiCache.getNextLevelVectorDrawable(t.getId());

        if(trackerIcon != null) return trackerIcon;
        // else create using color utils and add to cache

        trackerIcon = ColorUtils.getTrackerIcon(c, t.getIcon());

        int trackerColor = getTrackerNextLevelColor(c, t);
        ColorUtils.setVectorColor(trackerIcon, trackerColor);

        uiCache.storeNextLevelIconVectorDrawable(t.getId(), trackerIcon);

        return trackerIcon;
    }

    public static int getTrackerColor(Context c, Tracker t){

        // Check in cache, return if found
        Integer trackerColor = uiCache.getTrackerColor(t.getId());

        if(trackerColor != null) return trackerColor;
        // else create using color utils and add to cache

        // calculate color and store in cache
        trackerColor = ColorUtils.getTrackerColor(c, t);
        uiCache.storeTrackerColor(t.getId(), trackerColor);

        return trackerColor;

    }

    public static int getTrackerNextLevelColor(Context c, Tracker t){

        // Check in cache, return if found
        Integer nextLevelColor = uiCache.getTrackerNextLevelColor(t.getId());

        if(nextLevelColor != null) return nextLevelColor;
        // else create using color utils and add to cache

        // calculate color and store in cache
        nextLevelColor = ColorUtils.getLevelDefinedColor(c, t.getLevel() + 1);
        uiCache.storeTrackerNextLevelColor(t.getId(), nextLevelColor);

        return nextLevelColor;

    }

    public static VectorMasterDrawable getMaxLevelDrawable(Context c){

        // Check in cache, return if found
        VectorMasterDrawable maxLevelIcon = uiCache.getMaxLevelDrawable();

        if(maxLevelIcon != null) return maxLevelIcon;
        // else create using color utils and add to cache

        int maxLevelColor = ColorUtils.getLevelDefinedColor(c, 99);
        maxLevelIcon = ColorUtils.getTrackerIcon(c, Tracker.ICON_LEVEL);

        ColorUtils.setVectorColor(maxLevelIcon, maxLevelColor);

        uiCache.storeMaxLevelDrawable(maxLevelIcon);

        return maxLevelIcon;
    }

    public static VectorMasterDrawable getTrackerFilledRadioButtonDrawable(Context c, Tracker t){

        // Check in cache, return if found
        VectorMasterDrawable filledRadioButtonDrawable = uiCache.getTrackerFilledRadioButtonDrawable(t.getId());

        if(filledRadioButtonDrawable != null) return filledRadioButtonDrawable;
        // else create using color utils and add to cache

        filledRadioButtonDrawable = new VectorMasterDrawable(c, R.drawable.ico_nested_circles);
        int trackerColor = getTrackerColor(c, t);
        ColorUtils.setColoredCircle(c, filledRadioButtonDrawable, trackerColor);

        uiCache.storeTrackerFilledRadioButtonDrawable(t.getId(), filledRadioButtonDrawable);

        return filledRadioButtonDrawable;
    }

    public static VectorMasterDrawable getTrackerEmptyRadioButtonDrawable(Context c){

        // Check in cache, return if found
        VectorMasterDrawable emptyRadioButtonDrawable = uiCache.getTrackerRadioButtonEmpty();

        if(emptyRadioButtonDrawable != null) return emptyRadioButtonDrawable;
        // else create using color utils and add to cache

        emptyRadioButtonDrawable = new VectorMasterDrawable(c, R.drawable.ico_nested_circles);
        ColorUtils.setBlankCircle(c, emptyRadioButtonDrawable);
        uiCache.storeTrackerEmptyRadioButton(emptyRadioButtonDrawable);

        return emptyRadioButtonDrawable;

    }


}
