package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Fraser on 04/04/2018.
 */

public class ReadAndUpdateDataTask extends AsyncTask<Void, Void, ArrayList> {


    private boolean readTrackers;
    private DataWrapper dataWrapper;
    private ArrayAdapter adapter;
    private Context context;

    public ReadAndUpdateDataTask(boolean readingTargets, ArrayAdapter aa, Context cntxt){

        readTrackers = !readingTargets;
        adapter = aa;
        context = cntxt;

        dataWrapper = new DataWrapper();
    }

    @Override
    protected ArrayList doInBackground(Void... voids) {

        try {
            Thread.sleep(250);
        } catch (InterruptedException ignored) {
        }

        if(readTrackers){
            return dataWrapper.readTrackers(context);
        }else{
            return dataWrapper.readTargets(context);
        }
    }

    @Override
    protected void onPostExecute(ArrayList result){
        adapter.clear();

        // for tracker view Graph
        if(readTrackers) adapter.add(null);

        adapter.addAll(result);
    }

}
