package com.fraserbrooks.progresstracker.mainActivity;

import com.applandeo.materialcalendarview.EventDay;
import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Target;

import java.util.Calendar;

public interface CalendarContract {


    interface View extends BaseView<Presenter> {

        Target getFirstSelectedTarget();

        Target getSecondSelectedTarget();

        Target getThirdSelectedTarget();

        void deleteTarget(Target target);

//        void updateCalendar(List<Calendar> firstTargetDays,
//                            List<Calendar> secondTargetDays,
//                            List<Calendar> thirdTargetDays);

        void setTargetSpinners();

        void updateOrAddTarget(Target target);

        void showLoading();

        void hideLoading();

        void showNoDataAvailable();

        boolean isActive();

        Calendar getCalendarViewMonth();

        void addDayIcon(EventDay day);

        void clearDayIcons();
        void refreshCalendarView();

        int getTarget1ResourceId();
        int getTarget2ResourceId();
        int getTarget3ResourceId();

    }


    interface Presenter extends BasePresenter{

        void loadCalendar();

        void loadTargetNamesAndSetSpinners();

    }


}
