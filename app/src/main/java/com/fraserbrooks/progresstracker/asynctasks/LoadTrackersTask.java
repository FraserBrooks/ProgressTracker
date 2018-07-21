package com.fraserbrooks.progresstracker.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

public class LoadTrackersTask extends AsyncTask<Tracker, Tracker, Void>{

    private final String TAG = "LoadTrackersTask";

    private DataSource.GetTrackerCallback mCallback;


    public LoadTrackersTask(DataSource.GetTrackerCallback callback){
        Log.d(TAG, "LoadTrackersTask: created");
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Tracker... trackers) {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e(TAG, "doInBackground: interrupted");
        }

        for(Tracker t : trackers){
            
            publishProgress(t);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, "doInBackground: interrupted");
            }

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Tracker... trackers){
        Log.d(TAG, "doInBackground: publishing tracker");
        for(Tracker t : trackers) mCallback.onTrackerLoaded(t);
    }

    @Override
    protected void onPostExecute(Void v){
        // This callback may be called twice, once for the cache and once for loading
        // the data from the server API, so we check before decrementing, otherwise
        // it throws "Counter has been corrupted!" exception.
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }
    }




}
