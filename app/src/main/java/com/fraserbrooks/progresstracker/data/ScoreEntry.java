package com.fraserbrooks.progresstracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.source.local.Converters;

import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "entries",
        foreignKeys = @ForeignKey(
                entity = Tracker.class,
                parentColumns = "trackerId",
                childColumns = "trackId",
                onDelete = CASCADE),
        primaryKeys = {"trackId", "day"})
public class ScoreEntry {

    @NonNull
    @ColumnInfo(name = "trackId")
    private String mTrackId;

    @NonNull
    @ColumnInfo(name = "day")
    @TypeConverters({Converters.DayConverters.class})
    private Calendar mDay;

    @NonNull
    @ColumnInfo(name = "week")
    @TypeConverters({Converters.WeekConverters.class})
    private Calendar mWeek;

    @NonNull
    @ColumnInfo(name = "month")
    @TypeConverters({Converters.MonthConverters.class})
    private Calendar mMonth;

    @NonNull
    @ColumnInfo(name = "year")
    @TypeConverters({Converters.MonthConverters.class})
    private Calendar mYear;

    @ColumnInfo(name = "scoreThisDay")
    private int mScoreThisDay;


    /**
     * Create a new Counter Entry
     *
     * @param day            Calendar object where time will be ignored (will not be persisted)
     * @param week            Calendar object where time will be ignored (will not be persisted)
     * @param month            Calendar object where time will be ignored (will not be persisted)
     * @param trackId          id of tracker this entry pertains to
     * @param scoreThisDay      amount incremented on this day for this tracker
     */
    public ScoreEntry(@NonNull Calendar day, @NonNull Calendar week, @NonNull Calendar month, @NonNull Calendar year,
                      @NonNull String trackId, int scoreThisDay){
        mDay = day;
        mWeek = week;
        mMonth = month;
        mYear = year;
        mTrackId = trackId;
        mScoreThisDay = scoreThisDay;
    }

    public Calendar getDay(){
        return mDay;
    }

    public Calendar getWeek(){return mWeek;}

    public Calendar getMonth(){return mMonth;}

    public Calendar getYear(){return mYear;}

    @NonNull
    public String getTrackId(){
        return mTrackId;
    }

    public int getScoreThisDay(){
        return mScoreThisDay;
    }


}
