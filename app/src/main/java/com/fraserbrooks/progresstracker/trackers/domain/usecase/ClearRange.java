package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;
import androidx.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class ClearRange extends
        UseCase<ClearRange.RequestValues,
                ClearRange.ResponseValue,
                ClearRange.ErrorCodes> {

    public enum RangeToClear {WEEK, MONTH, YEAR}
    public enum ErrorCodes {NONE}

    private final TrackerRepository mTrackerRepository;

    public ClearRange(@NonNull TrackerRepository trackersRepository) {
        mTrackerRepository = checkNotNull(trackersRepository, "trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        switch (requestValues.getRangeToClear()) {

            case WEEK:
                mTrackerRepository.clearWeek(requestValues.getTrackerId());
                break;
            case MONTH:
                mTrackerRepository.clearMonth(requestValues.getTrackerId());
                break;

        }

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final RangeToClear mRangeToClear;
        private final String mTrackerId;

        public RequestValues(@NonNull RangeToClear rangeToClear, @NonNull String trackerId) {
            mRangeToClear = rangeToClear;
            mTrackerId = trackerId;
        }

        RangeToClear getRangeToClear() {
            return mRangeToClear;
        }

        String getTrackerId() {
            return mTrackerId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        public ResponseValue() {
            // Nothing to do, no value to return
        }
    }


}
