package com.fraserbrooks.progresstracker.util;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.graphs.TrackerLinePlotGraph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TrackerViewInflater {


    private TrackerFunctionsInterface mTrackerInterface;
    private Context mContext;

    private int[] levelIcons = {
            R.drawable.frag_trackers_gem_yellow,
            R.drawable.frag_trackers_gem_orange,
            R.drawable.frag_trackers_gem_green,
            R.drawable.frag_trackers_gem_purple,
            R.drawable.frag_trackers_gem_lightblue,
            R.drawable.frag_trackers_gem_blue,
            R.drawable.frag_trackers_gem_brown,
            R.drawable.frag_trackers_gem_black,
            R.drawable.frag_trackers_gem_blank};

    private int[] levelRects = {
            R.drawable.frag_trackers_level1_colour_rect,
            R.drawable.frag_trackers_level2_colour_rect,
            R.drawable.frag_trackers_level3_colour_rect,
            R.drawable.frag_trackers_level4_colour_rect,
            R.drawable.frag_trackers_level5_colour_rect,
            R.drawable.frag_trackers_level6_colour_rect,
            R.drawable.frag_trackers_level7_colour_rect,
            R.drawable.frag_trackers_level8_colour_rect
    };

    public TrackerViewInflater(@NonNull Context context,
                               @NonNull TrackerFunctionsInterface trackerInterface){
        mTrackerInterface = trackerInterface;
        mContext = context;
    }



    public void inflateTracker(View view, Tracker tracker, boolean expanded){

        // Get icon bitmap
        ImageView iconImageView;
        if(expanded){
            iconImageView = view.findViewById(R.id.gem_bitmap_unexpanded);
            iconImageView.setVisibility(View.GONE);
            iconImageView = view.findViewById(R.id.gem_bitmap);
        }else{
            iconImageView = view.findViewById(R.id.gem_bitmap_unexpanded);
            iconImageView.setVisibility(View.VISIBLE);
        }

        // Set the level indicator text view
        TextView levelIndicator;
        if(expanded){
            levelIndicator = view.findViewById(R.id.text_level_tv_unexpanded);
            levelIndicator.setVisibility(View.GONE);
            levelIndicator = view.findViewById(R.id.text_level_tv);
        } else{
            levelIndicator = view.findViewById(R.id.text_level_tv_unexpanded);
            levelIndicator.setVisibility(View.VISIBLE);
        }
        String levelToDisplay = getLevelIndicator(tracker);
        levelIndicator.setText(levelToDisplay);

        // Get different layouts and make sure correct one is showing
        ViewGroup smallLayout = view.findViewById(R.id.small_tracker_layout);
        View expandedLayout = view.findViewById(R.id.expanded_tracker_layout);

        // ExpandedLayout will be null if not calling from trackersFragment
        if(expanded && expandedLayout != null){
            // Show expanded view
            expandedLayout.setVisibility(View.VISIBLE);
            smallLayout.setVisibility(View.GONE);
            view = expandedLayout;
        }else{
            // Hide expanded part of view
            if(expandedLayout != null) expandedLayout.setVisibility(View.GONE);
            smallLayout.setVisibility(View.VISIBLE);
            view = smallLayout;
        }

        //Get Widgets
        TextView tvName = view.findViewById(R.id.name_tv);
        TextView tvQuantifier = view.findViewById(R.id.quantifier_tv);


        // Set name
        String name = tracker.getTitle();
        tvName.setText(name);

        // Set quantifier ( - 45 hours, 6 lectures, etc.)
        String quantifier = getTrackerQuantifier(tracker);
        tvQuantifier.setText(quantifier);

        setProgressBarAndIcons(tracker, view, iconImageView);

        if(tracker.isExpanded() && expandedLayout != null){
            // init extra widgets

            // Second quantifier (eg. minutes)
            TextView secondQuantifier = view.findViewById(R.id.quantifier_two_tv);
            if(tracker.isTimeTracker() && tracker.getCountSoFar() > 59){
                secondQuantifier.setText(getTrackerQuantifierTwo(tracker));
            }else{
                secondQuantifier.setVisibility(View.GONE);
            }

            // Count to max/level 8
            TextView countToMaxTv = view.findViewById(R.id.count_to_max_tv);
            countToMaxTv.setText(mContext.getResources().getString(
                    R.string.count_to_complete,
                    tracker.getProgressionRate()));

            initButtons(view, tracker);
        }

    }


    private void setProgressBarAndIcons(Tracker tracker, View view, ImageView levelIconImage){

        //Get Widgets
        ImageView nextLevelIcon = view.findViewById(R.id.next_level_gem_bitmap);
        ViewGroup progressBarGraphView = view.findViewById(R.id.progress_bar_graph_view);
        ViewGroup progressBar = view.findViewById(R.id.progress_bar_layout);
        ImageView progressBarImage = view.findViewById(R.id.progress_bar_image);
        // The textViews that make the progress bar via their backgrounds
        TextView tvFilledPart = view.findViewById(R.id.progress_bar_filled_part);
        TextView tvNotFilledPart = view.findViewById(R.id.progress_bar_not_filled_part);



        //Icons and progress bar
        float progress = tracker.getPercentageToNextLevel();
        setProgressBarDimensions(progress, tvFilledPart, tvNotFilledPart);

        if(tracker.isLevelUpTracker()){

            progressBar.setVisibility(View.VISIBLE);
            progressBarImage.setVisibility(View.VISIBLE);
            progressBarGraphView.setVisibility(View.GONE);
            progressBarGraphView.removeAllViews();

            int k = (tracker.getLevel() > 7) ? 7 : tracker.getLevel();
            levelIconImage.setImageResource(levelIcons[k]);
            tvFilledPart.setBackgroundResource(levelRects[k]);

            k = (k == 7) ? 7 : k + 1;
            if(nextLevelIcon != null) nextLevelIcon.setImageResource(levelIcons[k]);


        }else{

            if(nextLevelIcon != null)nextLevelIcon.setVisibility(View.GONE);

            // TODO: add other options
            levelIconImage.setImageResource(R.drawable.frag_trackers_heart_red);

            View graphPlot = new TrackerLinePlotGraph(mContext, tracker,
                    mContext.getResources().getDimension(R.dimen.tracker_list_graph_height),
                    R.drawable.frag_trackers_heart_colour_rect);

            progressBar.setVisibility(View.GONE);
            progressBarImage.setVisibility(View.GONE);

            progressBarGraphView.setVisibility(View.VISIBLE);
            progressBarGraphView.removeAllViews();
            progressBarGraphView.addView(graphPlot);

        }




    }

    private void setProgressBarDimensions(float progress, TextView tvFilledRect, TextView tvNotFilledRect) {
        //create Progress bar layouts
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
        tvFilledRect.setLayoutParams(paramForFilledPart);
        tvNotFilledRect.setLayoutParams(paramForNotFilledPart);
    }


    private void initButtons(View view, final Tracker tracker) {
        //Get buttons
        Button topButton1 = view.findViewById(R.id.top_button_1);
        Button topButton2 = view.findViewById(R.id.top_button_2);
        Button topButton3 = view.findViewById(R.id.top_button_3);
        Button topButton4 = view.findViewById(R.id.top_button_4);


        // Timer button
        if(tracker.isCurrentlyTiming()){
            topButton1.setText(R.string.end_timer);
            topButton1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }else{
            topButton1.setText(R.string.start_timer);
            topButton1.setTextColor(mContext.getResources().getColor(R.color.default_text_color));
        }
        topButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackerInterface.timerButtonClicked(tracker);
            }
        });

        final int button2AddAmount;
        final int button3AddAmount;

        // More details button
        topButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackerInterface.moreDetailsButtonClicked(tracker);
            }
        });


        if(tracker.isTimeTracker()){

            //Show timer button
            topButton1.setVisibility(View.VISIBLE);

            topButton2.setText(R.string.plus_15_minutes);
            topButton3.setText(R.string.plus_1_hour);

            button2AddAmount = 15;
            button3AddAmount = 60;

        }else{

            //hide timer button
            topButton1.setVisibility(View.GONE);

            topButton2.setText(mContext.getResources()
                    .getString(R.string.add_1_count,tracker.getCounterLabel()));
            topButton3.setText(mContext.getResources()
                    .getString(R.string.add_5_count,tracker.getCounterLabel()));


            button2AddAmount = 1;
            button3AddAmount = 5;

        }

        topButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackerInterface.addToTrackerScore(tracker, button2AddAmount);
            }
        });

        topButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackerInterface.addToTrackerScore(tracker, button3AddAmount);
            }
        });
    }


    private String getTrackerQuantifier(Tracker tracker) {
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


    private String getTrackerQuantifierTwo(Tracker tracker) {
        return " - " + tracker.getCountSoFar() + " minutes";
    }


    private String getLevelIndicator(Tracker tracker) {
        return tracker.getLevelToDisplay();
    }

}
