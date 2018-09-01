package com.fraserbrooks.progresstracker.trackerDetailsActivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.mainActivity.TrackersContract;

public interface TrackerDetailsContract {

    interface View extends BaseView<Presenter> {

        int getDecrementValue();

        int getIncrementValue();

        String getNewName();

        String getNewLabel();

        void returnToTrackersScreen();
        
        void setTracker(Tracker t);
        
    }

    interface Presenter extends BasePresenter {

        void getTracker(String id);

        void changeTrackerTitle(Tracker tracker, String newTitle);

        void changeTrackerMaxScore(Tracker tracker, int newMax);

        void changeTrackerLabel();

        void incrementTrackerScore();

        void decrementTrackerScore();

        void archiveTracker();


        void timerButtonClicked();

        void addToTrackerScore(int amount);

        void subButtonClicked();

        void addButtonClicked();

        void newTrackerName(String s);

        void newTrackerLabel(String s);

        void updateDifficultyButtonClicked();

        void newMaxCountSelected(String selected);
    }

}
