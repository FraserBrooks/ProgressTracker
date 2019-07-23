package com.fraserbrooks.progresstracker.datasource.source.local;


import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;

import com.fraserbrooks.progresstracker.trackers.domain.model.ScoreEntry;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */


/**
 * Data Access Object for the trackers.
 */
@Dao
abstract class TrackersDao implements BaseDao<Tracker> {

    private static final String TAG = "TrackersDAO";

    /**
     * Select all trackers from the tracker table.
     *
     * @return all trackers.
     */
    @Query("SELECT * FROM trackers ORDER BY `index`")
    abstract LiveData<List<Tracker>> getTrackers();

    /**
     * Select a tracker by id.
     *
     * @param trackerId the trackerId.
     * @return the tracker with trackerId.
     */
    @Query("SELECT * FROM trackers WHERE tracker_id = :trackerId")
    abstract LiveData<Tracker> getTrackerById(String trackerId);


    /**
     * Select a tracker by id.
     *
     * @param trackerId the trackerId.
     * @return the tracker with trackerId.
     */
    @Query("SELECT * FROM trackers WHERE tracker_id = :trackerId")
    abstract Tracker getTrackerSync(String trackerId);

    /**
     * Delete all trackers.
     */
    @Query("DELETE FROM trackers")
    abstract void deleteAllTrackers();

    /**
     *  Get total score/value/count for tracker
     *
     * @return int score
     */
    @Query("SELECT SUM(score_this_day) FROM entries WHERE tracker_id = :trackerId")
    abstract int getTrackerTotalScore(String trackerId);


    //   Score Entry -----------------------------
    /**
     *
     * @param entry  ScoreEntry to insert
     */
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract void insertEntry(ScoreEntry entry);

    /**
     *
     * @param trackerId    id of tracker
     * @param increment    amount to increment score by
     * @return             rows updated (should be 1)
     */
    @Query("UPDATE entries SET score_this_day = (score_this_day + :increment) " +
            "WHERE tracker_id = :trackerId AND day = :day")
    @TypeConverters({Converters.DayConverters.class})
    abstract int incrementEntry(String trackerId, int increment, Calendar day);

    @Query("UPDATE trackers SET score = :total WHERE tracker_id = :trackerId")
    abstract void setTotal(String trackerId, int total);

    @Query("UPDATE entries SET score_this_day = 0 WHERE tracker_id = :trackerId AND week = :week")
    @TypeConverters({Converters.WeekConverters.class})
    abstract void clearTrackerWeek(String trackerId, Calendar week);

    @Query("UPDATE entries SET score_this_day = 0 WHERE tracker_id = :trackerId AND month = :month")
    @TypeConverters({Converters.MonthConverters.class})
    abstract void clearTrackerMonth(String trackerId, Calendar month);


    @Transaction
    void incrementOrAddTrackerScore(String trackerId, int increment, Calendar cal){

        Log.d(TAG, "qwert incrementTrackerScore: incrementing tracker score");

        int totalB4 = getTrackerTotalScore(trackerId);

        Log.d(TAG, "incrementTrackerScore: total b4 = " + totalB4);

        int changed = incrementEntry(trackerId, increment, cal);

        if(changed == 0){
            Log.d(TAG, "qwert incrementTrackerScore: no entry for today. Creating new entry");
            insertEntry(new ScoreEntry(cal, cal, cal, cal, trackerId, increment));
        }else{
            Log.d(TAG, "qwert incrementTrackerScore: entry incremented successfully");
        }

        int after = getTrackerTotalScore(trackerId);

        Log.d(TAG, "incrementTrackerScore: total after = " + after);

        setTotal(trackerId, after);

    }

    void insertOrIncrementScore(String trackerId, int increment){

        Calendar cal = Calendar.getInstance();

        incrementOrAddTrackerScore(trackerId, increment, cal);

        /**
        Log.d(TAG, "asdff insertOrIncrementScore: incrementing");
        Calendar cal = Calendar.getInstance();
        try {
            insertEntry(new ScoreEntry(cal, cal, cal, cal, trackerId, increment));
        } catch (SQLiteConstraintException e){
            Log.d(TAG, "asdff insertOrIncrementScore: updating because entry for today already exists");
            incrementEntry(trackerId, increment, cal);
        }

        Log.d(TAG, "asdff insertOrIncrementScore: updating Tracker record");
        Tracker t = getTrackerSync(trackerId);
        Log.d(TAG, "qwert insertOrIncrementScore: b4 score: " + getTrackerTotalScore(t.getId()));
        t.setScoreSoFar(getTrackerTotalScore(t.getId()));
        Log.d(TAG, "qwert insertOrIncrementScore: aft score: " + t.getScoreSoFar());
        insertItemOnConflictReplace(t);
         **/
    }

    @Transaction
    void clearWeek(String trackerId){
        Calendar cal = Calendar.getInstance();
        clearTrackerWeek(trackerId, cal);
        setTotal(trackerId, getTrackerTotalScore(trackerId));
    }

    @Transaction
    void clearMonth(String trackerId){
        Calendar cal = Calendar.getInstance();
        clearTrackerMonth(trackerId, cal);
        setTotal(trackerId, getTrackerTotalScore(trackerId));
    }

    /**
     *  Get score for tracker on a given day
     *
     * @return int score
     */
    @Query("SELECT SUM(score_this_day) FROM entries WHERE tracker_id = :trackerId AND day = :day")
    @TypeConverters({Converters.DayConverters.class})
    abstract int getScoreOnSpecificDay(String trackerId, Calendar day);

    /**
     *  Get score for tracker on a given week
     *
     * @return int score
     */
    @Query("SELECT SUM(score_this_day) FROM entries WHERE tracker_id = :trackerId AND week = :week")
    @TypeConverters({Converters.WeekConverters.class})
    abstract int getScoreOnSpecificWeek(String trackerId, Calendar week);

    /**
     *  Get score for tracker on a given month
     *
     * @return int score
     */
    @Query("SELECT SUM(score_this_day) FROM entries WHERE tracker_id = :trackerId AND month = :month")
    @TypeConverters({Converters.MonthConverters.class})
    abstract int getScoreOnSpecificMonth(String trackerId, Calendar month);

    /**
     *  Get score value for tracker on a given day
     *
     * @return int count
     */
    @Query("SELECT SUM(score_this_day) FROM entries WHERE tracker_id = :trackerId AND year = :year")
    @TypeConverters({Converters.YearConverters.class})
    abstract int getScoreOnSpecificYear(String trackerId, Calendar year);

}
