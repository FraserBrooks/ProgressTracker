package com.fraserbrooks.progresstracker.targetscalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.targets.domain.model.Target;
import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public interface CalendarContract {


    interface View extends BaseView<Presenter> {

        void showLoading();

        void hideLoading();

        void updateOrAddTarget(Target target);

        void removeTargetOption(Target target);

        String tryToSetTargetSpinner(@NonNull UserSetting.Setting targetSpinnerNum,
                                     @Nullable String target1IdFromSettings);

        void showNoDataAvailable();

        Calendar getCalendarViewMonth();
        void showCalendarLoading();
        void hideCalendarLoading();

        void setCalendarTargetDays(@NonNull UserSetting.Setting targetSpinnerNum,
                                   Set<Date> days);

        void showNoTargetDays(@NonNull UserSetting.Setting targetSpinnerNum);

        void calendarNotifyDataSetChange();
        void updateDateInCalendar(Date date);

        void disableSpinnerSelectionListeners();
        void enableSpinnerSelectionListeners();

        boolean isActive();

    }


    interface Presenter extends BasePresenter{

        void newTargetSelected(UserSetting.Setting targetChanged, String newTargetId);

        void calendarPositionChanged();

    }


}
