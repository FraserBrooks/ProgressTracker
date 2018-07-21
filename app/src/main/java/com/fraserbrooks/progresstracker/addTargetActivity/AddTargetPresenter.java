package com.fraserbrooks.progresstracker.addTargetActivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddTargetPresenter implements AddTargetContract.Presenter{

    private final String TAG = "AddTargetPresenter";

    private final Repository mRepository;

    private Map<String, String> mTrackerIds;

    private final AddTargetContract.View mAddTargetView;

    AddTargetPresenter(@NonNull Repository repo,
                       @NonNull AddTargetContract.View addTargetView){
        mRepository = repo;
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

        mRepository.getTrackers(true, new DataSource.GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {
                ArrayList<String> names = new ArrayList<>();
                for(Tracker t : trackers){
                    if(!t.isArchived()){
                        names.add(t.getTitle());
                    }
                    mTrackerIds.put(t.getTitle(), t.getId());
                }
                mAddTargetView.setSpinner(names);
                mAddTargetView.hideLoading();
            }

            @Override
            public void onDataNotAvailable() {
                mAddTargetView.hideLoading();
                mAddTargetView.showNoTrackers();
            }
        });
    }
}
