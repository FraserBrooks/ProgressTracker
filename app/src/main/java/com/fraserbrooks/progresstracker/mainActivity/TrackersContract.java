package com.fraserbrooks.progresstracker.mainActivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter
 *
 * Created by Fraser on 08/04/2018.
 */


public interface TrackersContract {

    interface View extends BaseView<Presenter>{

        void showTrackers(List<Tracker> trackers);

        void updateOrAddTracker(Tracker tracker);

        void rememberExpanded(Tracker trackerAboutToRefresh);

        void removeTracker(Tracker tracker);

        void populateGraph(List<Tracker> trackers);

        void updateInGraph(Tracker t);

        void showLoading();

        void hideLoading();

        void showNoTrackers();

        void showTrackerDetailsScreen(String trackerId);

        void showAddTrackerScreen();

        boolean isActive();

    }

    interface Presenter extends BasePresenter, TrackerFunctionsInterface {

        // todo: remove
        void addTestData();

        void loadTrackers(boolean forceUpdate);

        void addTrackerButtonClicked();

        void graphClicked();

        void setTrackerExpandCollapse(Tracker tracker);

    }

}
