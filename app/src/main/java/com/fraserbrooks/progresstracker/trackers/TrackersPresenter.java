package com.fraserbrooks.progresstracker.trackers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import android.os.Looper;
import android.util.Log;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.datasource.source.utils.Status;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.UseCaseHandler;
import com.fraserbrooks.progresstracker.trackers.domain.filter.TrackersFilterType;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.ClearRange;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.GetTrackers;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.IncrementTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.StartStopTrackerTimer;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link TrackersFragment}), retrieves the data and updates the
 * UI as required.
 *
 * Created by Fraser on 09/04/2018.
 */
public class TrackersPresenter implements TrackersContract.Presenter {

    private final String TAG = "TrackersPresenter";

    private final TrackersContract.View mTrackersView;

    // Use cases
    private final UseCaseHandler mUseCaseHandler;
    private final GetTrackers  mGetTrackers;
    private final GetTrackers  mGetGraphTrackers;
    private final IncrementTracker mIncrementTracker;
    private final StartStopTrackerTimer mStartStopTimerTracker;
    private final ClearRange mClearRange;


    private boolean mFirstLoad = true;


    // LiveData observed by the UI
    private MediatorLiveData<List<Tracker>> trackers;
    private MediatorLiveData<List<Tracker>> graphTrackers;

    // LiveData from the Data Layer that becomes the source for the above
    private LiveData<Resource<List<Tracker>>> trackersSource;
    private LiveData<Resource<List<Tracker>>> graphTrackersSource;


    private TrackersFilterType mCurrentListFiltering = TrackersFilterType.ALL_TRACKERS;
    private TrackersFilterType mCurrentGraphFiltering = TrackersFilterType.ALL_TRACKERS;

    // todo: convert to MVVM so we don't need to run anything on main thread
    private AppExecutors mAppExecutors;

    public TrackersPresenter( @NonNull AppExecutors appExecutors,
                              @NonNull TrackersContract.View trackersView,
                              @NonNull UseCaseHandler useCaseHandler,
                              @NonNull GetTrackers getTrackers,
                              @NonNull GetTrackers getGraphTrackers,
                              @NonNull IncrementTracker incrementTracker,
                              @NonNull StartStopTrackerTimer startStopTrackerTimer,
                              @NonNull ClearRange clearRange
                             ) {
        mAppExecutors = checkNotNull(appExecutors);
        mTrackersView = checkNotNull(trackersView);

        mUseCaseHandler = checkNotNull(useCaseHandler);
        mGetTrackers = checkNotNull(getTrackers);
        mGetGraphTrackers = checkNotNull(getGraphTrackers);
        mIncrementTracker = checkNotNull(incrementTracker);
        mStartStopTimerTracker = checkNotNull(startStopTrackerTimer);
        mClearRange = checkNotNull(clearRange);

        // Live data being listened to by the UI
        trackers = new MediatorLiveData<>();
        graphTrackers = new MediatorLiveData<>();

        mTrackersView.setPresenter(this);
    }

    @Override
    public void start() {
        Log.d(TAG, "start: called");
        loadTrackers(false);
    }

    @Override
    public LiveData<List<Tracker>> getTrackersList() {
        return trackers;
    }

    @Override
    public LiveData<List<Tracker>> getGraphTrackersList() {
        return graphTrackers;
    }

    private void updateTrackerLiveData(LiveData<Resource<List<Tracker>>> fromDataLayer){
        Log.d(TAG, "updateTrackerLiveData: b44 graphTrackers hO = " + trackers.hasObservers());
        if(trackersSource != null) trackers.removeSource(trackersSource);
        trackersSource = fromDataLayer;
        setLiveData(trackersSource, trackers);
    }

    private void updateGraphTrackersLiveData(LiveData<Resource<List<Tracker>>> fromDataLayer){
        if(graphTrackersSource != null) graphTrackers.removeSource(graphTrackersSource);
        graphTrackersSource = fromDataLayer;
        setLiveData(graphTrackersSource, graphTrackers);
    }

