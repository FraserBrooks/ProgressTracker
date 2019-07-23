package com.fraserbrooks.progresstracker.trackers.domain.filter;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.util.ArrayList;
import java.util.List;

public class FilterArchivedTrackersFilter implements TrackerFilter {
    @Override
    public List<Tracker> filter(List<Tracker> trackers) {
        List<Tracker> filteredTrackers = new ArrayList<>();

        for(Tracker tracker : trackers){
            if(tracker.isArchived()){
                filteredTrackers.add(tracker);
            }
        }
        return filteredTrackers;
    }
}
