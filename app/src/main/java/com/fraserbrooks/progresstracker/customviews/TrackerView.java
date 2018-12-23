package com.fraserbrooks.progresstracker.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.graphs.TimeGraphView;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

public class TrackerView extends FrameLayout{

    private static final String TAG = "TrackerView";

    private ViewGroup mTopHalfLayout;

    private TimeGraphView mTimeGraphView;
    private ViewGroup mCheckListView;
    private ViewGroup  mProgressBarLayout;

    private View mProgressBarFilled, mProgressBarNotFilled;
    private TextView mTitleTextView, mRightHandTextView, mTextView2, mTextView3,
            mDifficultyTextView, mLevelTextView;

    private ImageView mTrackerIcon, mNextLevelIcon, mMaxLevelIcon;



    // Expanded (lower half) Layout
    private ViewGroup mButtonsLayout;
    private Button mButton1, mButton2, mButton3, mButton4;


    public TrackerView(@NonNull Context context){
        super(context);
        this.init(null);
    }

    public TrackerView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public TrackerView(@NonNull Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {

        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);
        inflater.inflate(R.layout.frag_trackers_tracker_custom_view, this);




        // Top half of view to act as button
        mTopHalfLayout = getRootView().findViewById(R.id.tracker_view_top_half_layout);

        // Only visible when expanded
        mButtonsLayout = getRootView().findViewById(R.id.tracker_view_buttons_layout);

        // Graphs
        mTimeGraphView = getRootView().findViewById(R.id.tracker_view_graph);
        mCheckListView = getRootView().findViewById(R.id.tracker_view_check_list);

        // Progress Bar
        mProgressBarLayout = getRootView().findViewById(R.id.tracker_view_progress_bar_layout);
        mProgressBarFilled = getRootView().findViewById(R.id.progress_bar_filled_part);
        mProgressBarNotFilled = getRootView().findViewById(R.id.progress_bar_not_filled_part);

        // TextViews
        mTitleTextView = getRootView().findViewById(R.id.tracker_view_title);
        mRightHandTextView = getRootView().findViewById(R.id.tracker_view_right_hand_text);
        mTextView2 = getRootView().findViewById(R.id.tracker_view_text_view_2);
        mTextView3 = getRootView().findViewById(R.id.tracker_view_text_view_3);
        mDifficultyTextView = getRootView().findViewById(R.id.tracker_view_difficulty_text_view);
        mLevelTextView = getRootView().findViewById(R.id.tracker_view_level_text);

        // Icon Views
        mTrackerIcon = getRootView().findViewById(R.id.tracker_view_ico);
        mNextLevelIcon = getRootView().findViewById(R.id.tracker_view_next_level_ico);
        mMaxLevelIcon = getRootView().findViewById(R.id.tracker_view_max_level_ico);

        // Buttons
        mButton1 = getRootView().findViewById(R.id.tracker_view_button_1);
        mButton2 = getRootView().findViewById(R.id.tracker_view_button_2);
        mButton3 = getRootView().findViewById(R.id.tracker_view_button_3);
        mButton4 = getRootView().findViewById(R.id.tracker_view_button_4);

        collapseView();

        mTopHalfLayout.setOnClickListener(view -> {
            if(mButtonsLayout.getVisibility() == GONE){
                expandView();
            }else{
                collapseView();
            }
        });

    }

    public void collapseView() {
        mButtonsLayout.setVisibility(GONE);
        mNextLevelIcon.setVisibility(GONE);
        mMaxLevelIcon.setVisibility(GONE);

        mDifficultyTextView.setVisibility(GONE);
        mTextView2.setVisibility(GONE);
        mTextView3.setVisibility(GONE);
    }

    public void expandView() {
        mButtonsLayout.setVisibility(VISIBLE);
        mNextLevelIcon.setVisibility(VISIBLE);
        mMaxLevelIcon.setVisibility(VISIBLE);

        mDifficultyTextView.setVisibility(VISIBLE);
        mTextView2.setVisibility(VISIBLE);
        mTextView3.setVisibility(VISIBLE);
    }

    public void setTracker(Tracker tracker, TrackerFunctionsInterface trackerInterface){

        mButtonsLayout.setVisibility(GONE);

        mTitleTextView.setText(tracker.getTitle());
        mRightHandTextView.setText(getCountLabel(tracker));

        // todo
        mTextView2.setText(tracker.getTitle());
        mTextView3.setText(tracker.getTitle());

        mDifficultyTextView.setText(getCountLabel(tracker));
        mLevelTextView.setText(tracker.getLevelToDisplay());

        int trackerColor = ColorUtils.getTrackerColor(getContext(), tracker);
        GradientDrawable gradientDrawable = ColorUtils.getGradientDrawable(getContext(),trackerColor);

        initIcons(tracker, trackerColor);

        initTrackerGraph(tracker, gradientDrawable);

        initButtons(tracker, trackerInterface);
    }

    private void initButtons(Tracker tracker, TrackerFunctionsInterface trackerInterface) {

        if(tracker.isTimeTracker()){
            initTimeButtons(tracker, trackerInterface);
        }else{
            mButton1.setVisibility(GONE);

            // todo: allow user to define values
            final int button2AddAmount = 1;
            final int button3AddAmount = 5;

            mButton2.setText(getResources().getString(R.string.x_ys,
                    button2AddAmount, tracker.getCounterLabel()));
            mButton3.setText(getResources().getString(R.string.x_ys,
                    button3AddAmount, tracker.getCounterLabel()));

            setListeners(button2AddAmount, button3AddAmount, tracker, trackerInterface);

        }

        // More details Button
        mButton4.setText(R.string.more);
        mButton4.setOnClickListener(view -> trackerInterface.moreDetailsButtonClicked(tracker));

    }

