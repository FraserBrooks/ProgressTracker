package com.fraserbrooks.progresstracker.trackers.view;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.ColorUtils;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

import androidx.annotation.ColorInt;

class TrackerViewButtonManager {

    public interface TrackerInterfaceCallback{


        TrackerFunctionsInterface getInterface();

    }

    private TrackerInterfaceCallback mCallback;

    private Button mStartStopTimerButton, mAddSmallButton,
            mAddLargeButton, mMoreDetailsButton, mBooleanButton;

    private View mTrackerView;

    TrackerViewButtonManager(View trackerView, TrackerInterfaceCallback callback){

        mTrackerView = trackerView;
        mCallback = callback;

        // Buttons
        mStartStopTimerButton =  trackerView.findViewById(R.id.tracker_view_button_1);
        mAddSmallButton =  trackerView.findViewById(R.id.tracker_view_button_2);
        mAddLargeButton =  trackerView.findViewById(R.id.tracker_view_button_3);
        mMoreDetailsButton =  trackerView.findViewById(R.id.tracker_view_button_4);
        mBooleanButton = trackerView.findViewById(R.id.tracker_view_boolean_button);

    }

    void showButtons(){

        // Show buttons according to tags
        if ( mStartStopTimerButton.getTag() == null || ((int) mStartStopTimerButton.getTag()) == View.VISIBLE) {
            mStartStopTimerButton.setVisibility(View.VISIBLE);
        }
        if ( mAddSmallButton.getTag() == null || ((int) mAddSmallButton.getTag()) == View.VISIBLE){
            mAddSmallButton.setVisibility(View.VISIBLE);
        }
        if ( mAddLargeButton.getTag() == null || ((int) mAddLargeButton.getTag()) == View.VISIBLE){
            mAddLargeButton.setVisibility(View.VISIBLE);
        }

        mMoreDetailsButton.setVisibility(View.VISIBLE);

        if ( mBooleanButton.getTag() == null || ((int) mBooleanButton.getTag()) == View.VISIBLE){
            mBooleanButton.setVisibility(View.VISIBLE);
        }

    }


    void hideButtons(){

        // Hide buttons
        mStartStopTimerButton.setVisibility(View.GONE);
        mAddSmallButton.setVisibility(View.GONE);
        mAddLargeButton.setVisibility(View.GONE);
        mMoreDetailsButton.setVisibility(View.GONE);
        mBooleanButton.setVisibility(View.GONE);

    }

