package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarPresenter implements CalendarContract.Presenter{

    private final String TAG = "CalendarPresenter";

    private final Repository mRepository;

    private final CalendarContract.View mCalendarView;


    public CalendarPresenter(@NonNull Repository r,
                             @NonNull CalendarContract.View v){
        mRepository = r;
        mCalendarView = v;

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

        mRepository.getDayTargets(new DataSource.GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {

                mCalendarView.setTargetSpinners(targets);
                initCalendar();
            }

            @Override
            public void onDataNotAvailable() {
                mCalendarView.showNoDataAvailable();
            }
        });
    }

    @Override
    public void initCalendar() {

        Target target1 = mCalendarView.getFirstSelectedTarget();
        Target target2 = mCalendarView.getSecondSelectedTarget();
        Target target3 = mCalendarView.getThirdSelectedTarget();


        String id1 = null;
        String id2 = null;
        String id3 = null;

        if(target1 != null) id1 = target1.getId();
        if(target2 != null) id2 = target2.getId();
        if(target3 != null) id3 = target3.getId();

        mRepository.getDaysTargetWasMet(id1, id2, id3, new DataSource.GetDaysTargetsMetCallback() {
            @Override
            public void onDaysLoaded(DataSource.CalendarTriple calendars) {
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

                mCalendarView.hideLoading();

            }

            @Override
            public void onDataNotAvailable() {
                mCalendarView.showNoDataAvailable();
            }
        });


    }

}
