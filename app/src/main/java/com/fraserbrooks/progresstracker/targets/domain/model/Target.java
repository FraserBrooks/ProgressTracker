package com.fraserbrooks.progresstracker.targets.domain.model;

/**
 * Created by Fraser on 07/04/2018.
 */

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.datasource.source.local.Converters;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "targets",
        foreignKeys = @ForeignKey(
                entity = Tracker.class,
                parentColumns = "tracker_id",
                childColumns = "tracker_id",
                onDelete = CASCADE),
        indices = {@Index("tracker_id")})
public class Target {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "target_id")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "tracker_id")
    private String mTrackId;

    @ColumnInfo(name = "numberToAchieve")
    private int mNumberToAchieve;

    @ColumnInfo(name = "rollingTarget")
    private boolean mIsRollingTarget;

    //only used when Rolling Target = True
    @ColumnInfo(name = "interval")
    private int mInterval;
    public static final int EVERY_DAY = 1;
    public static final int EVERY_WEEK = 2;
    public static final int EVERY_MONTH = 3;
    public static final int EVERY_YEAR = 4;
    public static final int NON_RECURRING = -1;

    //only used when Rolling Target = False
    @ColumnInfo(name = "deadline")
    @TypeConverters({Converters.DayConverters.class})
    private Calendar mDeadline;

    @ColumnInfo(name = "startDate")
    @TypeConverters({Converters.DayConverters.class})
    private Calendar mStartDate;

    @ColumnInfo(name = "achieved")
    private boolean mAchieved;


    @ColumnInfo(name = "archived")
    private boolean mArchived;

    @ColumnInfo(name = "target_index")
    private int mIndex;

    @Ignore
    private int mAverageOverTime;

    @Ignore
    private int mCurrentProgressPercentage;

    @Ignore
    private String mTrackerName;


    /**
     * Use this constructor to create a new Rolling Target
     *
     * @param trackId              id of tracker this target pertains to
     * @param numberToAchieve      int: number of minutes/'counterName' to achieve to meet the target
     * @param interval             String: "DAY"/"WEEK"/"MONTH"/"YEAR"
     */
    @Ignore
    public Target(@NonNull String trackId, int numberToAchieve, int interval, int index){
        this(UUID.randomUUID().toString(), trackId, numberToAchieve, true,
                interval, Calendar.getInstance(), Calendar.getInstance(), false, false, index);
    }

    /**
     * Use this constructor to create a new Deadline Target
     *
     * @param trackId              id of tracker this target pertains to
     * @param numberToAchieve      int: number of minutes/'counterName' to achieve to meet the target
     * @param deadline             deadline day for target
     */
    @Ignore
    public Target(@NonNull String trackId, int numberToAchieve, @NonNull Calendar deadline, int index){
        this(UUID.randomUUID().toString(), trackId, numberToAchieve, true,
                Target.NON_RECURRING, deadline, Calendar.getInstance(), false, false, index);
    }


    /**
     * This constructor should only ever really be used by room (& other constructors in this class)
     *
     * @param id                 id of the target
     * @param trackId            id of the tracker
     * @param numberToAchieve    number of minutes/'counterName' to achieve to meet the target
     * @param isRollingTarget    weekly/daily/yearly target or single use target with deadline
     * @param interval           interval for rolling target: "DAY"/"WEEK"/"MONTH"/"YEAR"
     * @param deadline           deadline day for target
     * @param startDate          date target created/started
     * @param achieved           target has been hit
     * @param archived           target has been archived
     */
    public Target(@NonNull String id, @NonNull String trackId, int numberToAchieve,
                  boolean isRollingTarget, int interval, Calendar deadline,
                  Calendar startDate, boolean achieved, boolean archived, int index ) {
        mId = id;
        mTrackId = trackId;
        mNumberToAchieve = numberToAchieve;
        mIsRollingTarget = isRollingTarget;
        mInterval = interval;
        mStartDate = startDate;
        mDeadline = deadline;
        mAchieved = achieved;
        mArchived = archived;
        mIndex = index;
    }


    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getTrackId() {
        return mTrackId;
    }

    public void setTrackId(@NonNull String mTrackId) {
        this.mTrackId = mTrackId;
    }

    public int getNumberToAchieve() {
        return mNumberToAchieve;
    }

    public void setNumberToAchieve(int mNumberToAchieve) {
        this.mNumberToAchieve = mNumberToAchieve;
    }

    public boolean isRollingTarget() {
        return mIsRollingTarget;
    }

    public void setIsRollingTarget(boolean mIsRollingTarget) {
        this.mIsRollingTarget = mIsRollingTarget;
    }

    public int getInterval() {
        return mInterval;
    }

    public void setInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public Calendar getDeadline() {
        return mDeadline;
    }

    public void setDeadline(Calendar mDeadline) {
        this.mDeadline = mDeadline;
    }

    public Calendar getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Calendar startDate) {
        this.mStartDate = startDate;
    }

    public boolean isAchieved() {
        return mAchieved;
    }

    public void setAchieved(boolean mAchieved) {
        this.mAchieved = mAchieved;
    }

    public boolean isArchived() {
        return mArchived;
    }

    public void setArchived(boolean mArchived) {
        this.mArchived = mArchived;
    }

    public String getTargetTitle() {
        String val = "";
        if (getNumberToAchieve() < 60){
            val += getNumberToAchieve() + " minutes of ";
        } else{
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            float hours = ((float) getNumberToAchieve())/60f;
            String hourString = " hours of ";
            if(hours == 1){
                hourString = " hour of ";
            }
            val += df.format(hours) + hourString;
        }
        val += getTrackerName();
        switch (getInterval()){
            // TODO
            case Target.EVERY_DAY:
                val += " a day:";
                break;
            case Target.EVERY_WEEK:
                val += " a week:";
                break;
            case Target.EVERY_MONTH:
                val += " a month:";
                break;
            case Target.EVERY_YEAR:
                val += " a year:";
                break;
            default:
                val = "error";
        }
        return val;
    }

    public String getTopRightLabel(){
        return "Average:";
    }

    public String getLowerLeftLabel(){
        String val = "";
        switch (getInterval()){
            case Target.EVERY_DAY:
                val = "today:";
                break;
            case Target.EVERY_WEEK:
                val = "this week:";
                break;
            case Target.EVERY_MONTH:
                val = "this month:";
                break;
            case Target.EVERY_YEAR:
                val = "this year:";
                break;
            default:
                val = "error";
        }
        return val;
    }

    public int getAverageOverTime() {
        return mAverageOverTime;
    }

    public void setAverageOverTime(int averageOverTime) {
        this.mAverageOverTime = averageOverTime;
    }

    public int getCurrentProgressPercentage(){
        return mCurrentProgressPercentage;
    }

    public void setCurrentProgressPercentage(int currentProgressPercentage){
        this.mCurrentProgressPercentage = currentProgressPercentage;

    }


    public String getTrackerName() {
        return mTrackerName;
    }

    public void setTrackerName(String trackerName) {
        this.mTrackerName = trackerName;
    }

    public int getIndex(){return mIndex;}

    @Override
    public String toString(){
        return getTargetTitle();
    }

}
