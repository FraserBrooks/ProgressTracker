package com.fraserbrooks.progresstracker.trackers.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.Expandable;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.RecyclableView;
import com.fraserbrooks.progresstracker.customviews.ColorUtils;
import com.fraserbrooks.progresstracker.customviews.UIUtils;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.util.TrackerFunctionsInterface;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;


public class TrackerView extends FrameLayout implements RecyclableView<Tracker>, Expandable {

    private static final String TAG = "TrackerView";

    private TrackerFunctionsInterface mTrackerInterface;
    private String mTrackerId;

    private TrackerViewButtonManager mButtonManager;
    private TrackerViewFigureManager mFigureManager;

    private TextView mTitleTextView, mRightHandTextView, mTextView2, mTextView3,
             mLevelTextView, mTextView4;

    private ImageView mTrackerIcon, mNextLevelIcon;

    private boolean mExpanded;

    public TrackerView(@NonNull Context context){
        this(context, null);
    }

    public TrackerView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackerView(@NonNull Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);
        inflater.inflate(R.layout.frag_trackers_tracker_custom_view_relative, this, true);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(lp);

        // TextViews
        mTitleTextView =  findViewById(R.id.tracker_view_title);
        mRightHandTextView =  findViewById(R.id.tracker_view_right_hand_text);
        mTextView2 =  findViewById(R.id.tracker_view_text_view_2);
        mTextView3 =  findViewById(R.id.tracker_view_text_view_3);
        mTextView4 = findViewById(R.id.tracker_view_text_view_4);
        mLevelTextView =  findViewById(R.id.tracker_view_level_text);

        // Icon Views
        mTrackerIcon =  findViewById(R.id.tracker_view_ico);
        mNextLevelIcon =  findViewById(R.id.tracker_view_next_level_ico);

        mButtonManager = new TrackerViewButtonManager(this, () -> mTrackerInterface);
        mFigureManager = new TrackerViewFigureManager(this);

        // shrink view by default
        shrinkView();
        mExpanded = false;
    }

    @Override
    public boolean isExpanded(){
        return mExpanded;
    }

    @Override
    public void shrinkView() {

        mNextLevelIcon.setVisibility(GONE);

        mTextView4.setVisibility(GONE);
        mTextView2.setVisibility(GONE);
        mTextView3.setVisibility(GONE);

        mButtonManager.hideButtons();

        mExpanded = false;
    }

    @Override
    public void expandView() {

        mNextLevelIcon.setVisibility(VISIBLE);

        mTextView4.setVisibility(VISIBLE);
        mTextView2.setVisibility(VISIBLE);
        mTextView3.setVisibility(VISIBLE);

        mButtonManager.showButtons();

        mExpanded = true;
    }

    public void setTrackerInterface(TrackerFunctionsInterface i){
        mTrackerInterface = i;
    }

    @Override
    public void initWith(Tracker tracker) {

        mTrackerId = tracker.getId();

        Log.d(TAG, "initWith: Tracker with title: " + tracker.getTitle());

        mTitleTextView.setText(tracker.getTitle());
        mRightHandTextView.setText(getCountLabel(tracker));

        // todo
        //mTextView2.setText(getContext().getString(R.string.x_ys, tracker.getScoreSoFar(), tracker.getCounterLabel()));

        // todo
        //mTextView3.setText(tracker.getTitle());

        // todo
        //mTextView4.setText(getCountLabel(tracker));
        mLevelTextView.setText(tracker.getLevelToDisplay());

        VectorMasterDrawable icon = UIUtils.getTrackerIcon(getContext(), tracker);
        mTrackerIcon.setImageDrawable(icon);

        if(tracker.getType() == Tracker.TYPE_LEVEL_UP){

            VectorMasterDrawable nextLevelIcon = UIUtils.getTrackerNextLevelIcon(getContext(), tracker);

            VectorMasterDrawable maxLevelIcon = UIUtils.getMaxLevelDrawable(getContext());
            mNextLevelIcon.setImageDrawable(nextLevelIcon);

            int button_icon_size = getContext().getResources().getDimensionPixelSize(R.dimen.small_icon_height);

            maxLevelIcon.setBounds(0, 0, button_icon_size, button_icon_size);

            mTextView4.setCompoundDrawablesRelative(maxLevelIcon, null, null, null);
        }else{
            mNextLevelIcon.setVisibility(GONE);
            mTextView4.setCompoundDrawablesRelative(null, null, null, null);
        }

        mButtonManager.initButtonsWith(tracker);
        mFigureManager.initFigureWith(tracker);

    }

    @Nullable
    @Override
    public String getIdForAdapter() {
        return mTrackerId;
    }

    private String getCountLabel(Tracker tracker) {
        String label;

        if(tracker.isTimeTracker()){
            label = (tracker.getScoreSoFar() > 119)
                    ? getResources().getString(R.string.x_hours, tracker.getScoreSoFar()/60)
                    : getResources().getString(R.string.x_minutes, tracker.getScoreSoFar());
        }else{
            label = getResources().getString(R.string.x_ys,  tracker.getScoreSoFar(), tracker.getCounterLabel());
        }
        return label;
    }



}
