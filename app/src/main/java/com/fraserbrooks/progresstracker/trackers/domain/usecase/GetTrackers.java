package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.datasource.source.utils.Status;
import com.fraserbrooks.progresstracker.trackers.domain.filter.TrackerFilter;
import com.fraserbrooks.progresstracker.trackers.domain.filter.TrackerFilterFactory;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.trackers.domain.filter.TrackersFilterType;


import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetTrackers extends
        UseCase<GetTrackers.RequestValues,
                GetTrackers.ResponseValue,
                GetTrackers.ErrorCodes> {

    public static final String TAG = "GetTrackers";

    public enum ErrorCodes{NONE}

    private final TrackerRepository mTrackerRepository;
    private final TrackerFilterFactory mTrackerFilterFactory;


    public GetTrackers(@NonNull TrackerRepository trackerRepository,
                       @NonNull TrackerFilterFactory trackerFilterFactory) {
        mTrackerRepository = checkNotNull(trackerRepository,
                "trackersRepository cannot be null!");
        mTrackerFilterFactory = checkNotNull(trackerFilterFactory,
                "trackerFilterFactory cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

        Log.d(TAG, "asdff executeUseCase: called");
        
        LiveData<Resource<List<Tracker>>> repositoryData = mTrackerRepository.getData();

        MediatorLiveData<Resource<List<Tracker>>> processedData = new MediatorLiveData<>();
        processedData.addSource(repositoryData, resource -> {

            Log.d(TAG, "asdff executeUseCase: tracker processed data called");
            if (Looper.getMainLooper() == Looper.myLooper())
                Log.d(TAG, "asdff executeUseCase: runningOnMainThread");
            else Log.d(TAG, "asdff executeUseCase: runningOffMainThread");

            if (resource.status == Status.LOADING || resource.data == null) {
                processedData.postValue(resource);
                return;
            }

            List<Tracker> trackers = resource.data;

            // Filter the trackers read from the repository
            TrackersFilterType currentFiltering = values.getCurrentFiltering();
            TrackerFilter trackerFilter = mTrackerFilterFactory.create(currentFiltering);

            List<Tracker> filteredTrackers = trackerFilter.filter(trackers);
            processedData.postValue(Resource.success(filteredTrackers));
        });
        Log.d(TAG, "asdff executeUseCase: getting callback");
        getUseCaseCallback().onSuccess(new ResponseValue(processedData));
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final TrackersFilterType mCurrentFiltering;
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate, @NonNull TrackersFilterType currentFiltering) {

            mForceUpdate = forceUpdate;
            mCurrentFiltering = checkNotNull(currentFiltering,
                    "currentFiltering cannot be null!");

        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }

        TrackersFilterType getCurrentFiltering() {
            return mCurrentFiltering;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final LiveData<Resource<List<Tracker>>> mTrackers;

        public ResponseValue(@NonNull LiveData<Resource<List<Tracker>>> trackers) {
            mTrackers = checkNotNull(trackers, "trackers cannot be null!");
        }

        public LiveData<Resource<List<Tracker>>> getLiveTrackers() {
            return mTrackers;
        }

    }


}
