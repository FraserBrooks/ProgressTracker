package com.fraserbrooks.progresstracker.trackers.domain.filter;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.util.List;

public interface TrackerFilter {

    List<Tracker> filter(List<Tracker> trackers);

}
