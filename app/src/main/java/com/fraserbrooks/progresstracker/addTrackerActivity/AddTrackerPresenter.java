package com.fraserbrooks.progresstracker.addTrackerActivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.Repository;

public class AddTrackerPresenter implements AddTrackerContract.Presenter {

    private final String TAG = "AddTrackerPresenter";

    private final Repository mTrackersRepository;

    private final AddTrackerContract.View mAddTrackerView;


    public AddTrackerPresenter(@NonNull Repository trackersRepository,
                               @NonNull AddTrackerContract.View addTrackersView){
        mTrackersRepository = trackersRepository;
        mAddTrackerView = addTrackersView;

        mAddTrackerView.setPresenter(this);
    }

    @Override
    public void start() {
        // nothing to do
    }


    @Override
    public void addTracker() {

        String trackerName = mAddTrackerView.getNewTrackerName();

        if(trackerName.equals("")){
            mAddTrackerView.longToast("You must enter a name.");
            return;
        }

        int trackerProgressionRate = mAddTrackerView.getProgressionRate();

        if(trackerProgressionRate == 0){
            mAddTrackerView.longToast("You must select a number.");
            return;
        }

        Tracker newTracker = new Tracker(trackerName, "hours", trackerProgressionRate, true, true, false);

        if(mTrackersRepository.saveTracker(newTracker)){
            mAddTrackerView.longToast("New Tracker added.");
            mAddTrackerView.backToTrackersScreen();
        }else{
            mAddTrackerView.longToast("Tracker could not be saved.");
        }
    }
}
