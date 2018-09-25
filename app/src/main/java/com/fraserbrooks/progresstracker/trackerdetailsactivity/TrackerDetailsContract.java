package com.fraserbrooks.progresstracker.trackerdetailsactivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;

public interface TrackerDetailsContract {

    interface View extends BaseView<Presenter> {

        void returnToTrackersScreen();
        
        void setTracker(Tracker t);

        void showNoNumberError();

        void showBlankNameError();

        void showInvalidDifficultyError();

        void showTrackerLoadError();

        void trackerChanged();


    }

    interface Presenter extends BasePresenter, TrackerFunctionsInterface {

        void getTracker(String id);

        void newTrackerName(Tracker tracker, String newName);

        void newTrackerLabel(Tracker tracker, String newLabel);

        void newTrackerMaxScore(Tracker tracker, int newMax);

    }

}
