package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import androidx.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveTracker extends
        UseCase<SaveTracker.RequestValues,
                SaveTracker.ResponseValue,
                SaveTracker.ErrorCodes> {

    public enum ErrorCodes{NONE}

    private final TrackerRepository mTrackerRepository;


    public SaveTracker(@NonNull TrackerRepository trackersRepository){
        mTrackerRepository = checkNotNull(trackersRepository,"trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTrackerRepository.saveItem(requestValues.getTracker());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Tracker mTracker;

        public RequestValues(@NonNull Tracker trackerToSave){
            mTracker = trackerToSave;
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
