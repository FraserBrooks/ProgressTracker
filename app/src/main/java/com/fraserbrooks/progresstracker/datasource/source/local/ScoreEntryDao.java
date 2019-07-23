package com.fraserbrooks.progresstracker.datasource.source.local;

import com.fraserbrooks.progresstracker.trackers.domain.model.ScoreEntry;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

/**
 * Created by Fraser on 11/01/2019.
 */


/**
 * Data Access Object for the trackers.
 */
@Dao
abstract class ScoreEntryDao implements BaseDao<ScoreEntry> {

    private static final String TAG = "ScoreEntryDao";


    /**
     * Select all ScoreEntries from the entries table.
     *
     * @return all ScoreEntries.
     */
    @Query("SELECT * FROM entries")
    abstract List<ScoreEntry> getEntries();



}
