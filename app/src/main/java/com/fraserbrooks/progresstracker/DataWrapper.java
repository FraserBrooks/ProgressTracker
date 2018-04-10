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

public class DataWrapper{

    private final String TAG = "Main>dataWrapper";
    private String trackerFileName = "tracker_list.srl";
    private String targetOrderFileName = "target_ordering.txt";

    public DataWrapper(){
    }

    public ArrayList<Tracker> readTrackers(Context context){
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

    public void writeTrackers(Context context, ArrayList<Tracker> trackers ){
        final ArrayList<Tracker> ls = trackers;
        final Context context_ = context;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectOutput out;
                if(!ls.isEmpty()){
                    if(ls.get(0) == null){
                        ls.remove(0);
                    }
                }

                collapseAll(ls);
                try{
                    out = new ObjectOutputStream(context_.openFileOutput(trackerFileName, Context.MODE_PRIVATE));
                    out.writeObject(ls);
                    out.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void collapseAll(ArrayList<Tracker> ds) {
        for (Tracker tracker : ds) {
            tracker.collapse();
        }
    }


    public void writeTargetOrdering(Context context, ArrayList<Target> targets) {
        // write target_order

        final ArrayList<Target> targets_ = targets;
        final Context context_ = context;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> intIDs = new ArrayList<>();
                for (Target t: targets_) {
                    intIDs.add(getID(t.getStartDate()));
                }

                ObjectOutput out;
                try{
                    out = new ObjectOutputStream(context_.openFileOutput(targetOrderFileName,
                            Context.MODE_PRIVATE));
                    out.writeObject(intIDs);
                    out.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getID(Calendar c){

        return  (c.get(Calendar.DAY_OF_YEAR))
                * (c.get(Calendar.SECOND))
                * (c.get(Calendar.MILLISECOND))
                / (c.get(Calendar.WEEK_OF_YEAR) + 1);
    }

    public ArrayList<Target> readTargets(Context context) {

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
