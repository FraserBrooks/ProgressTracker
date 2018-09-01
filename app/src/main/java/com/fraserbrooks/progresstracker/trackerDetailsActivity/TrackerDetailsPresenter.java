package com.fraserbrooks.progresstracker.trackerDetailsActivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.Repository;

public class TrackerDetailsPresenter implements TrackerDetailsContract.Presenter {

    private TrackerDetailsContract.View mTrackerDetailsView;
    private Repository mRepository;

    public TrackerDetailsPresenter(@NonNull Repository repo, @NonNull TrackerDetailsActivity trackerDetailsActivity) {
        mTrackerDetailsView = trackerDetailsActivity;
        mRepository = repo;
    }


    @Override
    public void getTracker(String id) {

    }

    @Override
    public void changeTrackerTitle(Tracker tracker, String newTitle) {

    }

    @Override
    public void changeTrackerMaxScore(Tracker tracker, int newMax) {

    }

    @Override
    public void changeTrackerLabel() {

    }

    @Override
    public void incrementTrackerScore() {

    }

    @Override
    public void decrementTrackerScore() {

    }

    @Override
    public void archiveTracker() {

    }

    @Override
    public void timerButtonClicked() {

    }

    @Override
    public void addToTrackerScore(int amount) {

    }

    @Override
    public void subButtonClicked() {

    }

    @Override
    public void addButtonClicked() {

    }

    @Override
    public void newTrackerName(String s) {

    }

    @Override
    public void newTrackerLabel(String s) {

    }

    @Override
    public void updateDifficultyButtonClicked() {

    }

    @Override
    public void newMaxCountSelected(String selected) {

    }

    @Override
    public void start() {

    }
}
