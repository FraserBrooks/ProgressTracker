package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.datasource.source.utils.Resource;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetTracker extends
        UseCase<GetTracker.RequestValues,
                GetTracker.ResponseValue,
                GetTracker.ErrorCodes>{

    public enum ErrorCodes{NONE}

    private final TrackerRepository mTrackerRepository;


    public GetTracker(@NonNull TrackerRepository trackerRepository){
        mTrackerRepository = checkNotNull(trackerRepository,
                "trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        LiveData<Resource<Tracker>> repoData = mTrackerRepository.getItem(requestValues.getTrackerId());
        getUseCaseCallback().onSuccess(new ResponseValue(repoData));

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mTrackerId;

        public RequestValues(@NonNull String id){
            mTrackerId = id;
        }

        public String getTrackerId() {
            return mTrackerId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final LiveData<Resource<Tracker>> mTracker;

        public ResponseValue(@NonNull LiveData<Resource<Tracker>> tracker){
            mTracker = tracker;
        }

        public LiveData<Resource<Tracker>> getTracker() {
            return mTracker;
        }
    }

}
