package com.fraserbrooks.progresstracker.targets;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;

import java.util.List;

public interface AddTargetContract {

    interface View extends BaseView<Presenter> {

        boolean newTargetIsRollingTarget();

        String getSingleNumberInput();

        String getHoursInput();

        String getMinutesInput();

        int getIntervalInput();

        String getTrackerName();

        void showNoTrackers();

        void showLoading();

        void hideLoading();

        void setSpinner(List<String> trackerNames);

        void longToast(String toast);

        void backToTargetsScreen();

    }


    interface Presenter extends BasePresenter {

        void addTarget();


    }


}
