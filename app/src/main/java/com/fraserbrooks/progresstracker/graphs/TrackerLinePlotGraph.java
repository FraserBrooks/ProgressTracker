package com.fraserbrooks.progresstracker.graphs;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TrackerLinePlotGraph extends LinearLayout{

    private final String TAG = "TrackerLinePlotGraph";

    private Tracker mTracker;

    private LinearLayout mGraphView;

    private LinearLayout mDaysLayout;

    private TextView mGraphTopTextView;


    private float mGraphHeight;
    private int mGraphBarResourceId;


    private int mGraphItemMargin;

    @SuppressWarnings("FieldCanBeLocal")
    private final float GRAPH_TEXT_SIZE_SP = 12f;


    public TrackerLinePlotGraph(Context context){
        super(context);
    }

    public TrackerLinePlotGraph(Context context, Tracker tracker,
                                float graphHeight,
                                int resourceId) {
        super(context);
        mTracker = tracker;
        mGraphHeight = graphHeight;
        mGraphBarResourceId = resourceId;


        this.setOrientation(LinearLayout.VERTICAL);

        initViews();

        getHeights();
    }

    private void initViews(){

        LinearLayout.LayoutParams graphLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        graphLayoutParams.gravity = Gravity.BOTTOM;
        mGraphView = new LinearLayout(getContext());
        this.addView(mGraphView);
        mGraphView.setLayoutParams(graphLayoutParams);
        mGraphView.setOrientation(LinearLayout.HORIZONTAL);
        mGraphView.setBackgroundResource(R.color.appBG);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        mGraphItemMargin = (int) (mGraphItemMargin * scale + 0.5f);

        ViewGroup.LayoutParams barItemParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) mGraphHeight);

        for(int i = 0; i < 35; i++){
            View v = new View(getContext());
            v.setLayoutParams(barItemParams);
            mGraphView.addView(v);
        }

        mGraphTopTextView = new TextView(getContext());
        LinearLayout.LayoutParams textLP = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textLP.gravity = Gravity.TOP;
        mGraphTopTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GRAPH_TEXT_SIZE_SP);
        mGraphTopTextView.setLayoutParams(textLP);


        mDaysLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams daysParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDaysLayout.setLayoutParams(daysParams);
        mDaysLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(mDaysLayout);

        LinearLayout.LayoutParams dayTextViewParam = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        dayTextViewParam.setMarginStart(mGraphItemMargin);
        dayTextViewParam.setMarginEnd(mGraphItemMargin);

        for(int i = 0; i < 7; i++){
            TextView tv = new TextView(getContext());
            tv.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, GRAPH_TEXT_SIZE_SP);
            tv.setLayoutParams(dayTextViewParam);
            mDaysLayout.addView(tv);
        }

        TextView extraView = new TextView(getContext());
        extraView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GRAPH_TEXT_SIZE_SP);
        extraView.setLayoutParams(textLP);
        extraView.setVisibility(INVISIBLE);
        mDaysLayout.addView(extraView);


    }


    private void getHeights(){

        Calendar day = Calendar.getInstance();

        final ArrayList<String> days = new ArrayList<>();
        float[] counts = new float[8];

        float max = 0;
        day.add(Calendar.DAY_OF_YEAR, -7);
        for (int i = 0; i < 8; i++) {

            counts[i] = mTracker.getPastEightDaysCounts()[i];

            days.add(day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1));

            if (counts[i] > max) max = counts[i];

            day.add(Calendar.DAY_OF_YEAR, 1);
        }

        final int[] heights = new int[35];

        for (int i = 0; i < 7; i++) {

            float thisDayHeight = (counts[i + 1] / max) * mGraphHeight;
            float prevDayHeight = (counts[i] / max) * mGraphHeight;

            float nextDayHeight = thisDayHeight;
            if (i < 6) {
                nextDayHeight = (counts[i + 2] / max) * mGraphHeight;
            }

            float backIncrement = 0;
            float forwardIncrement = 0;

            if (thisDayHeight != 0) {
                backIncrement = (prevDayHeight - thisDayHeight) / 5f;
                if (prevDayHeight == 0) backIncrement *= 2;

                forwardIncrement = (nextDayHeight - thisDayHeight) / 5f;
                if (nextDayHeight == 0) forwardIncrement *= 2;
            }


            Log.d(TAG, "run: back i = " + backIncrement);
            Log.d(TAG, "run: forw i = " + forwardIncrement);


            heights[i * 5] = (int) (thisDayHeight + 2 * backIncrement);
            heights[1 + i * 5] = (int) (thisDayHeight + 1 * backIncrement);
            heights[2 + i * 5] = (int) thisDayHeight;
            heights[3 + i * 5] = (int) (thisDayHeight + 1 * forwardIncrement);
            heights[4 + i * 5] = (int) (thisDayHeight + 2 * forwardIncrement);
        }


        for (int i = 0; i < 35; i++) {
            View v = mGraphView.getChildAt(i);
            LinearLayout.LayoutParams newParam = new LayoutParams(0, heights[i], 1);
            newParam.gravity = Gravity.BOTTOM;
            newParam.setMargins(mGraphItemMargin, mGraphItemMargin, mGraphItemMargin, 0);
            v.setBackgroundResource(mGraphBarResourceId);
            v.setLayoutParams(newParam);
        }

        if (max < 99) {
            String textMax = "" + (int) max;
            mGraphTopTextView.setText(textMax);
            mGraphView.addView(mGraphTopTextView);

            TextView tv = (TextView) mDaysLayout.getChildAt(7);
            tv.setText(textMax);
        }

        for (int i = 0; i < 7; i++) {
            TextView tv = (TextView) mDaysLayout.getChildAt(i);
            tv.setText(days.get(i + 1));
        }


    }
}
