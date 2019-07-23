package com.fraserbrooks.progresstracker.trackers;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * This specifies the contract between the view and the presenter
 *
 * Created by Fraser on 08/04/2018.
 */


public interface TrackersContract {

    interface View extends BaseView<Presenter>{

        void showLoading();

        void hideLoading();

        void showNoTrackers();

        void showTrackerDetailsScreen(String trackerId);

        void showAddTrackerScreen();

        boolean isActive();

    }

    interface Presenter extends BasePresenter, TrackerFunctionsInterface {

        LiveData<List<Tracker>> getTrackersList();

        LiveData<List<Tracker>> getGraphTrackersList();

        void loadTrackers(boolean forceUpdate);

        void addTrackerButtonClicked();

        void graphClicked();

    }

}
