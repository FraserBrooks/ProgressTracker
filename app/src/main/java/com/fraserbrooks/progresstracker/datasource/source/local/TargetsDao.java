package com.fraserbrooks.progresstracker.datasource.source.local;

import com.fraserbrooks.progresstracker.targets.domain.model.Target;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.TypeConverters;

@SuppressWarnings("unused")
@Dao
abstract class TargetsDao implements BaseDao<Target> {

    private static final String TAG = "TargetsDao";

    /**
     * Select all targets from the targets table
     *
     * @return all targets
     */
    @Query("SELECT * FROM targets ORDER BY target_index")
    abstract LiveData<List<Target>> getTargets();

    /**
     * Select a target by id.
     *
     * @param targetId the target id.
     * @return the target with matching targetId or null if none exists.
     */
    @Query("SELECT * FROM targets WHERE target_id = :targetId")
    abstract LiveData<Target> getTargetById(String targetId);


    /**
     * Select a target by id.
     *
     * @param targetId the target id.
     * @return the target with matching targetId or null if none exists.
     */
    @Query("SELECT * FROM targets WHERE target_id = :targetId")
    abstract Target getTargetSync(String targetId);


    /**
     * Delete all targets.
     */
    @Query("DELETE FROM targets")
    abstract void deleteAllTargets();


    @Query("SELECT day FROM entries WHERE " +
            "tracker_id = (SELECT tracker_id FROM targets WHERE target_id = :targetId)" +
            "AND score_this_day >= (SELECT numberToAchieve FROM targets WHERE target_id = :targetId)" +
            "AND day BETWEEN :previousMonth AND :nextMonth")
    @TypeConverters({Converters.DayConverters.class})
    abstract LiveData<List<Date>> getDaysTargetCompleted(String targetId, Calendar previousMonth, Calendar nextMonth);


    @Query("SELECT COUNT() FROM entries " +
            "WHERE tracker_id = (SELECT tracker_id FROM targets WHERE target_id = :targetId) " +
            "AND score_this_day >= :targetScore " +
            "AND day BETWEEN :start AND :end")
    @TypeConverters({Converters.DayConverters.class})
    abstract int getCountScoresOverDayTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE tracker_id= (SELECT tracker_id FROM targets WHERE target_id = :targetId) " +
            "AND week BETWEEN :start AND :end " +
            "GROUP BY week HAVING SUM(score_this_day) >= :targetScore)")
    @TypeConverters({Converters.WeekConverters.class})
    abstract int getCountScoresOverWeekTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE tracker_id = (SELECT tracker_id FROM targets WHERE target_id = :targetId) " +
            "AND month BETWEEN :start AND :end " +
            "GROUP BY month HAVING SUM(score_this_day) >= :targetScore)")
    @TypeConverters({Converters.MonthConverters.class})
    abstract int getCountScoresOverMonthTarget(String targetId, int targetScore, Calendar start, Calendar end);

    @Query("SELECT COUNT() FROM (SELECT * FROM entries " +
            "WHERE tracker_id = (SELECT tracker_id FROM targets WHERE target_id = :targetId) " +
            "AND year BETWEEN :start AND :end " +
            "GROUP BY year HAVING SUM(score_this_day) >= :targetScore)")
    @TypeConverters({Converters.YearConverters.class})
    abstract int getCountScoresOverYearTarget(String targetId, int targetScore, Calendar start, Calendar end);


}
