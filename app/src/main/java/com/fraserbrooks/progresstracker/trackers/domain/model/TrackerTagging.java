package com.fraserbrooks.progresstracker.trackers.domain.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

import static androidx.room.ForeignKey.CASCADE;

@SuppressWarnings({"unused"})
@Entity(tableName = "tracker_tags",
        foreignKeys = {@ForeignKey(
                entity = Tracker.class,
                parentColumns = "tracker_id",
                childColumns = "tracker_id",
                onDelete = CASCADE),
                @ForeignKey(
                        entity = Tag.class,
                        parentColumns = "tag_id",
                        childColumns = "tag_id",
                        onDelete = CASCADE)},
        primaryKeys = {"tracker_id", "tag_id"})
public class TrackerTagging {

    @NonNull
    @ColumnInfo(name = "tracker_id")
    private String mTrackId;

    @NonNull
    @ColumnInfo(name = "tag_id")
    private String mTagId;

    /**
     * Full constructor used by room
     *
     */
    public TrackerTagging(@NonNull String trackerId, @NonNull String tagId){

        this.mTrackId = trackerId;
        this.mTagId = tagId;

    }

    @NonNull
    public String getTrackerId(){
        return mTrackId;
    }

    @NonNull
    public String getTagId(){
        return mTagId;
    }


}
