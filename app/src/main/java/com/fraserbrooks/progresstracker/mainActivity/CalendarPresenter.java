package com.fraserbrooks.progresstracker.mainActivity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.UserSetting;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CalendarPresenter implements CalendarContract.Presenter{

    private final String TAG = "CalendarPresenter";

    private final Repository mRepository;

    private final CalendarContract.View mCalendarView;

    private String mCachedTargetId1 = "";
    private String mCachedTargetId2 = "";
    private String mCachedTargetId3 = "";


    public CalendarPresenter(@NonNull Repository repository,
                             @NonNull CalendarContract.View view){
        mRepository = repository;
        mCalendarView = view;

        mCalendarView.setPresenter(this);
        mRepository.addDeleteTargetListener(new Repository.DeleteTargetListener() {
            @Override
            public boolean isActive() {
                return mCalendarView.isActive();
            }

            @Override
            public void targetDeleted(Target targetToDelete) {
                mCalendarView.removeTargetOption(targetToDelete);
            }
        });
        mRepository.addUpdateOrAddTargetListener(new DataSource.UpdateOrAddTargetListener() {
            @Override
            public boolean isActive() {
                return mCalendarView.isActive();
            }

            @Override
            public void targetUpdated(Target targetToUpdate) {
                mCalendarView.updateOrAddTarget(targetToUpdate);
            }
        });

    }


    @Override
    public void start() {
        loadDataAndSetSpinners();
    }


    private void loadDataAndSetSpinners() {
        Log.d(TAG, "loadDataAndSetSpinners: called");

        // Make sure Espresso knows that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice
        mCalendarView.showLoading();

        mRepository.getTargets(new DataSource.GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {

                int dayTargets = 0;

                for(Target target : targets){
                    if(target.getInterval() == Target.EVERY_DAY){
                        dayTargets += 1;
                        mCalendarView.updateOrAddTarget(target);
                    }
                }

                if(dayTargets == 0){
                    mCalendarView.showNoDataAvailable();
                    return;
                }

                mCalendarView.showCalendarLoading();
                mCalendarView.disableSpinnerSelectionListeners();

                // load all calendar targets for first load
                getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_1, () ->
                        getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_2, () ->
                                getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_3, () -> {

                                    mCalendarView.calendarNotifyDataSetChange();

                                    mCalendarView.enableSpinnerSelectionListeners();
                                    mCalendarView.hideCalendarLoading();
                                    mCalendarView.hideLoading();

                                    // This callback may be called twice, once for the cache and once for loading
                                    // the data from the server API, so we check before decrementing, otherwise
                                    // it throws "Counter has been corrupted!" exception.
                                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                                        EspressoIdlingResource.decrement(); // Set app as idle.
                                    }
                                })));
            }

            @Override
            public void onDataNotAvailable() {
                mCalendarView.showNoDataAvailable();
            }
        });

    }

    @Override
    public void newTargetSelected(@NonNull UserSetting.Setting targetNumber, @NonNull String newTargetId){

        // Make sure Espresso knows that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mRepository.getSettingValue(targetNumber, new DataSource.GetSettingCallback() {
            @Override
            public void onSettingLoaded(@Nullable String oldTargetId) {
                if(newTargetId.equals(oldTargetId)){
                    done();
                    return;
                }

                mCalendarView.disableSpinnerSelectionListeners();
                mCalendarView.showCalendarLoading();
                mCalendarView.showLoading();

                mRepository.setSetting(targetNumber, newTargetId);

                getAndInitTargetDays(targetNumber, () -> {
                    mCalendarView.calendarNotifyDataSetChange();

                    mCalendarView.enableSpinnerSelectionListeners();
                    mCalendarView.hideCalendarLoading();
                    mCalendarView.hideLoading();

                    done();
                });
            }

            private void done(){
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }
            }

        });



    }

    private void getAndInitTargetDays(@NonNull UserSetting.Setting targetToGet,@NonNull Runnable finishedCallback){

        mRepository.getSettingValue(targetToGet, targetId -> {

            String targetIdSetInView = mCalendarView.tryToSetTargetSpinner(targetToGet, targetId);

            // No day targets exist therefore no target set in view
            // so no need to update calendar
            if(targetIdSetInView == null){
                finishedCallback.run();
                return;
            }

            // Persist the set target for the calendar view
            if(!targetIdSetInView.equals(targetId)) {
                mRepository.setSetting(targetToGet, targetIdSetInView);
            }

            Calendar calendar = mCalendarView.getCalendarViewMonth();

            mRepository.getDaysTargetMet(targetIdSetInView, calendar, new DataSource.GetDaysTargetMetCallback() {
                @Override
                public void onDaysLoaded(Set<Date> successfulDays) {
                    Log.d(TAG, "onDaysLoaded: settingDays for " + targetToGet);
                    mCalendarView.setCalendarTargetDays(targetToGet, successfulDays);
                    finishedCallback.run();
                }

                @Override
                public void onDataNotAvailable() {
                    mCalendarView.showNoTargetDays(targetToGet);
                    finishedCallback.run();
                }
            });

        });

    }

    @Override
    public void calendarPositionChanged(){


        // Make sure Espresso knows that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mCalendarView.showCalendarLoading();

        // load all calendar target days for new calendar range
        getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_1, () ->
                getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_2, () ->
                        getAndInitTargetDays(UserSetting.Setting.CALENDAR_TARGET_3, () -> {

                            mCalendarView.hideCalendarLoading();

                            // no longer idle
                            if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                                EspressoIdlingResource.decrement(); // Set app as idle.
                            }

                        })));




    }

}
