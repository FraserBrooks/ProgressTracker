package com.fraserbrooks.progresstracker.trackers;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;

import androidx.lifecycle.LiveData;

public interface TrackerDetailsContract {

    interface View extends BaseView<Presenter> {

        String getTrackerId();

        void showLoading();

        void hideLoading();

        void returnToTrackersScreen();

        void showNoNumberError();

        void showBlankNameError();

        void showInvalidDifficultyError();

        void showTrackerLoadError();

    }

    interface Presenter extends BasePresenter, TrackerFunctionsInterface {

        LiveData<Tracker> getTracker();

        void newTrackerName(Tracker tracker, String newName);

        void newTrackerLabel(Tracker tracker, String newLabel);

        void newTrackerProgressionRate(Tracker tracker, int newMax);

    }

}
