package com.fraserbrooks.progresstracker.trackerDetailsActivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;

import java.util.List;

public class TrackerDetailsPresenter implements TrackerDetailsContract.Presenter {

    private TrackerDetailsContract.View mTrackerDetailsView;
    private Repository mRepository;

    public TrackerDetailsPresenter(@NonNull Repository repo, @NonNull TrackerDetailsActivity trackerDetailsActivity) {
        mTrackerDetailsView = trackerDetailsActivity;
        mRepository = repo;
        mTrackerDetailsView.setPresenter(this);
    }


    @Override
    public void getTracker(String id) {

        mRepository.getTracker(id, new DataSource.GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {
                // Not called
            }

            @Override
            public void onTrackerLoaded(Tracker tracker) {
                mTrackerDetailsView.setTracker(tracker);
                mTrackerDetailsView.trackerChanged();
            }

            @Override
            public void onDataNotAvailable() {
                mTrackerDetailsView.showTrackerLoadError();
            }
        });

    }

    @Override
    public void archiveTracker(Tracker tracker) {

    }

    @Override
    public void deleteTracker(Tracker tracker) {

    }

    @Override
    public void newTrackerName(Tracker tracker, String newName) {
        tracker.setTitle(newName);
        mRepository.saveTracker(tracker);
        mTrackerDetailsView.setTracker(tracker);
        mTrackerDetailsView.trackerChanged();
    }

    @Override
    public void newTrackerLabel(Tracker tracker, String newLabel) {
        tracker.setCounterLabel(newLabel);
        mRepository.saveTracker(tracker);
        mTrackerDetailsView.setTracker(tracker);
        mTrackerDetailsView.trackerChanged();
    }

    @Override
    public void newTrackerMaxScore(Tracker tracker, int newMax) {

    }

    @Override
    public void start() {

    }


    @Override
    public void addToTrackerScore(Tracker tracker, int amount) {
        mRepository.incrementScore(tracker.getId(), amount);
        mTrackerDetailsView.trackerChanged();
    }

    @Override
    public void timerButtonClicked(Tracker tracker) {

    }

    @Override
    public void moreDetailsButtonClicked(Tracker tracker) {

    }

    @Override
    public void updateTracker(Tracker tracker) {

    }

}
