package com.fraserbrooks.progresstracker.trackers.domain.usecase;

import com.fraserbrooks.progresstracker.BuildConfig;
import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.UseCase;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.security.InvalidParameterException;

import androidx.annotation.NonNull;

import static com.fraserbrooks.progresstracker.trackers.domain.usecase.StartStopTrackerTimer.ErrorCodes.ALREADY_TIMING;
import static com.fraserbrooks.progresstracker.trackers.domain.usecase.StartStopTrackerTimer.ErrorCodes.NOT_TIMING;
import static com.google.common.base.Preconditions.checkNotNull;

public class StartStopTrackerTimer extends
        UseCase<StartStopTrackerTimer.RequestValues,
                StartStopTrackerTimer.ResponseValue,
                StartStopTrackerTimer.ErrorCodes>{

    public enum  ErrorCodes{ALREADY_TIMING, NOT_TIMING}
    public enum  StartStop {START_TIMING, STOP_TIMING}

    private final TrackerRepository mTrackerRepository;

    public StartStopTrackerTimer(@NonNull TrackerRepository trackersRepository){
        mTrackerRepository = checkNotNull(trackersRepository,"trackersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        Tracker t = requestValues.getTracker();

        if (requestValues.getStartOrStop().equals(StartStop.START_TIMING)) {

            if (!t.isCurrentlyTiming()) {
                t.startTimingAt(System.currentTimeMillis());
            } else {
                getUseCaseCallback().onError(ALREADY_TIMING);
                return;
            }

        } else if (requestValues.getStartOrStop().equals(StartStop.STOP_TIMING)) {

            if (t.isCurrentlyTiming()) {
                int minutes = t.getMinutesSinceTimerStart(System.currentTimeMillis());
                t.resetTimer();
                mTrackerRepository.incrementTracker(t.getId(), minutes);
            } else {
                getUseCaseCallback().onError(NOT_TIMING);
                return;
            }
        }
        mTrackerRepository.updateItem(t);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final StartStop mStartOrStop;

        private final Tracker mTracker;

        public RequestValues(@NonNull StartStop startOrStop,@NonNull Tracker tracker){
            if(BuildConfig.DEBUG && (!tracker.isTimeTracker())) throw new InvalidParameterException();
            mStartOrStop = startOrStop;
            mTracker = tracker;
        }

        StartStop getStartOrStop() {
            return mStartOrStop;
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
