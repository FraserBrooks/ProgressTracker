package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.asynctasks.LoadTrackersTask;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.List;

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
//                        mAppExecutors.mainThread().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                // The view may not be able to handle UI updates anymore
//                                if (!mTrackersView.isActive()) {
//                                    Log.d(TAG, "onTrackersLoaded: mTackersView not active. exiting from callback");
//                                    return;
//                                }
//                                mTrackersView.updateOrAddTracker(tracker);
//                                mTrackersView.updateInGraph(tracker);
//                            }
//                        });
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
    public String getTrackerQuantifier(Tracker tracker) {
        String quantifier;
        if(tracker.isTimeTracker()){
            quantifier = (tracker.getCountSoFar() > 59)
                    ? " - " + tracker.getCountSoFar()/60 + " hours"
                    : " - " + tracker.getCountSoFar() + " minutes";
        }else{
            quantifier = " - " + tracker.getCountSoFar() + " " + tracker.getCounterLabel();
        }
        return quantifier;
    }

    @Override
    public String getTrackerQuantifierOne(Tracker tracker) {
        if(tracker.isTimeTracker()){
            return " - " + tracker.getCountSoFar()/60 + " hours";
        }else{
            return " - " + tracker.getCountSoFar() + " " + tracker.getCounterLabel();
        }
    }

    @Override
    public String getTrackerQuantifierTwo(Tracker tracker) {
        return " - " + tracker.getCountSoFar() + " minutes";
    }


    @Override
    public String getLevelIndicator(Tracker tracker) {
        return tracker.getLevelToDisplay();
    }

    @Override
    public void changeTrackerTitle(Tracker tracker, String newTitle) {
        // todo
    }

    @Override
    public void changeTrackerMaxScore(Tracker tracker, int newMax) {
        // todo
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
        // todo (currently temp delete)

        mTrackersRepository.deleteTracker(tracker.getId());
        mTrackersView.removeTracker(tracker);

        //mTrackersView.showTrackerDetailsScreen(tracker.getId());
    }

    @Override
    public void changeTrackerOrder(int from, int to) {

        //todo
//                ArrayList<Tracker> ls = mListAdapter.getItems();
//
//                //Assuming that item is moved up the list
//                int direction = -1;
//
//                //For instance where the item is dragged down the list
//                if(from < to) {
//                    direction = 1;
//                }
//
//                if(from == 0 || from == (ls.size() + 1)){
//                    Log.d(TAG, "drop: can't move graph or footer");
//                    return;
//                }
//                if(to == 0){
//                    to = 1;
//                }
//                if (to  == (ls.size() + 1)){
//                    to = ls.size();
//                }
//
//                from -= 1;
//                to -= 1;
//
//                Object target = ls.get(from);
//                for(int i = from; i != to ; i += direction){
//                    ls.set(i, ls.get(i+direction));
//                }
//                ls.set(to, (Tracker) target);
//
//                trackerAdapter.setItems(ls);
    }
}
