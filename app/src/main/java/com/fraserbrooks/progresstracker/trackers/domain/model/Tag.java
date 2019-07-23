package com.fraserbrooks.progresstracker.trackers.domain.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Created by Fraser on 01/01/2019.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Entity(tableName = "tags")
public class Tag {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tag_id")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "display_name")
    private String mDisplayName;


    /**
     *  create a new tag
     *
     * @param name         display name of this tag in the ui
     */
    public Tag(@NonNull String name){
        this(UUID.randomUUID().toString(), name);
    }

    /**
     * Full constructor used by room
     *
     * @param id           id of this tag
     * @param name         display name of this tag in the ui
     */
    protected Tag(@NonNull String id, @NonNull String name){

        this.mId = id;
        this.mDisplayName = name;

    }


    @NonNull
    public String getTagId(){
        return mId;
    }

    @NonNull
    public String getDisplayName(){
        return mDisplayName;
    }

    public void setDisplayName(@NonNull String name){
        this.mDisplayName = name;
    }

}
