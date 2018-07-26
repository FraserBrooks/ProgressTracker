package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fraserbrooks.progresstracker.asynctasks.LoadDayIconsForCalendarTask;
import com.fraserbrooks.progresstracker.asynctasks.LoadDayTargetsForCalendarTask;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.Calendar;
import java.util.List;

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

        mRepository.addDeleteTargetListener(new Repository.DeleteTargetListener() {
            @Override
            public boolean isActive() {
                return mCalendarView.isActive();
            }

            @Override
            public void trackerDeleted(Target targetToDelete) {
                mCalendarView.deleteTarget(targetToDelete);
            }
        });
    }


    @Override
    public void start() {
        loadTargetNamesAndSetSpinners();
    }

    public void loadTargetNamesAndSetSpinners() {
        Log.d(TAG, "loadTargetNamesAndSetSpinners: called");

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

                        Log.d(TAG, "onTargetsLoaded: starting new LoadDayTargetsTask");
                        new LoadDayTargetsForCalendarTask(new DataSource.GetTargetsCallback() {
                            @Override
                            public void onTargetsLoaded(@Nullable List<Target> targets) {
                                // Called when task finished (targets = null)
                                Log.d(TAG, "onTargetsLoaded: setting target spinners");
                                mCalendarView.setTargetSpinners();
                                mCalendarView.hideLoading();
                                loadCalendar();
                            }

                            @Override
                            public void onTargetLoaded(Target target) {
                                mCalendarView.updateOrAddTarget(target);
                            }

                            @Override
                            public void onDataNotAvailable() {
                                Log.e(TAG, "onDataNotAvailable: data not available");
                            }
                        }).execute(targets.toArray(new Target[targets.size()]));
                    }

                    @Override
                    public void onTargetLoaded(Target target) {
                        // Staggered load = false
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
    public void loadCalendar() {
        Log.d(TAG, "loadCalendar: called");
        
        final Calendar month = mCalendarView.getCalendarViewMonth();

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

                mRepository.getDaysTargetsMet(id1, id2, id3, month, new DataSource.GetDaysTargetsMetCallback() {
                    @Override
                    public void onDaysLoaded(final DataSource.CalendarTriple calendars) {
                        // This callback may be called twice, once for the cache and once for loading
                        // the data from the server API, so we check before decrementing, otherwise
                        // it throws "Counter has been corrupted!" exception.
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                            EspressoIdlingResource.decrement(); // Set app as idle.
                        }

                        Log.d(TAG, "onDaysLoaded: creating new LoadDayIcons task");
                        new LoadDayIconsForCalendarTask(mCalendarView).execute(calendars);

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
