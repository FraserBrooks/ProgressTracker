package com.fraserbrooks.progresstracker.mainactivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Target;

import java.util.Calendar;
import java.util.List;

public interface CalendarContract {


    interface View extends BaseView<Presenter> {

        Target getFirstSelectedTarget();

        Target getSecondSelectedTarget();

        Target getThirdSelectedTarget();

        void updateCalendar(List<Calendar> firstTargetDays,
                            List<Calendar> secondTargetDays,
                            List<Calendar> thirdTargetDays);

        void setTargetSpinners();

        void addToTargetSpinners(Target target);

        void showLoading();

        void hideLoading();

        void showNoDataAvailable();

    }


    interface Presenter extends BasePresenter{

        void initCalendar();

        void loadTargetNamesAndSetSpinners();

    }


}
