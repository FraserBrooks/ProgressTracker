package com.fraserbrooks.progresstracker.trackers.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.datasource.source.local.Converters;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "entries",
        foreignKeys = @ForeignKey(
                entity = Tracker.class,
                parentColumns = "tracker_id",
                childColumns = "tracker_id",
                onDelete = CASCADE),
        primaryKeys = {"tracker_id", "day"})
public class ScoreEntry {

    @NonNull
    @ColumnInfo(name = "tracker_id")
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
    @TypeConverters({Converters.YearConverters.class})
    private Calendar mYear;

    @ColumnInfo(name = "score_this_day")
    private int mScoreThisDay;


    /**
     * Create a new Counter Entry
     *
     * @param day            Calendar object (time will be ignored / will not be persisted)
     * @param week            Calendar object (time will be ignored / will not be persisted)
     * @param month            Calendar object (time will be ignored / will not be persisted)
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
