package com.fraserbrooks.progresstracker.trackers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.UseCaseHandler;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.SaveTracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;

public class AddTrackerPresenter implements AddTrackerContract.Presenter {

    private final String TAG = "AddTrackerPresenter";

    private AddTrackerContract.View mAddTrackerView;

    // Use Cases
    private final UseCaseHandler mUseCaseHandler;
    private final SaveTracker   mSaveTracker;


    AddTrackerPresenter(@NonNull AppExecutors appExecutors,
                        @NonNull AddTrackerContract.View addTrackerView,
                        @NonNull UseCaseHandler useCaseHandler,
                        @NonNull SaveTracker saveTracker) {
        mUseCaseHandler = useCaseHandler;
        mSaveTracker = saveTracker;
    }

    @Override
    public void start() {
        Log.d(TAG, "start: called");
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

        //todo
        String iconText = "AA";
        int color = 10;

        Tracker newTracker = Tracker.TimeLevelUpTracker(trackerName,trackerProgressionRate,
                Tracker.ICON_LEVEL, iconText, 0);





    }


    private void saveTracker(Tracker tracker){

        SaveTracker.RequestValues requestValues = new SaveTracker.RequestValues(tracker);

        mUseCaseHandler.execute(mSaveTracker, requestValues,
                new UseCase.UseCaseCallback<SaveTracker.ResponseValue, SaveTracker.ErrorCodes>() {
                    @Override
                    public void onSuccess(SaveTracker.ResponseValue response) {
                        mAddTrackerView.backToTrackersScreen();
                    }

                    @Override
                    public void onError(SaveTracker.ErrorCodes errorCode) {

                    }
                });

    }
}
