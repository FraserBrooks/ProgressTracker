package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.ScoreEntry;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link TrackersFragment}), retrieves the data and updates the
 * UI as required.
 *
 * Created by Fraser on 09/04/2018.
 */
public class TrackersPresenter implements TrackersContract.Presenter {

    private final String TAG = "TrackersPresenter";

    private final Repository mTrackersRepository;
    private final AppExecutors mAppExecutors;
    private final TrackersContract.View mTrackersView;

    private boolean mFirstLoad = true;

    public TrackersPresenter(@NonNull Repository trackersRepository,
                             @NonNull TrackersContract.View trackersView,
                             @NonNull AppExecutors appExecutors){
        mTrackersRepository = checkNotNull(trackersRepository);
        mAppExecutors = appExecutors;
        mTrackersView = checkNotNull(trackersView);

        // Add listeners
        mTrackersRepository.addUpdateOrAddTrackerListener(new DataSource.UpdateOrAddTrackerListener() {
            @Override
            public boolean isActive() {
                return mTrackersView.isActive();
            }

            @Override
            public void trackerUpdated(Tracker tracker) {
                mTrackersView.updateOrAddTracker(tracker);
                mTrackersView.updateInGraph(tracker);
            }
        });
        mTrackersRepository.addDeleteTrackerListener(new DataSource.DeleteTrackerListener() {
            @Override
            public boolean isActive() {
                return mTrackersView.isActive();
            }

            @Override
            public void trackerDeleted(Tracker trackerToDelete) {
                mTrackersView.removeTracker(trackerToDelete);
            }
        });

        mTrackersView.setPresenter(this);
    }

    @Override
    public void start() {
        Log.d(TAG, "start: called");
        loadTrackers(false);
    }

    @Override
    public void addTestData() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Tracker> trackers = new ArrayList<>();

                Tracker tracker1 = new Tracker("Guitar Practice", "hours",5000*60,
                        true, true, false);
                Tracker tracker2 = new Tracker("Java", "hours", 10000*60,
                        true, false, false);
                Tracker tracker3 = new Tracker("Haskell", "hours",  100 * 60,
                        true, false, false);
                Tracker tracker4 = new Tracker("Exercise", "hours", 10000*60,
                        true, false, false);
                Tracker tracker5 = new Tracker("Reading", "books read", 1000,
                        false, true, false);
                Tracker tracker6 = new Tracker("Jiu-Jitsu", "sessions", 500,
                        false, true, false);
                Tracker tracker7 = new Tracker("Maths Revision", "lectures", 20,
                        false, true, false);

                Target target1 = new Target(tracker1.getId(), 2*60, Target.EVERY_DAY);
                Target target2 = new Target(tracker2.getId(), 200*60, Target.EVERY_YEAR);
                Target target3 = new Target(tracker3.getId(), 60, Target.EVERY_DAY);
                Target target4 = new Target(tracker4.getId(), 60, Target.EVERY_DAY);
                Target target5 = new Target(tracker5.getId(), 1, Target.EVERY_WEEK);
                Target target6 = new Target(tracker1.getId(), 1000*60, Target.EVERY_YEAR);
                Target target7 = new Target(tracker6.getId(), 2, Target.EVERY_WEEK);
                Target target8 = new Target(tracker7.getId(), 1, Target.EVERY_DAY);

                Calendar startDate = Calendar.getInstance();
                startDate.add(Calendar.YEAR, -3);

                target1.setStartDate(startDate);
                target2.setStartDate(startDate);
                target3.setStartDate(startDate);
                target4.setStartDate(startDate);
                target5.setStartDate(startDate);
                target6.setStartDate(startDate);
                target7.setStartDate(startDate);
                target8.setStartDate(startDate);



                trackers.add(tracker1);
                trackers.add(tracker2);
                trackers.add(tracker3);
                trackers.add(tracker4);

                Random rand = new Random();

                ArrayList<ScoreEntry> entries = new ArrayList<>();

