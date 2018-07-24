package com.fraserbrooks.progresstracker.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

public class LoadTargetsTask extends AsyncTask<Target, Target, Void>{

    private final String TAG = "LoadTargetsTask";

    private DataSource.GetTargetsCallback mCallback;


    public LoadTargetsTask(DataSource.GetTargetsCallback callback){
        Log.d(TAG, "LoadTrackersTask: created");
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Target... targets) {

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Log.e(TAG, "doInBackground: interrupted");
        }

        for(Target t : targets){
            
            publishProgress(t);
            
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Log.e(TAG, "doInBackground: interrupted");
            }

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Target... targets){
        Log.d(TAG, "onProgressUpdate: publishing target");
        for(Target t : targets) mCallback.onTargetLoaded(t);
    }

    @Override
    protected void onPostExecute(Void v){
        // This callback may be called twice, once for the cache and once for loading
        // the data from the server API, so we check before decrementing, otherwise
        // it throws "Counter has been corrupted!" exception.
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }
        mCallback.onTargetsLoaded(null);
    }




}