    /**
     *
     * @param fromDataLayer   LiveData source fetched by the Domain layer from the Data Layer
     * @param toUi            LiveData that the UI is observing that needs to connect to new data
     */
    private void setLiveData(LiveData<Resource<List<Tracker>>> fromDataLayer,
                             MediatorLiveData<List<Tracker>> toUi){
        Log.d(TAG, "asdff setLiveData: b4 toUI has observer = " + toUi.hasObservers());

        toUi.addSource(fromDataLayer,resource -> {
            if(Looper.getMainLooper() == Looper.myLooper()) Log.d(TAG, "asdff setLiveData: runningOnMainThread");
            else Log.d(TAG, "asdff setLiveData: runningOnMainThread");
            if(resource.status == Status.LOADING){
                mAppExecutors.mainThread().execute(mTrackersView::showLoading);
            }else {
                mAppExecutors.mainThread().execute(mTrackersView::hideLoading);
            }
            if(resource.data != null) trackers.postValue(resource.data);
        });
        Log.d(TAG, "asdff setLiveData: toUi has observer = " + toUi.hasObservers());
        Log.d(TAG, "asdff setLiveData: fromData has observer = " + fromDataLayer.hasObservers());
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TrackerRepository}
     */
    @Override
    public void loadTrackers(boolean forceUpdate) {

        GetTrackers.RequestValues requestValue = new GetTrackers.RequestValues(forceUpdate,
                mCurrentListFiltering);

        mUseCaseHandler.execute(mGetTrackers, requestValue,
                new UseCase.UseCaseCallback<GetTrackers.ResponseValue, GetTrackers.ErrorCodes>() {
                    @Override
                    public void onSuccess(GetTrackers.ResponseValue response) {
                        Log.d(TAG, "865 onSuccess: updating trackers live data");
                        updateTrackerLiveData(response.getLiveTrackers());
                    }

                    @Override
                    public void onError(GetTrackers.ErrorCodes errorCode) {
                        mTrackersView.showNoTrackers();
                    }


                });

        GetTrackers.RequestValues graphRequestValue = new GetTrackers.RequestValues(forceUpdate,
                mCurrentGraphFiltering);

        mUseCaseHandler.execute(mGetGraphTrackers, graphRequestValue,
                new UseCase.UseCaseCallback<GetTrackers.ResponseValue, GetTrackers.ErrorCodes>()  {
                    @Override
                    public void onSuccess(GetTrackers.ResponseValue response) {
                        Log.d(TAG, "865 onSuccess: updating graph trackers live data");
                        updateGraphTrackersLiveData(response.getLiveTrackers());
                    }

                    @Override
                    public void onError(GetTrackers.ErrorCodes errorCode) {
                        mTrackersView.showNoTrackers();
                    }

                });

    }

    @Override
    public void addTrackerButtonClicked() {
        mTrackersView.showAddTrackerScreen();
    }

    @Override
    public void graphClicked() {

    }



    @Override
    public void incrementTrackerScore(Tracker tracker, final int increment) {
        // Send change to repository which will notify ui of change through live data
        IncrementTracker.RequestValues requestValues = new IncrementTracker.RequestValues(increment,
                tracker.getId());
        Log.d(TAG, "asdff incrementTrackerScore: toUi has observer: = " + trackers.hasObservers());
        Log.d(TAG, "asdff incrementTrackerScore: fromData has observer: = " + trackersSource.hasObservers());
        Log.d(TAG, "asdff incrementTrackerScore: executing use case");
        mUseCaseHandler.execute(mIncrementTracker, requestValues,
                new UseCase.UseCaseCallback<IncrementTracker.ResponseValue, IncrementTracker.ErrorCodes>() {
                    @Override
                    public void onSuccess(IncrementTracker.ResponseValue response) {
                        Log.d(TAG, "asdff onSuccess: tracker incremented, nothing to do");
                    }

                    @Override
                    public void onError(IncrementTracker.ErrorCodes errorCode) {
                        Log.e(TAG, "asdff onError: tracker could not be incremented" );
                    }
                });

    }

    @Override
    public void timerButtonClicked(Tracker tracker) {
        // Send change to repository which will notify ui of change through live data
        StartStopTrackerTimer.StartStop ss = (tracker.isCurrentlyTiming())
                ? StartStopTrackerTimer.StartStop.STOP_TIMING
                : StartStopTrackerTimer.StartStop.START_TIMING;
        StartStopTrackerTimer.RequestValues requestValues = new StartStopTrackerTimer.RequestValues(
                ss, tracker
        );

        mUseCaseHandler.execute(mStartStopTimerTracker, requestValues,
                new UseCase.UseCaseCallback<StartStopTrackerTimer.ResponseValue, StartStopTrackerTimer.ErrorCodes>() {
                    @Override
                    public void onSuccess(StartStopTrackerTimer.ResponseValue response) {
                        Log.d(TAG, "onSuccess: tracker timer updated, nothing to do");
                    }

                    @Override
                    public void onError(StartStopTrackerTimer.ErrorCodes errorCode) {
                        Log.e(TAG, "onError: tracker timer could not be updated" );
                    }
                });

    }

    @Override
    public void moreDetailsButtonClicked(Tracker tracker) {
        mTrackersView.showTrackerDetailsScreen(tracker.getId());
    }

    @Override
    public void updateTracker(Tracker tracker) {
        // todo
    }


    @Override
    public void archiveTracker(Tracker tracker) {

    }

    @Override
    public void deleteTracker(Tracker tracker) {
        // todo
    }

    @Override
    public void clearWeek(String trackerId) {
        // todo
    }

    @Override
    public void clearMonth(String trackerId) {
        // todo
    }

}
