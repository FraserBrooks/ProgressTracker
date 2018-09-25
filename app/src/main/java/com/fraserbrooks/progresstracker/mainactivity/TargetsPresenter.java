package com.fraserbrooks.progresstracker.mainactivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.data.source.DataSource;
import com.fraserbrooks.progresstracker.data.source.Repository;
import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;

import java.util.List;

public class TargetsPresenter implements TargetsContract.Presenter{

    private final String TAG = "TargetsPresenter";

    private final Repository mRepository;

    private final TargetsContract.View mTargetsView;

    public TargetsPresenter(@NonNull Repository trackersRepository,
                            @NonNull TargetsContract.View targetsView){
        mRepository = trackersRepository;
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
            public void targetDeleted(Target targetToDelete) {
                mTargetsView.removeTarget(targetToDelete);
            }


        });
        mRepository.addUpdateOrAddTargetListener(new DataSource.UpdateOrAddTargetListener() {
            @Override
            public boolean isActive() {
                return mTargetsView.isActive();
            }

            @Override
            public void targetUpdated(Target targetToUpdate) {
                mTargetsView.updateOrAddTarget(targetToUpdate);
            }
        });
        loadTargets();
    }

    @Override
    public void loadTargets() {

        mTargetsView.showLoading();

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        setWorking();

        mRepository.getTargets(new DataSource.GetTargetsCallback() {
            @Override
            public void onTargetsLoaded(List<Target> targets) {
                // The view may not be able to handle UI updates anymore
                if (!mTargetsView.isActive()) {
                    Log.d(TAG, "onTargetsLoaded: targetsView not active, exiting callback");
                    return;
                }

                for(Target target : targets){
                    mTargetsView.updateOrAddTarget(target);
                }

                mTargetsView.hideLoading();
                setIdle();
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTargetsView.isActive()) {
                    Log.d(TAG, "onTargetsLoaded: targetsView not active, exiting callback");
                    return;
                }
                mTargetsView.hideLoading();
                mTargetsView.showNoTargets();
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
