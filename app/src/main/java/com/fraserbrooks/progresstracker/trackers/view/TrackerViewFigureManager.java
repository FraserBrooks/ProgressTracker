package com.fraserbrooks.progresstracker.trackers.view;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.ColorUtils;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.trackers.view.trackergraphs.TrackerBooleanGraphView;
import com.fraserbrooks.progresstracker.trackers.view.trackergraphs.TrackerTimeGraphView;

class TrackerViewFigureManager {

    private TrackerTimeGraphView mTrackerTimeGraphView;
    private TrackerBooleanGraphView mTrackerBooleanGraphView;
    private ViewGroup mProgressBarLayout;

    private View mTrackerView, mProgressBarFilled, mProgressBarNotFilled;


    TrackerViewFigureManager(View trackerView){

        mTrackerView = trackerView;

        // Find Possible Tracker Figures:
        // i.e. Time Graph, Boolean Graph, or Progress Bar

        // Time Graph (with option for weekly, monthly, or yearly graph)
        mTrackerTimeGraphView =  mTrackerView.findViewById(R.id.tracker_view_time_graph);

        // Boolean graph: e.g.  0---X---0---X---0---0---X-
        mTrackerBooleanGraphView =  mTrackerView.findViewById(R.id.tracker_view_boolean_graph);

        // Progress Bar
        mProgressBarLayout =  mTrackerView.findViewById(R.id.tracker_view_progress_bar_layout);
        mProgressBarFilled =  mTrackerView.findViewById(R.id.progress_bar_filled_part);
        mProgressBarNotFilled =  mTrackerView.findViewById(R.id.progress_bar_not_filled_part);


        // Default Visibility
        mTrackerTimeGraphView.setVisibility(View.GONE);
        mTrackerBooleanGraphView.setVisibility(View.GONE);
    }


    void initFigureWith(Tracker tracker){

        int trackerColor = ColorUtils.getTrackerColor(mTrackerView.getContext(), tracker);

        GradientDrawable gradientDrawable =
                ColorUtils.getGradientDrawable(mTrackerView.getContext(),trackerColor);

        if(tracker.getType() == Tracker.TYPE_LEVEL_UP){
            initProgressBar(tracker, gradientDrawable);
        }else{
            mProgressBarLayout.setVisibility(View.INVISIBLE);
        }

        if (tracker.getType() == Tracker.TYPE_GRAPH) {
            initTimeGraph(tracker, gradientDrawable);
        }else{
            mTrackerTimeGraphView.setVisibility(View.GONE);
        }

        if(tracker.getType() == Tracker.TYPE_BOOLEAN){
            mTrackerBooleanGraphView.setVisibility(View.VISIBLE);
            mTrackerBooleanGraphView.initGraph(tracker);
        }else{
            mTrackerBooleanGraphView.setVisibility(View.GONE);
        }


    }

    private void initTimeGraph(Tracker tracker, GradientDrawable gradientDrawable) {
        mTrackerTimeGraphView.setVisibility(View.VISIBLE);
        mTrackerTimeGraphView.setGraphBarResource(gradientDrawable);

        switch (tracker.getGraphType()) {
            case Tracker.GRAPH_TYPE_DAY:
                mTrackerTimeGraphView.setGraphType(TrackerTimeGraphView.DAY);
                mTrackerTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
                break;
            case Tracker.GRAPH_TYPE_WEEK:
                mTrackerTimeGraphView.setGraphType(TrackerTimeGraphView.WEEK);
                mTrackerTimeGraphView.initGraph(tracker.getPastEightWeeksCounts());
                break;
            case Tracker.GRAPH_TYPE_MONTH:
                mTrackerTimeGraphView.setGraphType(TrackerTimeGraphView.MONTH);
                mTrackerTimeGraphView.initGraph(tracker.getPastEightMonthsCounts());
                break;
            case Tracker.GRAPH_TYPE_YEAR:
                // todo
                mTrackerTimeGraphView.setGraphType(TrackerTimeGraphView.DAY);
                mTrackerTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
                break;
            default:
                mTrackerTimeGraphView.setGraphType(TrackerTimeGraphView.DAY);
                mTrackerTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
        }
    }


    private void initProgressBar(Tracker tracker, GradientDrawable gradientDrawable) {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mProgressBarFilled.setBackground(gradientDrawable);

        // Set progress bar dimensions
        float progress = tracker.getPercentageToNextLevel();

        LinearLayout.LayoutParams paramForFilledPart = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                progress
        );
        LinearLayout.LayoutParams paramForNotFilledPart = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                (1f - progress)
        );
        mProgressBarFilled.setLayoutParams(paramForFilledPart);
        mProgressBarNotFilled.setLayoutParams(paramForNotFilledPart);
    }

}
