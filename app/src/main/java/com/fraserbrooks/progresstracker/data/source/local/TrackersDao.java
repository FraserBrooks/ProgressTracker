package com.fraserbrooks.progresstracker.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.List;

/**
 * Created by Fraser on 07/04/2018.
 */


/**
 * Data Access Object for the trackers table.
 */
@Dao
public interface TrackersDao {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM trackers")
    List<Tracker> getTrackers();

    /**
     * Select a tracker by id.
     *
     * @param trackerId the trackerId.
     * @return the tracker with trackerId.
     */
    @Query("SELECT * FROM trackers WHERE entryid = :trackerId")
    Tracker getTrackerById(String trackerId);


    /**
     * Insert a tracker in the database. If the tracker already exists, replace it.
     *
     * @param tracker the tracker to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTracker(Tracker tracker);

    /**
     * Update a tracker.
     *
     * @param tracker   tracker to be updated
     * @return the number of trackers updated. This should always be 1.
     */
    @Update
    int updateTracker(Tracker tracker);


    /**
     * Delete a tracker by id.
     *
     * @return the number of trackers deleted. This should always be 1.
     */
    @Query("DELETE FROM trackers WHERE entryid = :trackerId")
    int deleteTrackerById(String trackerId);

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM trackers")
    void deleteTrackers();


}
