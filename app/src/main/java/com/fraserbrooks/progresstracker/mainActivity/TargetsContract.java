package com.fraserbrooks.progresstracker.mainActivity;

import com.fraserbrooks.progresstracker.BasePresenter;
import com.fraserbrooks.progresstracker.BaseView;
import com.fraserbrooks.progresstracker.data.Target;

import java.util.List;

public interface TargetsContract {

    interface View extends BaseView<Presenter> {

        void showTargets(List<Target> targets);

        void updateOrAddTarget(Target target);

        void removeTarget(Target target);

        void showLoading();

        void hideLoading();

        void showNoTargets();

        void showNoDataAvailable();

        void showTargetDetailsScreen(String targetId);

        void showAddTargetScreen();

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void loadTargets();

        void addTargetButtonClicked();

        int getTargetAverageCompletion(Target target);

        int getTargetCurrentPercentage(Target target);

        String getTargetTitle(Target target);

        String getTopRightLabel(Target target);

        String getLowerLeftLabel(Target target);

        void moreDetailsButtonClicked(Target target);

        void changeTargetOrder(int from, int to);


    }

}
