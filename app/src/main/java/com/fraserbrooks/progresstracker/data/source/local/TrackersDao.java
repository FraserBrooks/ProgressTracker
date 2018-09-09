package com.fraserbrooks.progresstracker.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */


/**
 * Data Access Object for the trackers, targets, and entries tables.
 */
@Dao
public abstract class TrackersDao {

    private final String TAG = "TrackersDAO";

    /**
     * Select all trackers from the tracker table.
     *
     * @return all trackers.
     */
    @Query("SELECT * FROM trackers ORDER BY trackerindex")
    public abstract List<Tracker> getTrackers();

    /**
     * Select all targets from the targets table.
     *
     * @return all targets.
     */
    @Query("SELECT * FROM targets")
    public abstract List<Target> getTargets();

    @Query("SELECT * FROM targets WHERE rollingTarget = 1 AND interval = 'DAY'")
    public abstract List<Target> getDayTargets();

    @Query("SELECT day FROM entries WHERE " +
            "trackId = (SELECT trackId FROM targets WHERE targetId = :targetId)" +
            "AND scoreThisDay >= (SELECT numberToAchieve FROM targets WHERE targetId = :targetId)" +
            "AND day BETWEEN :previousMonth AND :nextMonth")
    @TypeConverters({Converters.DayConverters.class})
    public abstract List<Calendar> getDaysTargetCompleted(String targetId, Calendar previousMonth, Calendar nextMonth);


    /**
     * Select all score entries from the entries table.
     *
     * @return all score entries.
     */
    @Query("SELECT * FROM entries")
    public abstract List<ScoreEntry> getEntries();

    /**
     * Select a tracker by id.
     *
     * @param trackerId the trackerId.
     * @return the tracker with trackerId.
     */
    @Query("SELECT * FROM trackers WHERE trackerId = :trackerId")
    public abstract Tracker getTrackerById(String trackerId);

    /**
     * Select a target by id.
     *
     * @param targetId the trackerId.
     * @return the target with targetId.
     */
    @Query("SELECT * FROM targets WHERE targetId = :targetId")
    public abstract Target getTargetById(String targetId);


    /**
     * Insert a tracker in the database. If the tracker already exists, replace it.
     *
     * @param tracker the tracker to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTracker(Tracker tracker);

    /**
     * Update a tracker.
     *
     * @param tracker   tracker to be updated
     * @return the number of trackers updated. This should always be 1.
     */
    @Update
    public abstract int updateTracker(Tracker tracker);


    /**
     * Delete a tracker by id.
     *
     * @return the number of trackers deleted. This should always be 1.
     */
    @Query("DELETE FROM trackers WHERE trackerId = :trackerId")
    public abstract int deleteTrackerById(String trackerId);

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM trackers")
    public abstract void deleteTrackers();


    /**
     * Insert a target in the database. If the target already exists, replace it.
     *
     * @param target the tracker to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTarget(Target target);

    /**
     * Update a target.
     *
     * @param target  target to be updated
     * @return the number of targets updated. This should always be 1.
     */
    @Update
    public abstract int updateTarget(Target target);


    /**
     * Delete a target by id.
     *
     * @return the number of targets deleted. This should always be 1.
     */
    @Query("DELETE FROM targets WHERE targetid = :targetId")
    public abstract int deleteTargetById(String targetId);

    /**
     * Delete all targets.
     */
    @Query("DELETE FROM targets")
    public abstract void deleteTargets();

    /**
     *  Get all-time counter value for tracker
     *
     * @return int score
     */
    @Query("SELECT SUM(scoreThisDay) FROM entries WHERE trackId = :trackerId")
    public abstract int getTotalScoreP(String trackerId);

    public int getTotalScore(String trackerId){
        int i = getTotalScoreP(trackerId);
        Log.d(TAG, "getTotalScore: for " + trackerId + " = " + i);
        return i;
    }


