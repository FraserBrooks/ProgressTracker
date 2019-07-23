package com.fraserbrooks.progresstracker.datasource.source.local;

import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

interface BaseDao<T> {


    /**
     * Insert a T into the database. If the T already exists, replace it.
     *
     * @param item        the T to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemOnConflictReplace(T item);

    /**
     * Insert a T into the database. If the T already exists throw exception
     *
     * @param item        the T to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertItemOnConflictException(T item);

    /**
     * Insert a list of Ts into the database. If a T already exists, replace it.
     *
     * @param items        the Ts to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<T> items);


    /**
     * Update a T.
     *
     * @param   item      T to be updated
     * @return         the number of T's updated. This should always be 1.
     */
    @Update
    int updateItem(T item);

    /**
     * Delete a T.
     *
     * @param   item      T to be deleted
     */
    @Delete
    void deleteItem(T item);



}