    void initButtonsWith(Tracker tracker) {

        // More details Button. Visible/used in all tracker types
        mMoreDetailsButton.setText(R.string.more);
        mMoreDetailsButton.setOnClickListener(view -> mCallback.getInterface().moreDetailsButtonClicked(tracker));

        // Tracker Figure is boolean graph
        // i.e.      Up by 8am (weekly)
        //           -0---X---0---X---0---0---X-
        if(tracker.getType() == Tracker.TYPE_BOOLEAN){

            //Show/hide appropriate buttons & set tags so we know whether to show when expanding
            setButtonVisibilityAndTags(View.GONE, View.GONE, View.GONE, View.VISIBLE);

            initYesNoButtons(tracker);

        }else{
            //Show/hide appropriate buttons & set tags so we know whether to show when expanding
            setButtonVisibilityAndTags(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
        }

        // Set button increment amounts (15 & 60 minutes or 1 & 5 by default)

        int addSmallAmount;
        int addLargeAmount;

        // todo: allow user defined values
        if(tracker.isTimeTracker()){
            addSmallAmount = 15;
            addLargeAmount = 60;
        }else{
            addSmallAmount = 1;
            addLargeAmount = 5;
        }


        if(tracker.isTimeTracker()){
            // Tracker counter tracks time: minutes/hours
            // i.e. Tracker score = minutes recorded

            initTimeButtons(tracker, addSmallAmount, addLargeAmount);

        }else{
            // Tracker counter tracks user defined numerical value
            // e.g. Tracker score = Book Chapters Read
            //  or: Tracker score = Guitar Lessons


            mAddSmallButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_ys,
                    addSmallAmount, tracker.getCounterLabel()));
            mAddLargeButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_ys,
                    addLargeAmount, tracker.getCounterLabel()));


        }


        setListeners(addSmallAmount, addLargeAmount, tracker);

    }

    private void initTimeButtons(Tracker tracker, int addSmallAmount, int addLargeAmount) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mTrackerView.getContext().getTheme();
        theme.resolveAttribute(R.attr.color_accent, typedValue, true);
        @ColorInt int colorAccent = typedValue.data;
        theme.resolveAttribute(R.attr.theme_text_color, typedValue, true);
        @ColorInt int defaultColor = typedValue.data;

        if(tracker.isCurrentlyTiming()){
            mStartStopTimerButton.setText(R.string.end_timer);
            mStartStopTimerButton.setTextColor(colorAccent);
        }else{
            mStartStopTimerButton.setText(R.string.start_timer);
            mStartStopTimerButton.setTextColor(defaultColor);
        }
        mStartStopTimerButton.setOnClickListener(view -> mCallback.getInterface().timerButtonClicked(tracker));


        if(addSmallAmount > 59){
            mAddSmallButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_hours, addSmallAmount/60));
        }else{
            mAddSmallButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_minutes, addSmallAmount));
        }
        if(addLargeAmount > 59){
            mAddLargeButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_hours, addLargeAmount/60));
        }else{
            mAddLargeButton.setText(mTrackerView.getContext().getResources().getString(R.string.plus_x_minutes, addLargeAmount));
        }

    }


    private void initYesNoButtons(Tracker tracker){

        int trackerColor = ColorUtils.getTrackerColor(mTrackerView.getContext(), tracker);

        VectorMasterDrawable circleDrawable = new VectorMasterDrawable(mTrackerView.getContext(),
                R.drawable.ico_nested_circles);

        switch (tracker.getGraphType()) {

            case Tracker.GRAPH_TYPE_WEEK:
                mBooleanButton.setText(mTrackerView.getContext().getResources().getString(R.string.this_week));
                if (tracker.getPastEightWeeksCounts()[7] >= 1) {
                    ColorUtils.setColoredCircle(mTrackerView.getContext(), circleDrawable, trackerColor);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().clearWeek(tracker.getId()));
                } else {
                    ColorUtils.setBlankCircle(mTrackerView.getContext(), circleDrawable);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().incrementTrackerScore(tracker, 1));
                }
                break;
            case Tracker.GRAPH_TYPE_MONTH:
                mBooleanButton.setText(mTrackerView.getContext().getResources().getString(R.string.this_month));
                if (tracker.getPastEightMonthsCounts()[7] >= 1) {
                    ColorUtils.setColoredCircle(mTrackerView.getContext(), circleDrawable, trackerColor);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().clearMonth(tracker.getId()));
                } else {
                    ColorUtils.setBlankCircle(mTrackerView.getContext(), circleDrawable);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().incrementTrackerScore(tracker, 1));
                }
                break;
            case Tracker.GRAPH_TYPE_YEAR:
            case Tracker.GRAPH_TYPE_DAY:
            default:
                mBooleanButton.setText(mTrackerView.getContext().getResources().getString(R.string.today));
                if (tracker.getPastEightDaysCounts()[7] >= 1) {
                    ColorUtils.setColoredCircle(mTrackerView.getContext(), circleDrawable, trackerColor);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().incrementTrackerScore(tracker, -1));
                } else {
                    ColorUtils.setBlankCircle(mTrackerView.getContext(), circleDrawable);
                    mBooleanButton.setOnClickListener(v -> mCallback.getInterface().incrementTrackerScore(tracker, 1));
                }
        }

        mBooleanButton.setCompoundDrawablesRelativeWithIntrinsicBounds(circleDrawable, null, null, null);

    }

    /** Show/hide appropriate buttons & set tags so we know whether to show when expanding **/
    private void setButtonVisibilityAndTags(int b1, int b2,
                                            int b3, int b4){
        mStartStopTimerButton.setVisibility(b1);
        mAddSmallButton.setVisibility(b2);
        mAddLargeButton.setVisibility(b3);
        mBooleanButton.setVisibility(b4);

        mStartStopTimerButton.setTag(b1);
        mAddSmallButton.setTag(b2);
        mAddLargeButton.setTag(b3);
        mBooleanButton.setTag(b4);

    }

    private void setListeners(int button2AddAmount, int button3AddAmount,
                              Tracker tracker) {
        mAddSmallButton.setOnClickListener(view -> mCallback.getInterface().incrementTrackerScore(tracker, button2AddAmount));
        mAddLargeButton.setOnClickListener(view -> mCallback.getInterface().incrementTrackerScore(tracker, button3AddAmount));

    }





}


