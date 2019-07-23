package com.fraserbrooks.progresstracker.util;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

public interface TrackerFunctionsInterface {

    void incrementTrackerScore(Tracker tracker, int amount);

    void timerButtonClicked(Tracker tracker);

    void moreDetailsButtonClicked(Tracker tracker);

    void updateTracker(Tracker tracker);

    void archiveTracker(Tracker tracker);

    void deleteTracker(Tracker tracker);

    void clearWeek(String trackerId);

    void clearMonth(String trackerId);

}
