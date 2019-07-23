package com.fraserbrooks.progresstracker.trackers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.datasource.source.utils.Status;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.UseCaseHandler;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.ClearRange;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.DeleteTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.GetTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.IncrementTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.StartStopTrackerTimer;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.UpdateTracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;

public class TrackerDetailsPresenter implements TrackerDetailsContract.Presenter {

    private final String TAG = "TrackerDetailsPresenter";
    private TrackerDetailsContract.View mTrackerDetailsView;

    // Use Cases
    private final UseCaseHandler mUseCaseHandler;
    private final GetTracker mGetTracker;
    private final IncrementTracker mIncrementTracker;
    private final StartStopTrackerTimer mStartStopTrackerTimer;
    private final ClearRange mClearRange;
    private final UpdateTracker mUpdateTracker;
    private final DeleteTracker mDeleteTracker;

    private LiveData<Resource<Tracker>> mTrackerSource;
    private MediatorLiveData<Tracker> mTracker;

    // todo: convert to MVVM so we don't need to run anything on main thread
    private AppExecutors mAppExecutors;

    TrackerDetailsPresenter(@NonNull AppExecutors appExecutors,
                                   @NonNull TrackerDetailsContract.View detailsView,
                                   @NonNull UseCaseHandler useCaseHandler,
                                   @NonNull GetTracker getTracker,
                                   @NonNull IncrementTracker incrementTracker,
                                   @NonNull StartStopTrackerTimer startStopTrackerTimer,
                                   @NonNull ClearRange clearRange,
                                   @NonNull UpdateTracker updateTracker,
                                   @NonNull DeleteTracker deleteTracker){
        mAppExecutors = appExecutors;
        mTrackerDetailsView = detailsView;

        mUseCaseHandler = useCaseHandler;
        mGetTracker = getTracker;
        mIncrementTracker = incrementTracker;
        mStartStopTrackerTimer = startStopTrackerTimer;
        mClearRange = clearRange;
        mUpdateTracker = updateTracker;
        mDeleteTracker = deleteTracker;

        mTracker = new MediatorLiveData<>();
    }

    @Override
    public void start() {
        Log.d(TAG, "start: called");

        GetTracker.RequestValues requestValues =
                new GetTracker.RequestValues(mTrackerDetailsView.getTrackerId());

        mUseCaseHandler.execute(mGetTracker, requestValues,
                new UseCase.UseCaseCallback<GetTracker.ResponseValue, GetTracker.ErrorCodes>() {

                    @Override
                    public void onSuccess(GetTracker.ResponseValue response) {
                        updateTrackerLiveData(response.getTracker());
                    }

                    @Override
                    public void onError(GetTracker.ErrorCodes errorCode) {
                        mTrackerDetailsView.showTrackerLoadError();
                    }
                });

    }

    private void updateTrackerLiveData(LiveData<Resource<Tracker>> fromDataLayer){
        if(mTrackerSource != null) mTracker.removeSource(mTrackerSource);
        mTrackerSource = fromDataLayer;
        mTracker.addSource(mTrackerSource, resource -> {

            if(resource.status == Status.LOADING){
                mAppExecutors.mainThread().execute(mTrackerDetailsView::showLoading);
            }else{
                mAppExecutors.mainThread().execute(mTrackerDetailsView::hideLoading);
            }
            if(resource.data != null) mTracker.postValue(resource.data);
        });
    }


    @Override
    public LiveData<Tracker> getTracker() {
        return mTracker;
    }

    @Override
    public void archiveTracker(Tracker tracker) {
        tracker.setArchived(true);
        updateTracker(tracker);

    }

    @Override
    public void deleteTracker(Tracker tracker) {

        DeleteTracker.RequestValues requestValue =
                new DeleteTracker.RequestValues(tracker);

        mUseCaseHandler.execute(mDeleteTracker, requestValue,
                new UseCase.UseCaseCallback<DeleteTracker.ResponseValue, DeleteTracker.ErrorCodes>() {

                    @Override
                    public void onSuccess(DeleteTracker.ResponseValue response) {
                        mTrackerDetailsView.returnToTrackersScreen();
                    }

                    @Override
                    public void onError(DeleteTracker.ErrorCodes errorCode) {

                    }
                });
    }

    @Override
    public void clearWeek(String trackerId) {
        ClearRange.RequestValues requestValues =
                new ClearRange.RequestValues(ClearRange.RangeToClear.WEEK, trackerId);

        mUseCaseHandler.execute(mClearRange, requestValues, null);
    }

    @Override
    public void clearMonth(String trackerId) {
        ClearRange.RequestValues requestValues =
                new ClearRange.RequestValues(ClearRange.RangeToClear.MONTH, trackerId);

        mUseCaseHandler.execute(mClearRange, requestValues, null);
    }

    @Override
    public void newTrackerName(Tracker tracker, String newName) {
        tracker.setTitle(newName);
        updateTracker(tracker);
    }

    @Override
    public void newTrackerLabel(Tracker tracker, String newLabel) {
        tracker.setCounterLabel(newLabel);
        updateTracker(tracker);
    }

    @Override
    public void newTrackerProgressionRate(Tracker tracker, int newMax) {

    }




    @Override
    public void incrementTrackerScore(Tracker tracker, int amount) {

        IncrementTracker.RequestValues requestValues =
                new IncrementTracker.RequestValues(amount,tracker.getId());

        mUseCaseHandler.execute(mIncrementTracker, requestValues, null);

    }

    @Override
    public void timerButtonClicked(Tracker tracker) {
        StartStopTrackerTimer.RequestValues requestValues = (tracker.isCurrentlyTiming())
                ? new StartStopTrackerTimer.RequestValues(StartStopTrackerTimer.StartStop.STOP_TIMING,
                tracker)
                : new StartStopTrackerTimer.RequestValues(StartStopTrackerTimer.StartStop.START_TIMING,
                tracker);

        mUseCaseHandler.execute(mStartStopTrackerTimer, requestValues, null);

    }

    @Override
    public void moreDetailsButtonClicked(Tracker tracker) {
        Log.e(TAG, "moreDetailsButtonClicked: already in TrackerDetails View");
        // Not used
    }

    @Override
    public void updateTracker(Tracker tracker) {
        UpdateTracker.RequestValues requestValues =
                new UpdateTracker.RequestValues(tracker);

        mUseCaseHandler.execute(mUpdateTracker, requestValues, null);
    }

}
