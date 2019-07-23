package com.fraserbrooks.progresstracker.trackers.domain.filter;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.util.ArrayList;
import java.util.List;

public class FilterAllTrackersFilter implements TrackerFilter {

    @Override
    public List<Tracker> filter(List<Tracker> trackers) {
        return new ArrayList<>(trackers);
    }
}
