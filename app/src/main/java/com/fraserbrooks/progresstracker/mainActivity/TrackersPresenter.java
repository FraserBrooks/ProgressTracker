package com.fraserbrooks.progresstracker.mainActivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.asyncTasks.LoadTrackersTask;
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
                        true, true, false);
                Tracker tracker3 = new Tracker("Haskell", "hours",  100 * 60,
                        true, true, false);
                Tracker tracker4 = new Tracker("Exercise", "hours", 10000*60,
                        true, true, false);
                Tracker tracker5 = new Tracker("Reading", "books read", 1000,
                        false, true, false);
                Tracker tracker6 = new Tracker("Jiu-Jitsu", "sessions", 500,
                        false, true, false);
                Tracker tracker7 = new Tracker("Maths Revision", "lectures", 20,
                        false, true, false);

                Target target1 = new Target(tracker1.getId(), 2*60, "DAY");
                Target target2 = new Target(tracker2.getId(), 200*60, "YEAR");
                Target target3 = new Target(tracker3.getId(), 60, "DAY");
                Target target4 = new Target(tracker4.getId(), 60, "DAY");
                Target target5 = new Target(tracker5.getId(), 1, "WEEK");
                Target target6 = new Target(tracker1.getId(), 1000*60, "YEAR");
                Target target7 = new Target(tracker6.getId(), 2, "WEEK");
                Target target8 = new Target(tracker7.getId(), 1, "DAY");

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

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mTrackersRepository.getTrackers(new DataSource.GetTrackersCallback() {
                    @Override
                    public void onTrackersLoaded(final List<Tracker> trackers) {
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {

                                // The view may not be able to handle UI updates anymore
                                if (!mTrackersView.isActive()) {
                                    Log.d(TAG, "onTrackersLoaded: mTackersView not active. exiting from callback");
                                    return;
                                }

                                mTrackersView.hideLoading();
                                if(trackers.isEmpty()){
                                    Log.d(TAG, "onTrackersLoaded: trackers.isEmpty");
                                    mTrackersView.showNoTrackers();
                                }else{
                                    // Create AsyncTask to spread out the redrawing of trackers
                                    // otherwise the whole listView will be redrawn at once
                                    Log.d(TAG, "onTrackersLoaded: creating new LoadTrackersTask");
                                    new LoadTrackersTask(new DataSource.GetTrackersCallback() {
                                        @Override
                                        public void onTrackersLoaded(List<Tracker> trackers) {
                                            // Should not be called
                                            Log.e(TAG, "onTrackersLoaded: called" );
                                        }

                                        @Override
                                        public void onTrackerLoaded(Tracker tracker) {
                                            // The view may not be able to handle UI updates anymore
                                            if (!mTrackersView.isActive()) {
                                                Log.d(TAG, "onTrackersLoaded: mTrackersView not active. exiting from callback");
                                                return;
                                            }
                                            mTrackersView.updateOrAddTracker(tracker);
                                            mTrackersView.updateInGraph(tracker);
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.e(TAG, "onDataNotAvailable from async task");
                                        }
                                    }).execute(trackers.toArray(new Tracker[trackers.size()]));
                                }
                            }
                        });


                    }

                    @Override
                    public void onTrackerLoaded(final Tracker tracker) {
                        Log.e(TAG, "onTrackerLoaded: called");
                        // should never be called when staggeredLoad = false
                    }

                    @Override
                    public void onDataNotAvailable() {
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mTrackersView.showNoDataAvailable();
                            }
                        });
                    }
                }, false);
            }
        });
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
        tracker.setExpanded(!tracker.isExpanded());
        mTrackersView.updateOrAddTracker(tracker);
    }



    @Override
    public void addToTrackerScore(Tracker tracker, final int increment) {
        mTrackersRepository.incrementScore(tracker.getId(), increment);
        mTrackersView.updateOrAddTracker(tracker);
        mTrackersView.updateInGraph(tracker);
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

    }
}
