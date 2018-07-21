package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.asynctasks.LoadDayTargetsForCalendarTask;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarPresenter implements CalendarContract.Presenter{

    private final String TAG = "CalendarPresenter";

    private final Repository mRepository;
    private final AppExecutors mAppExecutors;

    private final CalendarContract.View mCalendarView;


    public CalendarPresenter(@NonNull Repository repository,
                             @NonNull CalendarContract.View view,
                             @NonNull AppExecutors appExecutors){
        mRepository = repository;
        mAppExecutors = appExecutors;

        mCalendarView = view;

        mCalendarView.setPresenter(this);
    }


    @Override
    public void start() {
        loadTargetNamesAndSetSpinners();
    }

    public void loadTargetNamesAndSetSpinners() {
        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice
        mCalendarView.showLoading();

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRepository.getTargets(new DataSource.GetTargetsCallback() {
                    @Override
                    public void onTargetsLoaded(final List<Target> targets) {

                        new LoadDayTargetsForCalendarTask(new DataSource.GetTargetsCallback() {
                            @Override
                            public void onTargetsLoaded(List<Target> targets) {
                                mCalendarView.setTargetSpinners();
                                initCalendar();
                            }

                            @Override
                            public void onTargetLoaded(Target target) {
                                mCalendarView.addToTargetSpinners(target);
                            }

                            @Override
                            public void onDataNotAvailable() {
                                Log.e(TAG, "onDataNotAvailable: data not available");
                            }
                        }).execute(targets.toArray(new Target[targets.size()]));
                    }

                    @Override
                    public void onTargetLoaded(Target target) {
                        if(target.isRollingTarget() && target.getInterval().equals("DAY")){
                            mCalendarView.addToTargetSpinners(target);
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        mCalendarView.showNoDataAvailable();
                    }
                }, false);
            }
        });
    }

    @Override
    public void initCalendar() {

        final Target target1 = mCalendarView.getFirstSelectedTarget();
        final Target target2 = mCalendarView.getSecondSelectedTarget();
        final Target target3 = mCalendarView.getThirdSelectedTarget();

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String id1 = null;
                String id2 = null;
                String id3 = null;

                if(target1 != null) id1 = target1.getId();
                if(target2 != null) id2 = target2.getId();
                if(target3 != null) id3 = target3.getId();

                mRepository.getDaysTargetWasMet(id1, id2, id3, new DataSource.GetDaysTargetsMetCallback() {
                    @Override
                    public void onDaysLoaded(final DataSource.CalendarTriple calendars) {
                        // This callback may be called twice, once for the cache and once for loading
                        // the data from the server API, so we check before decrementing, otherwise
                        // it throws "Counter has been corrupted!" exception.
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                            EspressoIdlingResource.decrement(); // Set app as idle.
                        }

                        mCalendarView.updateCalendar(
                                calendars.list1,
                                calendars.list2,
                                calendars.list3);

                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mCalendarView.hideLoading();
                            }
                        });

                    }

                    @Override
                    public void onDataNotAvailable() {
                        mCalendarView.showNoDataAvailable();
                    }
                });
            }
        });



    }

}