    private void initTimeButtons(Tracker tracker, TrackerFunctionsInterface trackerInterface) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.color_accent, typedValue, true);
        @ColorInt int colorAccent = typedValue.data;
        theme.resolveAttribute(R.attr.theme_text_color, typedValue, true);
        @ColorInt int defaultColor = typedValue.data;

        //Show timer button
        mButton1.setVisibility(View.VISIBLE);

        if(tracker.isCurrentlyTiming()){
            mButton1.setText(R.string.end_timer);
            mButton1.setTextColor(colorAccent);
        }else{
            mButton1.setText(R.string.start_timer);
            mButton1.setTextColor(defaultColor);
        }
        mButton1.setOnClickListener(view -> trackerInterface.timerButtonClicked(tracker));


        // todo: allow user to define values
        final int button2AddAmount = 15;
        final int button3AddAmount = 60;

        if(button2AddAmount > 59){
            mButton2.setText(getResources().getString(R.string.plus_x_hours, (float)button2AddAmount/60));
        }else{
            mButton2.setText(getResources().getString(R.string.plus_x_minutes, button2AddAmount));
        }
        if(button3AddAmount > 59){
            mButton3.setText(getResources().getString(R.string.plus_x_hours, (float)button3AddAmount/60));
        }else{
            mButton3.setText(getResources().getString(R.string.plus_x_minutes, button3AddAmount));
        }

        setListeners(button2AddAmount, button3AddAmount, tracker, trackerInterface);

    }

    private void setListeners(int button2AddAmount, int button3AddAmount,
                              Tracker tracker, TrackerFunctionsInterface trackerInterface) {
        mButton2.setOnClickListener(view -> trackerInterface.addToTrackerScore(tracker, button2AddAmount));
        mButton3.setOnClickListener(view -> trackerInterface.addToTrackerScore(tracker, button3AddAmount));

    }

    private void initTrackerGraph(Tracker tracker, GradientDrawable gradientDrawable) {
        if(tracker.getType() == Tracker.TRACKER_TYPE.LEVEL_UP){
            mProgressBarLayout.setVisibility(VISIBLE);
            mProgressBarFilled.setBackground(gradientDrawable);
            setProgressBarDimen(tracker);
        }else{
            mProgressBarLayout.setVisibility(GONE);
        }

        if (tracker.getType() == Tracker.TRACKER_TYPE.GRAPH) {
            mTimeGraphView.setVisibility(VISIBLE);
            mTimeGraphView.setGraphBarResource(gradientDrawable);

            switch (tracker.getDefaultGraph()) {
                case DAY:
                    mTimeGraphView.setGraphType(TimeGraphView.DAY);
                    mTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
                    break;
                case WEEK:
                    mTimeGraphView.setGraphType(TimeGraphView.WEEK);
                    mTimeGraphView.initGraph(tracker.getPastEightWeeksCounts());
                    break;
                case MONTH:
                    mTimeGraphView.setGraphType(TimeGraphView.MONTH);
                    mTimeGraphView.initGraph(tracker.getPastEightMonthsCounts());
                    break;
                case YEAR:
                    // todo
                    mTimeGraphView.setGraphType(TimeGraphView.DAY);
                    mTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
                    break;
                default:
                    mTimeGraphView.setGraphType(TimeGraphView.DAY);
                    mTimeGraphView.initGraph(tracker.getPastEightDaysCounts());
            }

        }else{
            mTimeGraphView.setVisibility(GONE);
        }

        if(tracker.getType() == Tracker.TRACKER_TYPE.YES_NO){
            mCheckListView.setVisibility(VISIBLE);
        }else{
            mCheckListView.setVisibility(GONE);
        }
    }

    private void initIcons(Tracker tracker, int trackerColor) {

        int nextLevelColor = ColorUtils.getLevelDefinedColor(getContext(),
                tracker.getLevel() + 1);
        int maxLevelColor = ColorUtils.getLevelDefinedColor(getContext(),
                Integer.MAX_VALUE);


        VectorMasterDrawable icon = ColorUtils.getTrackerIcon(getContext(), tracker.getIcon());
        VectorMasterDrawable nextLevelIcon = ColorUtils.getTrackerIcon(getContext(), tracker.getIcon());
        VectorMasterDrawable maxLevelIcon = ColorUtils.getTrackerIcon(getContext(), tracker.getIcon());
        setVectorColor(icon, trackerColor);
        setVectorColor(nextLevelIcon, nextLevelColor);
        setVectorColor(maxLevelIcon, maxLevelColor);


        mTrackerIcon.setImageDrawable(icon);
        mNextLevelIcon.setImageDrawable(nextLevelIcon);
        mMaxLevelIcon.setImageDrawable(maxLevelIcon);


    }


    private void setVectorColor(VectorMasterDrawable mTrackerIcon, int trackerColor) {

        int outlineNo = 1;
        PathModel outline = mTrackerIcon.getPathModelByName("outline" + outlineNo);

        while(outline != null){
            outlineNo += 1;
            outline.setFillColor(trackerColor);
            outline = mTrackerIcon.getPathModelByName("outline" + outlineNo);
        }
    }

    private void setProgressBarDimen(Tracker tracker) {
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


    private String getCountLabel(Tracker tracker) {
        String label;

        if(tracker.isTimeTracker()){
            label = (tracker.getCountSoFar() > 59)
                    ? getResources().getString(R.string.x_hours, ((float) tracker.getCountSoFar()/60))
                    : getResources().getString(R.string.x_minutes, tracker.getCountSoFar());
        }else{
            label = getResources().getString(R.string.x_ys,  tracker.getCountSoFar(), tracker.getCounterLabel());
        }
        return label;
    }


}
