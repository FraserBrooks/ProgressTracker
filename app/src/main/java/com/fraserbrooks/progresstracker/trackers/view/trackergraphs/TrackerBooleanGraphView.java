package com.fraserbrooks.progresstracker.trackers.view.trackergraphs;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.UIUtils;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

public class TrackerBooleanGraphView extends FrameLayout {

    private static final String TAG = "BooleanTrackerGraph";

    // graph formats
    public static final int FORMAT_1 = 0;
    public static final int FORMAT_2 = 1;
    public static final int FORMAT_3 = 2;
    public static final int FORMAT_4 = 3;


    // todo: move to tracker
    // graph types
    public static final int DAY = 1;
    public static final int WEEK = 2;
    public static final int MONTH = 3;

    private LinearLayout mCircleLayout;
    private VectorMasterDrawable mFilledCircle;
    private VectorMasterDrawable mBlankCircle;

    private ImageView mCircle1, mCircle2, mCircle3, mCircle4, mCircle5, mCircle6, mCircle7;

    public TrackerBooleanGraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrackerBooleanGraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lp);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);
        inflater.inflate(R.layout.frag_trackers_boolean_graph, this);

        mCircleLayout = getRootView().findViewById(R.id.boolean_graph_circle_layout);
        mFilledCircle = new VectorMasterDrawable(getContext(), R.drawable.ico_nested_circles);
        mBlankCircle = new VectorMasterDrawable(getContext(), R.drawable.ico_nested_circles);

        mCircle1 = findViewById(R.id.boolean_graph_circle_1);
        mCircle2 = findViewById(R.id.boolean_graph_circle_2);
        mCircle3 = findViewById(R.id.boolean_graph_circle_3);
        mCircle4 = findViewById(R.id.boolean_graph_circle_4);
        mCircle5 = findViewById(R.id.boolean_graph_circle_5);
        mCircle6 = findViewById(R.id.boolean_graph_circle_6);
        mCircle7 = findViewById(R.id.boolean_graph_circle_7);


    }

    public void initGraph(Tracker tracker){

        mFilledCircle = UIUtils.getTrackerFilledRadioButtonDrawable(getContext(), tracker);
        mBlankCircle = UIUtils.getTrackerEmptyRadioButtonDrawable(getContext());

        int graphSize = getResources().getDimensionPixelSize(R.dimen.graph_height_small);

        mFilledCircle.setBounds(graphSize, graphSize, graphSize, graphSize);
        mBlankCircle.setBounds(graphSize, graphSize, graphSize, graphSize);


        int circles;
        int[] counts;

        switch (tracker.getGraphType()) {

            case DAY:
                circles = 7;
                counts = tracker.getPastEightDaysCounts();
                break;
            case WEEK:
                circles = 6;
                counts = tracker.getPastEightWeeksCounts();
                break;
            case MONTH:
                circles = 5;
                counts = tracker.getPastEightMonthsCounts();
                break;
            default:
                circles = 7;
                counts = tracker.getPastEightDaysCounts();

        }

        int startI = 1;
        if(circles == 7){
            mCircle6.setVisibility(VISIBLE);
            mCircle7.setVisibility(VISIBLE);
        }else if(circles == 6){
            mCircle6.setVisibility(VISIBLE);
            mCircle7.setVisibility(GONE);
            startI = 2;
        }else {
            mCircle6.setVisibility(GONE);
            mCircle7.setVisibility(GONE);
            startI = 3;
        }

        if(counts[startI] >= 1){
            mCircle1.setImageDrawable(mFilledCircle);
        }else{
            mCircle1.setImageDrawable(mBlankCircle);
        }

        if(counts[startI + 1] >= 1){
            mCircle2.setImageDrawable(mFilledCircle);
        }else{
            mCircle2.setImageDrawable(mBlankCircle);
        }

        if(counts[startI + 2] >= 1){
            mCircle3.setImageDrawable(mFilledCircle);
        }else{
            mCircle3.setImageDrawable(mBlankCircle);
        }

        if(counts[startI + 3] >= 1){
            mCircle4.setImageDrawable(mFilledCircle);
        }else{
            mCircle4.setImageDrawable(mBlankCircle);
        }

        if(counts[startI + 4] >= 1){
            mCircle5.setImageDrawable(mFilledCircle);
        }else{
            mCircle5.setImageDrawable(mBlankCircle);
        }
        if(circles == 5) return;

        if(counts[startI + 5] >= 1){
            mCircle6.setImageDrawable(mFilledCircle);
        }else{
            mCircle6.setImageDrawable(mBlankCircle);
        }
        if(circles == 6) return;

        if(counts[startI + 6] >= 1){
            mCircle7.setImageDrawable(mFilledCircle);
        }else{
            mCircle7.setImageDrawable(mBlankCircle);
        }

    }


    @NonNull
    private ImageView getCircleView() {
        ImageView iv = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.graph_height_small),
                getResources().getDimensionPixelSize(R.dimen.graph_height_small)
        );
        iv.setLayoutParams(lp);
        return iv;
    }

    private void addWeightedSpace(){
        View v = new View(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, 0,1);
        v.setLayoutParams(lp);
        mCircleLayout.addView(v);
    }





}


