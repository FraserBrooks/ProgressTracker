package com.fraserbrooks.progresstracker.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Tracker;

public class TrackerViewInflater {


    private TrackerFunctionsInterface mTrackerInterface;
    private Context mContext;

    private int[] levelIcons = {
            R.drawable.frag_trackers_heart_red,
            R.drawable.frag_trackers_gem_blank,
            R.drawable.frag_trackers_gem_yellow,
            R.drawable.frag_trackers_gem_orange,
            R.drawable.frag_trackers_gem_green,
            R.drawable.frag_trackers_gem_purple,
            R.drawable.frag_trackers_gem_lightblue,
            R.drawable.frag_trackers_gem_blue,
            R.drawable.frag_trackers_gem_brown,
            R.drawable.frag_trackers_gem_black};
    private int[] levelRects = {
            R.drawable.frag_trackers_heart_colour_rect,
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

        //Make sure correct view is showing
        View smallLayout = view.findViewById(R.id.small_tracker_layout);
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


        // Set the level indicator text view
        TextView levelIndicator = view.findViewById(R.id.text_level_tv);
        String levelToDisplay = getLevelIndicator(tracker);
        levelIndicator.setText(levelToDisplay);

        //Get Widgets
        TextView tvName = view.findViewById(R.id.name_tv);
        TextView tvQuantifier = view.findViewById(R.id.quantifier_tv);
        ImageView iconImage = view.findViewById(R.id.gem_bitmap);
        TextView tvFilledPart = view.findViewById(R.id.progress_bar_filled_part);
        TextView tvNotFilledPart = view.findViewById(R.id.progress_bar_not_filled_part);

        // Set name
        String name = tracker.getTitle();
        tvName.setText(name);

        // Set quantifier ( - 45 hours, 6 lectures, etc.)
        String quantifier = getTrackerQuantifier(tracker);
        tvQuantifier.setText(quantifier);

        //Icon and progress bar
        float progress = tracker.getPercentageToNextLevel();
        setIconAndProgressBarColours(tracker, tvFilledPart, iconImage);
        setProgressBarDimensions(progress, tvFilledPart, tvNotFilledPart);


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

    private void setProgressBarDimensions(float progress, TextView tvFilledRect, TextView tvNotFilledRect) {
        //create Progress bar layouts
        LinearLayout.LayoutParams paramForFilledPart = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                progress
        );
        LinearLayout.LayoutParams paramForNotFilledPart = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (1f - progress)
        );
        tvFilledRect.setLayoutParams(paramForFilledPart);
        tvNotFilledRect.setLayoutParams(paramForNotFilledPart);
    }

    private void setIconAndProgressBarColours(Tracker tracker, TextView tvFilledRect, ImageView iconImage) {
        if(tracker.getProgressionRate() == 0){
            iconImage.setImageResource(levelIcons[0]);
            tvFilledRect.setBackgroundResource(levelRects[0]);
        }else{
            int k = (tracker.getLevel() > 8) ? 8 : tracker.getLevel();
            iconImage.setImageResource(levelIcons[k+1]);
            int i = (k >= 8) ? 8 : k + 1;
            tvFilledRect.setBackgroundResource(levelRects[i]);
        }
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
