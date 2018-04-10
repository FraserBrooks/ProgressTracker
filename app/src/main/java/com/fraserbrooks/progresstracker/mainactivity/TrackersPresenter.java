package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;

import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
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

    private final Repository mTrackersRepository;

    private final TrackersContract.View mTrackersView;

    private boolean mFirstLoad = true;

    public TrackersPresenter(@NonNull Repository trackersRepository,
                             @NonNull TrackersContract.View trackersView){
        mTrackersRepository = checkNotNull(trackersRepository);
        mTrackersView = checkNotNull(trackersView);

        mTrackersView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTrackers(false);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link Repository}
     */
    @Override
    public void loadTrackers(boolean forceUpdate) {
        // A network reload will be forced on first load.
        if(forceUpdate || mFirstLoad){
            mTrackersRepository.refreshTrackers();
        }
        mFirstLoad = false;

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mTrackersRepository.getTrackers(new DataSource.LoadTrackersCallback() {
            @Override
            public void onTrackersLoaded(List<Tracker> trackers) {
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }
                // The view may not be able to handle UI updates anymore
                if (!mTrackersView.isActive()) {
                    return;
                }

                if(trackers.isEmpty()){
                    mTrackersView.showNoTrackers();
                }else{
                    mTrackersView.showTrackers(trackers);
                }
            }

            @Override
            public void onDataNotAvailable() {
                mTrackersView.showNoDataAvailable();
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
        String levelToDisplay;

        // Only display level if past max level or if no difficulty is set
        if (tracker.getLevel() > 8 ||
                (tracker.getCountToMaxLevel() == 0 && tracker.getLevel() > 0)){
            int l = tracker.getLevel();
            l = (tracker.getCountToMaxLevel() == 0) ? l : l-8; //subtract 8 if no difficulty
            levelToDisplay = "" + l;
        } else{
            levelToDisplay = ""; // Don't display level
        }
        return levelToDisplay;
    }

    @Override
    public void changeTrackerTitle(Tracker tracker, String newTitle) {
        // todo
    }

    @Override
    public void changeTrackerMaxCount(Tracker tracker, int newMax) {
        // todo
    }

    @Override
    public void addToTrackerCount(Tracker tracker, int c) {
        // todo
    }

    @Override
    public void timerButtonClicked(Tracker tracker) {
        // todo
    }

    @Override
    public void moreDetailsButtonClicked(Tracker tracker) {
        // todo
    }

    @Override
    public void changeTrackerOrder(int from, int to) {

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
