package com.fraserbrooks.progresstracker.trackers;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;

public interface AddTrackerContract {

    interface View extends BaseView<Presenter>{

        int getProgressionRate();

        String getNewTrackerName();

        void longToast(String toast);

        void backToTrackersScreen();


    }


    interface Presenter extends BasePresenter {

        void addTracker();



    }


}
