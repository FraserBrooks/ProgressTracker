package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Fraser on 02/01/2018.
 */

class DataWrapper{

    private final String TAG = "Main>dataWrapper";
    private String trackerFileName = "tracker_list.srl";
    private String targetOrderFileName = "target_ordering.txt";

    DataWrapper(){
    }

    ArrayList<Tracker> readTrackers(Context context){
        ArrayList<Tracker> val = new ArrayList<>();
        FileInputStream fis;
        try{
            fis = context.openFileInput(trackerFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            val = (ArrayList<Tracker>) ois.readObject();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return val;
    }

    void writeTrackers(Context context, ArrayList<Tracker> ls ){
        ObjectOutput out;
        if(!ls.isEmpty()){
            if(ls.get(0) == null){
                ls.remove(0);
            }
        }

        collapseAll(ls);
        try{
            out = new ObjectOutputStream(context.openFileOutput(trackerFileName, Context.MODE_PRIVATE));
            out.writeObject(ls);
            out.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private void collapseAll(ArrayList<Tracker> ds) {
        for (Tracker tracker : ds) {
            tracker.collapse();
        }
    }


    void writeTargets(Context context, ArrayList<Target> items) {
        // todo: write target_order
        ArrayList<Integer> intIDs = new ArrayList<>();
        for (Target t: items) {
            intIDs.add(getID(t.getStartDate()));
        }

        ObjectOutput out;
        try{
            out = new ObjectOutputStream(context.openFileOutput(targetOrderFileName,
                    Context.MODE_PRIVATE));
            out.writeObject(intIDs);
            out.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getID(Calendar c){

        return  (c.get(Calendar.DAY_OF_YEAR) + 1)
                * (c.get(Calendar.SECOND) + 1)
                * (c.get(Calendar.MILLISECOND) + 1)
                / (c.get(Calendar.WEEK_OF_YEAR) + 1);
    }

    ArrayList<Target> readTargets(Context context) {

        ArrayList<Tracker> trackers = readTrackers(context);
        ArrayList<Target>  targets = new ArrayList<>();

        for (Tracker t: trackers) {
            targets.addAll(t.getTargets());
        }

        ArrayList<Integer> intIDs = new ArrayList<>();
        FileInputStream fis;
        try{
            fis = context.openFileInput(targetOrderFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            intIDs = (ArrayList<Integer>) ois.readObject();
            ois.close();
            Log.d(TAG, "readTargets: read IDs successfully");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "readTargets: ");

        ArrayList<Target> orderedTargets = new ArrayList<>();
        for (int id: intIDs) {
            for (Target t: targets) {
                if (getID(t.getStartDate()) == id){
                    Log.d(TAG, "readTargets: MATCH_FOUND: " + id);
                    orderedTargets.add(t);
                    targets.remove(t);
                    break;
                }else{
                    Log.d(TAG, "readTargets: \nNO_MATCH\n" + id
                    +" \nDOES NOT MATCH:\n" + getID(t.getStartDate()));
                }
            }
        }
        orderedTargets.addAll(targets);
        return orderedTargets;
    }
}
