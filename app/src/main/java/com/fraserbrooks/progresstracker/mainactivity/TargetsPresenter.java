package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fraserbrooks.progresstracker.asynctasks.LoadTargetsTask;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.List;

public class TargetsPresenter implements TargetsContract.Presenter{

    private final String TAG = "TargetsPresenter";

    private final Repository mRepository;
    private final AppExecutors mAppExecutors;

    private final TargetsContract.View mTargetsView;

    public TargetsPresenter(@NonNull Repository trackersRepository,
                            @NonNull TargetsContract.View targetsView,
                            @NonNull AppExecutors appExecutors){
        mRepository = trackersRepository;
        mAppExecutors = appExecutors;
        mTargetsView = targetsView;

        mTargetsView.setPresenter(this);
    }

    @Override
    public void start() {
        mRepository.addDeleteTargetListener(new Repository.DeleteTargetListener() {
            @Override
            public boolean isActive() {
                return mTargetsView.isActive();
            }

            @Override
            public void trackerDeleted(Target targetToDelete) {
                mTargetsView.removeTarget(targetToDelete);
            }


        });
        loadTargets();
    }

    @Override
    public void loadTargets() {

        mTargetsView.showLoading();

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRepository.getTargets(new DataSource.GetTargetsCallback() {
                    @Override
                    public void onTargetsLoaded(final List<Target> targets) {
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                // The view may not be able to handle UI updates anymore
                                if (!mTargetsView.isActive()) {
                                    return;
                                }

                                mTargetsView.hideLoading();

                                if(targets.isEmpty()){
                                    Log.d(TAG, "onTargetsLoaded: no targets to show");
                                    mTargetsView.showNoTargets();
                                }
                                else{
                                    // Create AsyncTask to spread out the redrawing of trackers
                                    // otherwise the whole listView will be redrawn at once
                                    Log.d(TAG, "onTargetsLoaded: creating new LoadTargetsTask");
                                    new LoadTargetsTask(new DataSource.GetTargetsCallback() {
                                        @Override
                                        public void onTargetsLoaded(@Nullable List<Target> targets) {
                                            //called when AsyncTask finished but targets = null
                                            //nothing to do
                                            Log.d(TAG, "onTargetsLoaded: LoadTargetsTask finished" );
                                        }

                                        @Override
                                        public void onTargetLoaded(Target target) {
                                            // The view may not be able to handle UI updates anymore
                                            if (!mTargetsView.isActive()) {
                                                Log.d(TAG, "onTargetsLoaded: mTargetsView not active. exiting from callback");
                                                return;
                                            }
                                            mTargetsView.updateOrAddTarget(target);
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.e(TAG, "onDataNotAvailable from async task");
                                        }
                                    }).execute(targets.toArray(new Target[targets.size()]));
                                }
                            }
                        });
                    }

                    @Override
                    public void onTargetLoaded(final Target target) {
                        Log.e(TAG, "onTargetLoaded: called");
//                      // should never be called when staggeredLoad = false
                    }

                    @Override
                    public void onDataNotAvailable() {
                        mAppExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mTargetsView.showNoDataAvailable();
                            }
                        });
                    }
                }, false);
            }
        });

    }

    @Override
    public void addTargetButtonClicked() {
        mTargetsView.showAddTargetScreen();
    }

    @Override
    public int getTargetAverageCompletion(final Target target) {
        return target.getAverageOverTime();
    }

    @Override
    public int getTargetCurrentPercentage(final Target target) {
        return target.getCurrentProgressPercentage();
    }

    @Override
    public String getTargetTitle(Target target) {
        return target.getTargetTitle();
    }

    @Override
    public String getTopRightLabel(Target target) {
        return target.getTopRightLabel();
    }

    @Override
    public String getLowerLeftLabel(Target target) {
        return target.getLowerLeftLabel();
    }

    @Override
    public void moreDetailsButtonClicked(Target target) {

    }

    @Override
    public void changeTargetOrder(int from, int to) {

    }


}
