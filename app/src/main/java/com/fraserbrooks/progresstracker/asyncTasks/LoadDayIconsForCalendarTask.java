package com.fraserbrooks.progresstracker.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.mainActivity.CalendarContract;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.Calendar;

public class LoadDayIconsForCalendarTask extends AsyncTask<DataSource.CalendarTriple, EventDay, Void>{

    private final String TAG = "LoadDayIconsTask";

    private CalendarContract.View mCalendarView;

    private int resource1;
    private int resource2;
    private int resource3;

    public LoadDayIconsForCalendarTask(CalendarContract.View view){
        Log.d(TAG, "LoadDayIconsForCalendarTask: created");
        mCalendarView = view;
        resource1 = mCalendarView.getTarget1ResourceId();
        resource2 = mCalendarView.getTarget2ResourceId();
        resource3 = mCalendarView.getTarget3ResourceId();
    }

    @Override
    protected Void doInBackground(DataSource.CalendarTriple... calendarTriples) {

        for(DataSource.CalendarTriple calendars : calendarTriples){

            Log.d(TAG, "doInBackground: called. looping through days");
            
            if(calendars.list1 != null){
                for(Calendar c : calendars.list1){
                    publishProgress(new EventDay(c, resource1));
                }
            }

            if(calendars.list2 != null){
                for(Calendar c : calendars.list2){
                    publishProgress(new EventDay(c, resource2));
                }
            }

            if(calendars.list3 != null){
                for(Calendar c : calendars.list3){
                    publishProgress(new EventDay(c, resource3));
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(EventDay... dayIcons){
        //Log.d(TAG, "onProgressUpdate: publishing day icon(s)");
        for(EventDay d : dayIcons) mCalendarView.addDayIcon(d);
    }

    @Override
    protected void onPreExecute(){
        mCalendarView.showLoading();
        mCalendarView.clearDayIcons();
    }

    @Override
    protected void onPostExecute(Void v){
        // This callback may be called twice, once for the cache and once for loading
        // the data from the server API, so we check before decrementing, otherwise
        // it throws "Counter has been corrupted!" exception.
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }

        mCalendarView.refreshCalendarView();
        mCalendarView.hideLoading();

    }




}
