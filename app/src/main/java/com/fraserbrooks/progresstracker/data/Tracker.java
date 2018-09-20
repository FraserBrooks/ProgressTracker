package com.fraserbrooks.progresstracker.data;

/**
 * Created by Fraser on 07/04/2018.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


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

    @ColumnInfo(name = "progressionrate")
    private int mProgressionRate;

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

    @ColumnInfo(name = "trackerindex")
    private int mIndex;

    @ColumnInfo(name = "leveluptracker")
    private boolean mLevelUpTracker;

    @ColumnInfo(name = "ticklisttracker")
    private boolean  mTickListTracker;


    // Values used by the UI -----------------------
    @Ignore
    private boolean mExpanded = false;

    @Ignore
    private int mLevel;

    @Ignore
    private float mProgress;

    @Ignore
    private String mLevelToDisplay = "";

    @Ignore
    private int[] mPastEightDaysCounts;

    @Ignore
    private int[] mPastEightWeeksCounts;

    @Ignore
    private int[] mPastEightMonthsCounts;



    @Ignore
    private static final int DEFAULT_INDEX = 99;

    /**
     * Use this constructor to create a new Tracker that tracks time (eg. minutes/hours).
     *
     * @param title           title of the tracker
     * @param counterLabel    thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     * @param progressionRate number of 'counterLabel's (minutes if time tracker) to get to level 8
     * @param timeTracker     this tracker will track time (i.e hours/minutes spent on an activity)
     * @param levelUpTracker  this tracker will 'level up' as it is incremented
     * @param tickListTracker this tracker will be a yes/no tracker rather than a numerical one
     */
    @Ignore
    public Tracker(@NonNull String title, @NonNull String counterLabel, int progressionRate,
                   boolean timeTracker, boolean levelUpTracker, boolean tickListTracker) {
        this(title, UUID.randomUUID().toString(), counterLabel, DEFAULT_INDEX, progressionRate, 0,
                0,false,false, timeTracker,levelUpTracker,tickListTracker);
    }


    /**
     * This constructor should only ever be used by room
     *
     * @param title               title of the tracker
     * @param id                  id of the tracker
     * @param counterLabel        thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     * @param index               index used for user defined ordering in the UI
     * @param progressionRate     number of 'counterLabel's (minutes if time tracker) to get to level 8
     * @param countSoFar          count of minutes/'counterName' so far
     * @param timerStartTime      time that the user started timing. Used only if 'isTimeTracker' = true
     * @param currentlyTiming     currently timing. When set to false, minutes will be added to
     *                            countSoFar based on minutes since 'timerStartTime'
     * @param archived            boolean: whether or not the tracker has been archived
     * @param timeTracker     this tracker will track time (i.e hours/minutes spent on an activity)
     * @param levelUpTracker  this tracker will 'level up' as it is incremented
     * @param tickListTracker this tracker will be a yes/no tracker rather than a numerical one
     */
    public Tracker(@NonNull String title, @NonNull String id, @NonNull String counterLabel,
                   int index, int progressionRate,  int countSoFar,
                   long timerStartTime, boolean currentlyTiming,
                   boolean archived, boolean timeTracker,
                   boolean levelUpTracker, boolean tickListTracker){

        // A tracker cannot be both a levelUpTracker and a tickListTracker
        if(levelUpTracker && tickListTracker) throw new IllegalArgumentException();

        this.mId = id;
        this.mTitle = title;
        this.mIndex = index;
        this.mProgressionRate = progressionRate;
        this.mCountSoFar = countSoFar;
        this.mTimerStartTime = timerStartTime;
        this.mCurrentlyTiming = currentlyTiming;
        this.mTimeTracker = timeTracker;
        this.mCounterLabel = counterLabel;
        this.mArchived = archived;
        this.mLevelUpTracker = levelUpTracker;
        this.mTickListTracker = tickListTracker;
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

    public int getProgressionRate() {
        return mProgressionRate;
    }

    public void setProgressionRate(int progressionRate){
        mProgressionRate = progressionRate;
    }

    public int getCountSoFar() {
        return mCountSoFar;
    }

    public void setScoreSoFar(int countSoFar){
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
            setScoreSoFar(getCountSoFar() + getMinutesSinceTimerStart() );
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
        int countToMax = getProgressionRate();
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
                (getProgressionRate() == 0 && getLevel() > 0)){
            int l = getLevel();
            l = (getProgressionRate() == 0) ? l : l-8; //subtract 8 if no difficulty
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


    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public boolean isLevelUpTracker() {
        return mLevelUpTracker;
    }

    public void setLevelUpTracker(boolean levelUpTracker) {
        this.mLevelUpTracker = levelUpTracker;
    }

    public boolean isTickListTracker() {
        return mTickListTracker;
    }

    public int[] getPastEightDaysCounts() {
        return mPastEightDaysCounts;
    }

    public void setPastEightDaysCounts(int[] pastEightDaysCounts) {

        if(pastEightDaysCounts.length != 8) throw new RuntimeException();

        this.mPastEightDaysCounts = pastEightDaysCounts;
    }

    public int[] getPastEightWeeksCounts() {
        return mPastEightWeeksCounts;
}

    public void setPastEightWeekCounts(int[] pastEightWeekCounts) {

        if(pastEightWeekCounts.length != 8) throw new RuntimeException();

        this.mPastEightWeeksCounts = pastEightWeekCounts;
    }

    public int[] getPastEightMonthsCounts() {
        return mPastEightMonthsCounts;
    }

    public void setPastEightMonthCounts(int[] pastEightMonthCounts) {

        if(pastEightMonthCounts.length != 8) throw new RuntimeException();

        this.mPastEightMonthsCounts = pastEightMonthCounts;
    }
}
