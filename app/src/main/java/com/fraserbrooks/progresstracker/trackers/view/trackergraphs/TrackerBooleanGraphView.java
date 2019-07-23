package com.fraserbrooks.progresstracker.trackers.view.trackergraphs;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.customviews.ColorUtils;
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

    }

    public void initGraph(Tracker tracker){

        initCircleDrawables(tracker);

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

        mCircleLayout.removeAllViews();
        for(int i = 0; i < circles; i++){
            addWeightedSpace();
            if(counts[(8-circles) + i] >= 1){
                addFilledCircleDrawable();
            }else{
                addBlankCircle();
            }
        }
        addWeightedSpace();


    }




    private void addFilledCircleDrawable(){
        ImageView iv = getCircleView();
        iv.setImageDrawable(mFilledCircle);
        //iv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        Log.d(TAG, "addFilledCircleDrawable: FILLED");
        mCircleLayout.addView(iv);
    }

    private void addBlankCircle(){
        ImageView iv = getCircleView();
        iv.setImageDrawable(mBlankCircle);
        //iv.setBackgroundColor(getResources().getColor(R.color.colorError));
        Log.d(TAG, "addFilledCircleDrawable: BLANK");
        mCircleLayout.addView(iv);
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

    private void initCircleDrawables(Tracker tracker){

        // todo: format from tracker data  :  ????

        int trackerColor = ColorUtils.getTrackerColor(getContext(), tracker);

        ColorUtils.setBlankCircle(getContext(), mBlankCircle);
        ColorUtils.setColoredCircle(getContext(), mFilledCircle, trackerColor);


    }




}


