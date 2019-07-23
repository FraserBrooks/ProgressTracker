package com.fraserbrooks.progresstracker.trackers.domain.filter;

import java.util.HashMap;
import java.util.Map;

public class TrackerFilterFactory {

    private static final Map<TrackersFilterType, TrackerFilter> mFilters = new HashMap<>();

    public TrackerFilterFactory(){
        mFilters.put(TrackersFilterType.ALL_TRACKERS, new FilterAllTrackersFilter());
        mFilters.put(TrackersFilterType.ACTIVE_TRACKERS, new FilterActiveTrackersFilter());
        mFilters.put(TrackersFilterType.ARCHIVED_TRACKERS, new FilterArchivedTrackersFilter());
    }

    public TrackerFilter create(TrackersFilterType filterType){
        return mFilters.get(filterType);
    }

}
