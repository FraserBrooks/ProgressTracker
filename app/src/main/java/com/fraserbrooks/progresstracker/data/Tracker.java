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


import java.util.HashMap;
import java.util.UUID;

@Entity(tableName = "trackers")
public class Tracker {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "tomaxlevel")
    private int mCountToMaxLevel;

    @ColumnInfo(name = "sofar")
    private int mCountSoFar;

    @NonNull
    @ColumnInfo(name = "addedeachday")
    private HashMap<String, Integer> mAddedEachDay;

    @NonNull
    @ColumnInfo(name = "addedeachweek")
    private HashMap<String, Integer> mAddedEachWeek;

    @ColumnInfo(name = "timerstart")
    private long mTimerStartTime;

    @ColumnInfo(name = "currentlytiming")
    private boolean mCurrentlyTiming;

    @ColumnInfo(name = "istimetracker")
    private final boolean mTimeTracker;

    @NonNull
    @ColumnInfo(name = "counterLabel")
    private String mCounterLabel;

    @Ignore
    private boolean mExpanded;

    @Ignore
    private static final int COUNT_TO_MAX_NO_DIFFICULTY_SET = 4800;


    /**
     * Use this constructor to create a new Tracker that tracks time (eg. minutes/hours).
     *
     * @param title           title of the tracker
     * @param toMaxLevel      number of minutes/'counterName' to max level
     */
    @Ignore
    public Tracker(@NonNull String title, int toMaxLevel) {
        this(title, UUID.randomUUID().toString(), toMaxLevel, 0,
                new HashMap<String, Integer>(),
                new HashMap<String, Integer>(),
                0, false, true, "hours");
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
                new HashMap<String, Integer>(),
                new HashMap<String, Integer>(),
                0, false, false, counterLabel);
    }


    /**
     * This constructor should only ever really be used by room
     *
     * @param title           title of the tracker
     * @param id              id of the tracker
     * @param countToMaxLevel      number of minutes/'counterName' to max level
     * @param countSoFar      the count of minutes/'counterName' so far
     * @param addedEachDay    a map of days to the change to count for that day
     * @param addedEachWeek   a map of weeks to the change to count for that week
     * @param timerStartTime  time that the user started timing. Used only if 'isTimeTracker' = true
     * @param currentlyTiming currently timing. When set to false, minutes will be added to
     *                        countSoFar based on minutes since 'timerStartTime'
     * @param timeTracker   count refers to minutes/hours else count refers to 'counterName'
     * @param counterLabel     thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     */
    public Tracker(@NonNull String title, @NonNull String id,
                   int countToMaxLevel,  int countSoFar,
                   @NonNull HashMap<String, Integer> addedEachDay,
                   @NonNull HashMap<String, Integer> addedEachWeek,
                   long timerStartTime,
                   boolean currentlyTiming, boolean timeTracker,
                   @Nullable String counterLabel) {
        this.mId = id;
        this.mTitle = title;
        this.mCountToMaxLevel = countToMaxLevel;
        this.mCountSoFar = countSoFar;
        this.mAddedEachDay = addedEachDay;
        this.mAddedEachWeek = addedEachWeek;
        this.mTimerStartTime = timerStartTime;
        this.mCurrentlyTiming = currentlyTiming;
        this.mTimeTracker = timeTracker;
        this.mCounterLabel = counterLabel;
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

    @NonNull
    public int getCountToMaxLevel() {
        return mCountToMaxLevel;
    }

    public void setCountToMaxLevel(int countToMaxLevel){
        mCountToMaxLevel = countToMaxLevel;
    }

    @NonNull
    public int getCountSoFar() {
        return mCountSoFar;
    }

    public void setCountSoFar(int countSoFar){
        mCountSoFar = countSoFar;
    }

    @NonNull
    public HashMap<String, Integer> getAddedEachDay() {
        return mAddedEachDay;
    }

    @NonNull
    public HashMap<String, Integer> getAddedEachWeek() {
        return mAddedEachWeek;
    }

    @NonNull
    public long getTimerStartTime() {
        return mTimerStartTime;
    }

    public void setmTimerStartTime(Long timerStartTime){
        if(!isCurrentlyTiming()){
            mTimerStartTime = timerStartTime;
            mCurrentlyTiming = true;
        }
    }

    @NonNull
    public boolean isCurrentlyTiming() {
        return mCurrentlyTiming;
    }

    @NonNull
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

    public int getMinutesSinceTimerStart(){
        long timePassedMilli = System.currentTimeMillis() - getTimerStartTime();
        long timePassedMinutes = (timePassedMilli / 1000) / 60;
        return (int) timePassedMinutes;
    }

    public int getLevel(){
        int level = 0;
        float score = getCountSoFar();
        int countToMax = (getCountToMaxLevel() != 0)
                ? getCountToMaxLevel() : COUNT_TO_MAX_NO_DIFFICULTY_SET;
        while((score / (float) countToMax) > (1/8f)){
            level += 1;
            score -= (float) countToMax / 8;
        }
        return level;
    }

    public float getPercentageToNextLevel(){
        float soFar = getCountSoFar();
        int countToMax = (getCountToMaxLevel() != 0)
                ? getCountToMaxLevel() : COUNT_TO_MAX_NO_DIFFICULTY_SET;
        while((soFar / ((float) countToMax)) > (1/8f)){
            soFar -= (float) countToMax / 8;
        }
        float decimal = soFar / ((float) countToMax / 8);
        if (decimal >= 1f){
            decimal = 0.999f;
        }
        return decimal;
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