                for(Tracker t : trackers){
                    for(int i = 0; i < 450; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, -i);
                        entries.add(new ScoreEntry(cal, cal, cal, cal, t.getId(), rand.nextInt(181)));
                    }
                }

                trackers.clear();
                trackers.add(tracker5);
                trackers.add(tracker6);
                trackers.add(tracker7);

                for(Tracker t : trackers){
                    for(int i = 0; i < 450; i++){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, -i);
                        int k = rand.nextInt(5) - 3;
                        k = (k <= 0) ? 0 : 1;
                        entries.add(new ScoreEntry(cal, cal, cal, cal, t.getId(), k));
                    }
                }

                trackers.add(tracker1);
                trackers.add(tracker2);
                trackers.add(tracker3);
                trackers.add(tracker4);

                mTrackersRepository.saveTrackers(trackers);

                ArrayList<Target> targets = new ArrayList<>();

                targets.add(target1);
                targets.add(target2);
                targets.add(target3);
                targets.add(target4);
                targets.add(target5);
                targets.add(target6);
                targets.add(target7);
                targets.add(target8);

                mTrackersRepository.saveTargets(targets);

                mTrackersRepository.saveEntries(entries);


                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadTrackers(true);
                    }
                });

            }
        });

    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link Repository}
     */
    @Override
    public void loadTrackers(boolean forceUpdate) {

        // A network reload will be forced on first load.
        if(forceUpdate || mFirstLoad){
            mTrackersRepository.refreshAllCache();
        }
        mFirstLoad = false;

        mTrackersView.showLoading();

        // app is busy until the response is handled.
        setWorking();

        mTrackersRepository.getTrackers(new DataSource.GetTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {

                // The view may not be able to handle UI updates anymore
                if (!mTrackersView.isActive()) {
                    Log.d(TAG, "onTrackersLoaded: trackersView not active. exiting from callback");
                    return;
                }

                for (Tracker tracker : trackers) {
                    mTrackersView.updateOrAddTracker(tracker);
                    mTrackersView.updateInGraph(tracker);
                }

                mTrackersView.hideLoading();
                setIdle();
            }

            @Override
            public void onDataNotAvailable() {

                // The view may not be able to handle UI updates anymore
                if (!mTrackersView.isActive()) {
                    Log.d(TAG, "onDataNotAvailable: trackersView not active. exiting from callback");
                    return;
                }

                mTrackersView.hideLoading();
                mTrackersView.showNoTrackers();
                setIdle();
            }
        });

    }

    private void setWorking(){
        EspressoIdlingResource.increment(); // App is busy until further notice
    }

    private void setIdle(){
        // This callback may be called twice, once for the cache and once for loading
        // the data from the server API, so we check before decrementing, otherwise
        // it throws "Counter has been corrupted!" exception.
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
        }
    }

    @Override
    public void addTrackerButtonClicked() {
        mTrackersView.showAddTrackerScreen();
    }

    @Override
    public void graphClicked() {

    }

    @Override
    public void setTrackerExpandCollapse(Tracker tracker) {
        // Don't need to send this change to the repository as
        // expand/collapse is only relevant in the ui
        tracker.setExpanded(!tracker.isExpanded());
        mTrackersView.rememberExpanded(tracker);
        mTrackersView.updateOrAddTracker(tracker);
    }



    @Override
    public void addToTrackerScore(Tracker tracker, final int increment) {

        // Remember that this tracker is expanded/unexpanded
        mTrackersView.rememberExpanded(tracker);

        // Send change to repository which will notify ui of change through listeners
        mTrackersRepository.incrementTracker(tracker.getId(), increment);
    }

    @Override
    public void timerButtonClicked(Tracker tracker) {
        // todo
    }

    @Override
    public void moreDetailsButtonClicked(Tracker tracker) {
        mTrackersView.showTrackerDetailsScreen(tracker.getId());
    }

    @Override
    public void updateTracker(Tracker tracker) {
        mTrackersRepository.updateTracker(tracker);
    }


    @Override
    public void archiveTracker(Tracker tracker) {

    }

    @Override
    public void deleteTracker(Tracker tracker) {
        mTrackersRepository.deleteTracker(tracker.getId());
    }

}
