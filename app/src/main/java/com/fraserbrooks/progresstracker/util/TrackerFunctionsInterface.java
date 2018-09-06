package com.fraserbrooks.progresstracker.util;

import com.fraserbrooks.progresstracker.data.Tracker;

public interface TrackerFunctionsInterface {

    void addToTrackerScore(Tracker tracker, int amount);

    void timerButtonClicked(Tracker tracker);

    void moreDetailsButtonClicked(Tracker tracker);

    void changeTrackerOrder(Tracker tracker, int from,int to);

    void archiveTracker(Tracker tracker);

    void deleteTracker(Tracker tracker);

}
