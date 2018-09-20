package com.fraserbrooks.progresstracker.util;

import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.Calendar;

public interface TrackerFunctionsInterface {

    void addToTrackerScore(Tracker tracker, int amount);

    void timerButtonClicked(Tracker tracker);

    void moreDetailsButtonClicked(Tracker tracker);

    void updateTracker(Tracker tracker);

    void archiveTracker(Tracker tracker);

    void deleteTracker(Tracker tracker);

//    int getCountOnDay(Tracker tracker, Calendar day);
}
