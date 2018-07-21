package com.fraserbrooks.progresstracker.addTargetActivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddTargetPresenter implements AddTargetContract.Presenter{

    private final String TAG = "AddTargetPresenter";

    private final Repository mRepository;
    private final AppExecutors mAppExecutors;

    private Map<String, String> mTrackerIds;

    private final AddTargetContract.View mAddTargetView;

    AddTargetPresenter(@NonNull Repository repository,
                       @NonNull AddTargetContract.View addTargetView,
                       @NonNull AppExecutors appExecutors){
        mRepository = repository;
        mAppExecutors = appExecutors;

        mAddTargetView = addTargetView;
        mAddTargetView.setPresenter(this);
    }

    @Override
    public void addTarget() {

        boolean isRollingTarget = mAddTargetView.newTargetIsRollingTarget();

        String hoursS = mAddTargetView.getHoursInput();
        String minutesS = mAddTargetView.getMinutesInput();

        if(hoursS.isEmpty() && minutesS.isEmpty()){
            mAddTargetView.longToast("You must enter a time!");
            return;
        }

        int hours = 0;
        int minutes = 0;

        try{
            if(!hoursS.isEmpty()){
                hours = Integer.parseInt(hoursS);
            }
            if(!minutesS.isEmpty()){
                minutes = Integer.parseInt(minutesS);
            }
        } catch (NumberFormatException e){
            mAddTargetView.longToast("Invalid Input");
        }

        String trackerName = mAddTargetView.getTrackerName();

        if(mTrackerIds == null){
            mAddTargetView.longToast("Could not find Tracker");
        }

        String trackerId = mTrackerIds.get(trackerName);
        String period = mAddTargetView.getPeriodInput();

        Target newTarget = new Target(trackerId, (hours*60) + minutes, period);

        if(mRepository.saveTarget(newTarget)){
            mAddTargetView.longToast("Target saved");
            mAddTargetView.backToTargetsScreen();
        }else{
            mAddTargetView.longToast("Error saving Target");
        }
    }


    @Override
    public void start() {

        mAddTargetView.showLoading();


        mTrackerIds = new LinkedHashMap<>();

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRepository.getTrackers(new DataSource.GetTrackersCallback() {
                    @Override
                    public void onTrackersLoaded(List<Tracker> trackers) {
                        final ArrayList<String> names = new ArrayList<>();
                        for(Tracker t : trackers){
                            if(!t.isArchived()){
                                names.add(t.getTitle());
                            }
                            mTrackerIds.put(t.getTitle(), t.getId());
                        }
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAddTargetView.setSpinner(names);
                                mAddTargetView.hideLoading();
                            }
                        });
                    }

                    @Override
                    public void onTrackerLoaded(Tracker tracker) {
                        // Shouldn't be called
                        Log.e(TAG, "onTrackerLoaded: called");
                    }

                    @Override
                    public void onDataNotAvailable() {
                        mAddTargetView.hideLoading();
                        mAddTargetView.showNoTrackers();
                    }
                }, false);
            }
        });

    }
}
