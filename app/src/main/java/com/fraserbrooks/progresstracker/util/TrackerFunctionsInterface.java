package com.fraserbrooks.progresstracker.util;

import com.fraserbrooks.progresstracker.data.Tracker;

public interface TrackerFunctionsInterface {

    void addToTrackerScore(Tracker tracker, int amount);

    void timerButtonClicked(Tracker tracker);

    void moreDetailsButtonClicked(Tracker tracker);

    void updateTracker(Tracker tracker);

    void archiveTracker(Tracker tracker);

    void deleteTracker(Tracker tracker);

}