    /**
     * Delete all score entries.
     */
    @Query("DELETE FROM entries")
    public abstract void deleteEntries();


    /**
     *  Get score value for tracker on a given day
     *
     * @return int count
     */
    @Query("SELECT SUM(scoreThisDay) FROM entries WHERE trackId = :trackerId AND day = :day")
    @TypeConverters({Converters.DayConverters.class})
    public abstract int getScoreOnSpecificDay(String trackerId, Calendar day);

    /**
     *  Get score value for tracker on a given day
     *
     * @return int count
     */
    @Query("SELECT SUM(scoreThisDay) FROM entries WHERE trackId = :trackerId AND week = :week")
    @TypeConverters({Converters.WeekConverters.class})
    public abstract int getScoreOnSpecificWeek(String trackerId, Calendar week);

    /**
     *  Get score value for tracker on a given day
     *
     * @return int count
     */
    @Query("SELECT SUM(scoreThisDay) FROM entries WHERE trackId = :trackerId AND month = :month")
    @TypeConverters({Converters.MonthConverters.class})
    public abstract int getScoreOnSpecificMonth(String trackerId, Calendar month);

    /**
     *  Get score value for tracker on a given day
     *
     * @return int count
     */
    @Query("SELECT SUM(scoreThisDay) FROM entries WHERE trackId = :trackerId AND year = :year")
    @TypeConverters({Converters.YearConverters.class})
    public abstract int getScoreOnSpecificYear(String trackerId, Calendar year);

    /**
     *
     * @param entry
     */
    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract void insertEntry(ScoreEntry entry);

    /**
     *
     * @param trackerId    id of tracker
     * @param increment    amount to increment score by
     * @return             rows updated (should be 1)
     */
    @Query("UPDATE entries SET scoreThisDay = (scoreThisDay + :increment) WHERE trackId = :trackerId AND day = :day")
    @TypeConverters({Converters.DayConverters.class})
    abstract int  updateEntry(String trackerId, int increment, Calendar day);

    @Query("UPDATE trackers SET soFar = soFar + :increment WHERE trackerId = :trackerId")
    abstract void incrementTotal(String trackerId, int increment);

    public boolean upsertScore(String trackerId, int increment){
        Calendar cal = Calendar.getInstance();
        try{
            insertEntry(new ScoreEntry(cal, cal, cal, cal, trackerId, increment));
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "upsertScore: updating");
            updateEntry(trackerId, increment, cal);
        }


        incrementTotal(trackerId, increment);

        return true;
    }

    @Query("SELECT COUNT() FROM entries " +
            "WHERE trackId = (SELECT trackId FROM targets WHERE targetId = :targetId) " +
            "AND scoreThisDay >= :targetScore " +
            "AND day BETWEEN :start AND :end")
    @TypeConverters({Converters.DayConverters.class})
    abstract int getCountScoresOverDayTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE trackId = (SELECT trackId FROM targets WHERE targetId = :targetId) " +
            "AND week BETWEEN :start AND :end " +
            "GROUP BY week HAVING SUM(scoreThisDay) >= :targetScore)")
    @TypeConverters({Converters.WeekConverters.class})
    abstract int getCountScoresOverWeekTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE trackId = (SELECT trackId FROM targets WHERE targetId = :targetId) " +
            "AND month BETWEEN :start AND :end " +
            "GROUP BY month HAVING SUM(scoreThisDay) >= :targetScore)")
    @TypeConverters({Converters.MonthConverters.class})
    abstract int getCountScoresOverMonthTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE trackId = (SELECT trackId FROM targets WHERE targetId = :targetId) " +
            "AND year BETWEEN :start AND :end " +
            "GROUP BY year HAVING SUM(scoreThisDay) >= :targetScore)")
    @TypeConverters({Converters.YearConverters.class})
    abstract int getCountScoresOverYearTarget(String targetId, int targetScore, Calendar start, Calendar end);




}
