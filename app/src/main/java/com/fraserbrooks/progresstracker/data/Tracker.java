package com.fraserbrooks.progresstracker.data;

/**
 * Created by Fraser on 07/04/2018.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

@Entity(tableName = "trackers")
public class Tracker {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "trackerId")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "tomaxlevel")
    private int mCountToMaxLevel;

    @ColumnInfo(name = "sofar")
    private int mCountSoFar;

    @ColumnInfo(name = "timerstart")
    private long mTimerStartTime;

    @ColumnInfo(name = "currentlytiming")
    private boolean mCurrentlyTiming;

    @ColumnInfo(name = "istimetracker")
    private final boolean mTimeTracker;

    @NonNull
    @ColumnInfo(name = "counterLabel")
    private String mCounterLabel;

    @ColumnInfo(name = "archived")
    private boolean mArchived;

    @Ignore
    private boolean mExpanded;

    @Ignore
    private static final int COUNT_TO_MAX_WHEN_NO_DIFFICULTY_SET = 4800;

    @Ignore
    private int mLevel;

    @Ignore
    private float mProgress;

    @Ignore
    private String mLevelToDisplay = "";

    /**
     * Use this constructor to create a new Tracker that tracks time (eg. minutes/hours).
     *
     * @param title           title of the tracker
     * @param toMaxLevel      number of minutes/'counterName' to max level
     */
    @Ignore
    public Tracker(@NonNull String title, int toMaxLevel) {
        this(title, UUID.randomUUID().toString(), toMaxLevel, 0,
                0, false, true, "hours", false);
    }

    /**
     * Use this constructor to create a new Tracker that tracks something
     * other than time (eg. 'projects completed', 'paintings sold', 'miles ran').
     *
     * @param title           title of the tracker
     * @param toMaxLevel      number of minutes/'counterName' to max level
     * @param counterLabel     thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     */
    @Ignore
    public Tracker(@NonNull String title, int toMaxLevel, String counterLabel){
        this(title, UUID.randomUUID().toString(), toMaxLevel, 0,
                0, false, false, counterLabel, false);
    }


    /**
     * This constructor should only ever really be used by room
     *
     * @param title           title of the tracker
     * @param id              id of the tracker
     * @param countToMaxLevel      number of minutes/'counterName' to max level
     * @param countSoFar      the count of minutes/'counterName' so far
     * @param timerStartTime  time that the user started timing. Used only if 'isTimeTracker' = true
     * @param currentlyTiming currently timing. When set to false, minutes will be added to
     *                        countSoFar based on minutes since 'timerStartTime'
     * @param timeTracker   count refers to minutes/hours else count refers to 'counterName'
     * @param counterLabel     thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     * @param archived         boolean: whether or not the tracker has been archived
     */
    public Tracker(@NonNull String title, @NonNull String id,
                   int countToMaxLevel,  int countSoFar,
                   long timerStartTime,
                   boolean currentlyTiming, boolean timeTracker,
                   String counterLabel, boolean archived) {
        this.mId = id;
        this.mTitle = title;
        this.mCountToMaxLevel = countToMaxLevel;
        this.mCountSoFar = countSoFar;
        this.mTimerStartTime = timerStartTime;
        this.mCurrentlyTiming = currentlyTiming;
        this.mTimeTracker = timeTracker;
        this.mCounterLabel = counterLabel;
        this.mArchived = archived;
        mExpanded = false;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getCountToMaxLevel() {
        return mCountToMaxLevel;
    }

    public void setCountToMaxLevel(int countToMaxLevel){
        mCountToMaxLevel = countToMaxLevel;
    }

    public int getCountSoFar() {
        return mCountSoFar;
    }

    public void setCountSoFar(int countSoFar){
        mCountSoFar = countSoFar;
    }

    public long getTimerStartTime() {
        return mTimerStartTime;
    }

    public void setmTimerStartTime(Long timerStartTime){
        if(!isCurrentlyTiming()){
            mTimerStartTime = timerStartTime;
            mCurrentlyTiming = true;
        }
    }

    public boolean isCurrentlyTiming() {
        return mCurrentlyTiming;
    }

    public boolean isTimeTracker() {
        return mTimeTracker;
    }

    @NonNull
    public String getCounterLabel() {
        return mCounterLabel;
    }

    public void setCounterLabel(String counterLabel){
        mCounterLabel = counterLabel;
    }


    public boolean isArchived() {
        return mArchived;
    }

    public void setArchived(boolean b){
        mArchived = b;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded){
        mExpanded = expanded;
    }


    public boolean stopTiming(){
        if(isCurrentlyTiming()){
            setCountSoFar(getCountSoFar() + getMinutesSinceTimerStart() );
            mCurrentlyTiming = false;
            return true;
        }else{
            return false;
        }

    }

    private int getMinutesSinceTimerStart(){
        long timePassedMilli = System.currentTimeMillis() - getTimerStartTime();
        long timePassedMinutes = (timePassedMilli / 1000) / 60;
        return (int) timePassedMinutes;
    }


    public void setUiValues(){
        int level = 0;
        float score = getCountSoFar();
        int countToMax = (getCountToMaxLevel() != 0)
                ? getCountToMaxLevel() : COUNT_TO_MAX_WHEN_NO_DIFFICULTY_SET;
        while((score / (float) countToMax) > (1/8f)){
            level += 1;
            score -= (float) countToMax / 8;
        }
        mLevel = level;

        float soFar = getCountSoFar();
        while((soFar / ((float) countToMax)) > (1/8f)){
            soFar -= (float) countToMax / 8;
        }
        float decimal = soFar / ((float) countToMax / 8);
        if (decimal >= 1f){
            decimal = 1f;
        }
        mProgress = decimal;

        String levelToDisplay;

        // Only display level if past max level or if no difficulty is set
        if (getLevel() > 8 ||
                (getCountToMaxLevel() == 0 && getLevel() > 0)){
            int l = getLevel();
            l = (getCountToMaxLevel() == 0) ? l : l-8; //subtract 8 if no difficulty
            levelToDisplay = "" + l;
        } else{
            levelToDisplay = ""; // Don't display level
        }
        mLevelToDisplay = levelToDisplay;
    }

    public int getLevel(){
        return mLevel;
    }

    public float getPercentageToNextLevel(){
        return mProgress;
    }

    public String getLevelToDisplay(){
        return mLevelToDisplay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracker tracker = (Tracker) o;
        return tracker.getId().equals(getId());
    }



    @Override
    public String toString() {
        return "Tracker with title " + mTitle;
    }




}
