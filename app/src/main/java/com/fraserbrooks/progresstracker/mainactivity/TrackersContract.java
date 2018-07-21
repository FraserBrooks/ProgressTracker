package com.fraserbrooks.progresstracker.mainactivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter
 *
 * Created by Fraser on 08/04/2018.
 */


public interface TrackersContract {

    interface View extends BaseView<Presenter>{

        void refreshListAdapter();

        void showTrackers(List<Tracker> trackers);

        void updateOrAddTracker(Tracker tracker);

        void populateGraph(List<Tracker> trackers);

        void updateInGraph(Tracker t);

        void showLoading();

        void hideLoading();

        void showNoTrackers();

        void showNoDataAvailable();

        void showTrackerDetailsScreen(String trackerId);

        void showAddTrackerScreen();

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void loadTrackers(boolean forceUpdate);

        void addTrackerButtonClicked();

        void graphClicked();

        void setTrackerExpandCollapse(Tracker tracker);

        String getTrackerQuantifier(Tracker tracker);

        String getTrackerQuantifierOne(Tracker tracker);

        String getTrackerQuantifierTwo(Tracker tracker);

        String getLevelIndicator(Tracker tracker);

        void changeTrackerTitle(Tracker tracker, String newTitle);

        void changeTrackerMaxScore(Tracker tracker, int newMax);

        void addToTrackerScore(Tracker tracker, int c);

        void timerButtonClicked(Tracker tracker);

        void moreDetailsButtonClicked(Tracker tracker);

        void changeTrackerOrder(int from,int to);

    }

}
