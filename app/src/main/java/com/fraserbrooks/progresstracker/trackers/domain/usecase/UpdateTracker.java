package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import androidx.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class UpdateTracker extends
        UseCase<UpdateTracker.RequestValues,
                UpdateTracker.ResponseValue,
                UpdateTracker.ErrorCodes> {

    public enum ErrorCodes { NONE }

    private final TrackerRepository mTrackerRepository;


    public UpdateTracker(@NonNull TrackerRepository trackersRepository){
        mTrackerRepository = checkNotNull(trackersRepository,"trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTrackerRepository.updateItem(requestValues.getTracker());
        getUseCaseCallback().onSuccess(new ResponseValue());

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Tracker mTracker;

        public RequestValues(@NonNull Tracker trackerToUpdate){
            mTracker = trackerToUpdate;
        }

        public Tracker getTracker() {
            return mTracker;
        }
    }


    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {
            // Nothing to do, no value to return
        }
    }

}
