package com.fraserbrooks.progresstracker.addTargetActivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddTargetPresenter implements AddTargetContract.Presenter{

    private final String TAG = "AddTargetPresenter";

    private final Repository mRepository;

    private Map<String, String> mTrackerIds;

    private final AddTargetContract.View mAddTargetView;

    AddTargetPresenter(@NonNull Repository repository,
                       @NonNull AddTargetContract.View addTargetView){
        mRepository = repository;

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
        int interval = mAddTargetView.getIntervalInput();

        Target newTarget = new Target(trackerId, (hours*60) + minutes, interval);

        if(mRepository.saveTarget(newTarget)){
            mAddTargetView.longToast("Target saved");
            mAddTargetView.backToTargetsScreen();
        }else{
            mAddTargetView.longToast("Error saving Target");
        }
    }


    @Override
    public void start() {

        // App is busy until further notice
        mAddTargetView.showLoading();

        setWorking();


        mTrackerIds = new LinkedHashMap<>();

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
                mAddTargetView.setSpinner(names);

                mAddTargetView.hideLoading();
                setIdle();
            }

            @Override
            public void onDataNotAvailable() {
                mAddTargetView.hideLoading();
                mAddTargetView.showNoTrackers();
                setIdle();
            }
        });

    }

    private void setWorking(){
        EspressoIdlingResource.increment(); // App is busy until further notice
    }

    private void setIdle(){
        // This callback may be called twice, once for the cache and once for loading
        // the data from the server API, so we check before decrementing, otherwise
        // it throws "Counter has been corrupted!" exception.
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }
    }

}
