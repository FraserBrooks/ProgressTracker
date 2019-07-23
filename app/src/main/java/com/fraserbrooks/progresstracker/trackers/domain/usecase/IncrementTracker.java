package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;

import androidx.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class IncrementTracker extends
        UseCase<IncrementTracker.RequestValues,
                IncrementTracker.ResponseValue,
                IncrementTracker.ErrorCodes> {

    public enum ErrorCodes{NONE}
    private final TrackerRepository mTrackerRepository;


    public IncrementTracker(@NonNull TrackerRepository trackersRepository){
        mTrackerRepository = checkNotNull(trackersRepository,"trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTrackerRepository.incrementTracker(requestValues.mTrackerId,
                requestValues.mIncrementAmount);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final int mIncrementAmount;

        private final String mTrackerId;

        public RequestValues(int incrementAmount, @NonNull String trackerId) {

            mIncrementAmount = incrementAmount;
            mTrackerId = trackerId;

        }

        public int getAmount() {
            return mIncrementAmount;
        }

        public String getTrackerId() {
            return mTrackerId;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {
            // Nothing to do, no value to return
        }
    }



}
