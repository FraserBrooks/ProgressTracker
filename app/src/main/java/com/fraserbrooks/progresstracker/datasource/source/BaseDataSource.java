package com.fraserbrooks.progresstracker.datasource.source;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public interface BaseDataSource<T> {


    /**
     *  Data ------------------------------------------------------------------------
     */
    LiveData<List<T>> getData();

    LiveData<T> getItem(String id);

    void saveData(@NonNull List<T> data);

    void saveItem(@NonNull T item);

    void updateItem(@NonNull T item);

    void deleteItem(@NonNull T item);

    void deleteAllItems();



}
