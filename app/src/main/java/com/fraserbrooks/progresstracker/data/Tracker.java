package com.fraserbrooks.progresstracker.data;

/**
 * Created by Fraser on 07/04/2018.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;


import com.fraserbrooks.progresstracker.data.source.local.Converters;

import java.util.UUID;

@Entity(tableName = "trackers")
public class Tracker {

    public enum GRAPH_TYPE {DAY, WEEK, MONTH, YEAR}
    public enum TRACKER_ICON{LEVEL_UP, TEXT, HEART, STUDY,
        COMPUTER, BOOK, PENCIL, APPLE, PHONE, PEOPLE}
    public enum TRACKER_TYPE {LEVEL_UP, GRAPH, YES_NO}

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

    @NonNull
    @ColumnInfo(name = "trakertype")
    @TypeConverters({Converters.TrackerConverters.class})
    private TRACKER_TYPE mType;

    @NonNull
    @ColumnInfo(name = "defaultgraph")
    @TypeConverters({Converters.TrackerConverters.class})
    private GRAPH_TYPE mDefaultGraph;

    @NonNull
    @ColumnInfo(name = "icon")
    @TypeConverters({Converters.TrackerConverters.class})
    private TRACKER_ICON mIcon;

    @ColumnInfo(name = "icontext")
    private String mIconText;

    @ColumnInfo(name = "trackercolor")
    private int mColor;


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
    private static final int DEFAULT_INDEX = 999;





    /**
     * Use this constructor to create a new Tracker that tracks time (eg. minutes/hours).
     *
     * @param title           title of the tracker
     * @param progressionRate number of 'counterLabel's (minutes if time tracker) to get to level 8
     * @param type            display type of this tracker (level_up/graph/yes_no)
     * @param defaultGraph    default graph for this tracker in the ui
     * @param icon            tracker icon enum
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param color           int that is combined with theme values to create this icons color
     */
    @Ignore
    public Tracker(@NonNull String title, int progressionRate, @NonNull TRACKER_TYPE type,
                   @NonNull GRAPH_TYPE defaultGraph, @NonNull TRACKER_ICON icon,
                   @NonNull String iconText, int color){

        this(UUID.randomUUID().toString(), title, progressionRate, 0, 0,
                false,true, "hours", false,
                DEFAULT_INDEX, type, defaultGraph, icon, iconText,color);

    }


    /**
     * This constructor should only ever be used by room
     *
     * @param id                  id of the tracker
     * @param title               title of the tracker
     * @param progressionRate     number of 'counterLabel's (minutes if time tracker) to get to level 7
     * @param countSoFar          count of minutes/'counterName' so far
     * @param timerStartTime      time that the user started timing. Used only if 'isTimeTracker' = true
     * @param currentlyTiming     is this tracker currently being timed
     * @param timeTracker         this tracker will track time (i.e hours/minutes spent on an activity)
     * @param counterLabel        thing counted if not time (eg. 'projects done', 'miles ran', etc.)
     * @param archived            boolean: whether or not the tracker has been archived
     * @param index               index used for user defined ordering in the UI
     * @param type                enum representing the type of tracker (LEVEL_UP, GRAPH, or YES_NO)
     * @param defaultGraph        enum representing the graph to show (DAY, WEEK, MONTH, or YEAR)
     * @param icon                enum representing the icon for this particular tracker
     * @param iconText            String containing only once character/emoji for use as icon
     */
    public Tracker(@NonNull String id, @NonNull String title, int progressionRate, int countSoFar,
                    long timerStartTime, boolean currentlyTiming, boolean timeTracker,
                   @NonNull String counterLabel, boolean archived, int index,
                   @NonNull TRACKER_TYPE type, @NonNull GRAPH_TYPE defaultGraph,
                   @NonNull TRACKER_ICON icon, String iconText, int color){

        this.mId = id;
        this.mTitle = title;
        this.mProgressionRate = progressionRate;
        this.mCountSoFar = countSoFar;
        this.mTimerStartTime = timerStartTime;
        this.mCurrentlyTiming = currentlyTiming;
        this.mTimeTracker = timeTracker;
        this.mCounterLabel = counterLabel;
        this.mArchived = archived;
        this.mIndex = index;
        this.mType = type;
        this.mDefaultGraph = defaultGraph;
        this.mIcon = icon;
        this.mIconText = iconText;
        this.mColor = color;

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
        int level = 1;
        float x = getCountSoFar();
        int countToMax = getProgressionRate();
        while((x / (float) countToMax) > (1/8f)){
            level += 1;
            x -= (float) countToMax / 8;
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

        // Only display level if past max level and if level up tracker
        if (getLevel() > 8 && getType() == TRACKER_TYPE.LEVEL_UP){
            int l = getLevel();
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

    @NonNull
    public TRACKER_TYPE getType() {
        return mType;
    }

    public void setType(@NonNull TRACKER_TYPE mType) {
        this.mType = mType;
    }

    @NonNull
    public GRAPH_TYPE getDefaultGraph() {
        return mDefaultGraph;
    }

    public void setDefaultGraph(@NonNull GRAPH_TYPE mDefaultGraph) {
        this.mDefaultGraph = mDefaultGraph;
    }

    @NonNull
    public TRACKER_ICON getIcon() {
        return mIcon;
    }

    public void setIcon(@NonNull TRACKER_ICON mIcon) {
        this.mIcon = mIcon;
    }

    public String getIconText() {
        return mIconText;
    }

    public void setIconText(String mIconText) {
        this.mIconText = mIconText;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

}
