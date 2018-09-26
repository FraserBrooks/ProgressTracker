package com.fraserbrooks.progresstracker.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("unused")
public class TimeGraphView extends FrameLayout {

    private static final String TAG = "TimeGraphView";

    public static final int DAY = Calendar.DAY_OF_YEAR;
    public static final int WEEK = Calendar.WEEK_OF_YEAR;
    public static final int MONTH = Calendar.MONTH;

    public static final int ONE_BAR = 0;
    public static final int THREE_BAR = 1;
    public static final int FIVE_BAR = 2;
    public static final int NINE_BAR = 3;

    private int mGraphWindowHeight;
    private int mGraphBarMargin;

    private int mGraphWindowColor;
    private int mGraphAxisColor;
    private int mGraphFooterColor;
    private int mTimeLabelTextColor;
    private int mAxisTextColor;

    private boolean mShowIntervalLabel;
    private boolean mShowTimeLabels;
    private boolean mShowGraphAxis;
    private boolean mShowHighestValue;

    private Drawable mPointerDrawable;
    private Drawable mGraphBarResource;

    private int mGraphType;
    private int mGraphFormat;

    private LinearLayout mGraphWindow, mTimeLabelLayout, mGraphAxis;

    private String[] mOrdinalDates;

    private Calendar mCurrentDate;

    public TimeGraphView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public TimeGraphView(@NonNull Context context,AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }


    public void initGraph(int[] counts){

        int max = Integer.MIN_VALUE;

        for (int i : counts)  max = (max > i) ? max : i;

        initAttributes();
        initGraphWindow(counts, max, mGraphWindowHeight);
        initGraphAxis(max);
        initHighestValueTextView(max);

    }

    private void init(AttributeSet attrs){

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);
        inflater.inflate(R.layout.shared_ui_time_graph, this);

        mGraphWindow = getRootView().findViewById(R.id.graph_window);
        mTimeLabelLayout = getRootView().findViewById(R.id.time_labels_layout);
        mGraphAxis = getRootView().findViewById(R.id.graph_axis);

        mOrdinalDates = getResources().getStringArray(R.array.ordinal_dates);

        mCurrentDate = Calendar.getInstance();

        setAttributes(attrs);
    }

    /**
     * This method set xml values for graph elements
     *
     * @param attrs A set of xml attributes
     */
    private void setAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimeGraphView);

        try {
            initGraphSettings(typedArray);
            initAttributes();
        } finally {
            typedArray.recycle();
        }
    }


    private void initGraphSettings(TypedArray typedArray) {

        mGraphWindowHeight = typedArray.getDimensionPixelSize(R.styleable.TimeGraphView_graph_window_height, 50);
        mGraphBarMargin = typedArray.getDimensionPixelSize(R.styleable.TimeGraphView_graph_bar_margin, 0);

        mGraphWindowColor = typedArray.getColor(R.styleable.TimeGraphView_graph_window_background_color, 0);
        mGraphAxisColor = typedArray.getColor(R.styleable.TimeGraphView_graph_axis_background_color, 0);
        mGraphFooterColor = typedArray.getColor(R.styleable.TimeGraphView_graph_footer_background_color, 0);
        mTimeLabelTextColor = typedArray.getColor(R.styleable.TimeGraphView_time_label_text_color, 0);
        mAxisTextColor = typedArray.getColor(R.styleable.TimeGraphView_axis_text_color, 0);

        mShowIntervalLabel = typedArray.getBoolean(R.styleable.TimeGraphView_show_interval_label, true);
        mShowTimeLabels = typedArray.getBoolean(R.styleable.TimeGraphView_show_time_labels, true);
        mShowGraphAxis = typedArray.getBoolean(R.styleable.TimeGraphView_show_graph_axis, true);
        mShowHighestValue = typedArray.getBoolean(R.styleable.TimeGraphView_show_highest_value_label, true);

        mPointerDrawable = typedArray.getDrawable(R.styleable.TimeGraphView_pointer_resource);
        mGraphBarResource = typedArray.getDrawable(R.styleable.TimeGraphView_pointer_resource);

        int i = typedArray.getInt(R.styleable.TimeGraphView_graph_type, DAY);
        switch (i) {
            case 0:
                mGraphType = DAY;
                break;
            case 1:
                mGraphType = WEEK;
                break;
            case 2:
                mGraphType = MONTH;
                break;
            default:
                mGraphType = DAY;
        }
        mGraphFormat = typedArray.getInt(R.styleable.TimeGraphView_bars_per_interval, FIVE_BAR);

    }

    private void initAttributes() {

        if (mGraphWindowColor != 0){
            mGraphWindow.setBackgroundColor(mGraphWindowColor);
        }else{
            mGraphWindow.setBackgroundColor(Color.TRANSPARENT);
        }

        View graphFooter = getRootView().findViewById(R.id.time_graph_footer_row);
        if(mGraphFooterColor != 0){
            graphFooter.setBackgroundColor(mGraphFooterColor);
        }else{
            graphFooter.setBackgroundColor(Color.TRANSPARENT);
        }

        initTimeLabelsLayout();
        initIntervalLabel();

    }


    private void initGraphWindow(int[] counts, float highestValue, float graphHeight){

        mGraphWindow.removeAllViews();

        int barsPerInterval = 1;
        switch (mGraphFormat) {

            case ONE_BAR:
                barsPerInterval = 1;
                break;
            case THREE_BAR:
                barsPerInterval = 3;
                break;
            case FIVE_BAR:
                barsPerInterval = 5;
                break;
            case NINE_BAR:
                barsPerInterval = 9;
                break;
            default:
                break;
        }

        int intervals;

        switch (mGraphType) {
            case DAY:
                intervals = 7;
                break;
            case WEEK:
                intervals = 5;
                break;
            case MONTH:
                intervals = 6;
                break;
            default:
                throw new IllegalArgumentException();
        }

        int totalBars = intervals * barsPerInterval;

        int[] heights = new int[totalBars];

        int lastIndex = counts.length - 1;
        int firstIndex = lastIndex - intervals;

        int q = 0;

        for(int i = firstIndex; i < intervals; i++){



            float previousIntervalCount = counts[i];
            float thisIntervalCount = counts[i+1];
            float nextIntervalCount =
                    (i+2 >= intervals) ? thisIntervalCount : counts[i+2];


            float previousIntervalHeight = (previousIntervalCount/ highestValue )   * graphHeight;
            float thisIntervalHeight = (thisIntervalCount/ highestValue)            * graphHeight;
            float nextIntervalHeight = (nextIntervalCount/ highestValue)            * graphHeight;

            float backIncrement = 0;
            float forwardIncrement = 0;

            if(thisIntervalHeight != 0){
                backIncrement = (previousIntervalHeight - thisIntervalHeight) /
                        ((float) barsPerInterval);
                forwardIncrement = (nextIntervalHeight - thisIntervalHeight) /
                        ((float) barsPerInterval);

                if(previousIntervalHeight == 0) backIncrement *= 2f;
                if(nextIntervalHeight == 0) forwardIncrement *= 2f;
            }

            int baseIndex = i * barsPerInterval;
            int middleBarNum = barsPerInterval/2 +1;
            int midIndex = baseIndex + middleBarNum -1;

            heights[midIndex] = (int) thisIntervalHeight;

            for(int k = 1; k < middleBarNum; k++){
                heights[midIndex-k] = (int) (thisIntervalHeight  + (((float)k) * backIncrement));
                heights[midIndex+k] = (int) (thisIntervalHeight  + (((float)k) * forwardIncrement));

            }


        }

        Log.d(TAG, "initGraphWindow: heights len = " + heights.length);

        for (int height : heights) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1);
            lp.gravity = Gravity.BOTTOM;
            lp.setMargins(mGraphBarMargin, mGraphBarMargin, mGraphBarMargin, 0);
            View v = new View(getContext());
            v.setBackground(mGraphBarResource);
            if (height == 0) {
                lp.height = 15;
                v.setBackground(null);
            }
            v.setLayoutParams(lp);
            mGraphWindow.addView(v);
        }

    }

    private void initGraphAxis(float highestValue) {

        if (!mShowGraphAxis) {
            mGraphAxis.setVisibility(View.GONE);
            return;
        } else {
            mGraphAxis.setVisibility(View.VISIBLE);
            if(mGraphAxisColor != 0){
                mGraphAxis.setBackgroundColor(mGraphAxisColor);
            }
        }

        TextView topAxisTv = getRootView().findViewById(R.id.time_graph_axis_tv_1);
        TextView axis2Tv = getRootView().findViewById(R.id.time_graph_axis_tv_2);
        TextView axis3Tv = getRootView().findViewById(R.id.time_graph_axis_tv_3);
        TextView bottomAxisTv = getRootView().findViewById(R.id.time_graph_axis_tv_4);


        float value2 = (highestValue / 4f) * 3f;
        float value3 = (highestValue / 2f);
        float value4 = (highestValue / 4f);


        if (highestValue > 999_999) {
            Log.d(TAG, "initGraphAxis: high > 1mil");
            setMillionAxis(topAxisTv, highestValue);
            setMillionAxis(axis2Tv, value2);
            setMillionAxis(axis3Tv, value3);
            setMillionAxis(bottomAxisTv, value4);

        } else if (highestValue > 1000) {
            Log.d(TAG, "initGraphAxis: high > 1000");
            setThousandAxis(topAxisTv, highestValue);
            setThousandAxis(axis2Tv, value2);
            setThousandAxis(axis3Tv, value3);
            setThousandAxis(bottomAxisTv, value4);

        } else {
            Log.d(TAG, "initGraphAxis: high < 1k");
            setAxis(topAxisTv, highestValue);
            setAxis(axis2Tv, value2);
            setAxis(axis3Tv, value3);
            setAxis(bottomAxisTv, value4);
        }

        topAxisTv.setBackground(mPointerDrawable);
        axis2Tv.setBackground(mPointerDrawable);
        axis3Tv.setBackground(mPointerDrawable);
        bottomAxisTv.setBackground(mPointerDrawable);

        if (mAxisTextColor != 0) {
            topAxisTv.setTextColor(mAxisTextColor);
            axis2Tv.setTextColor(mAxisTextColor);
            axis3Tv.setTextColor(mAxisTextColor);
            bottomAxisTv.setTextColor(mAxisTextColor);
        } else if (mTimeLabelTextColor != 0) {
            topAxisTv.setTextColor(mTimeLabelTextColor);
            axis2Tv.setTextColor(mTimeLabelTextColor);
            axis3Tv.setTextColor(mTimeLabelTextColor);
            bottomAxisTv.setTextColor(mTimeLabelTextColor);
        }

    }

    private void setAxis(TextView tv, float value){
        Log.d(TAG, "setAxis: printV:" + getStringForAxis(value));
        tv.setText(getStringForAxis(value));
    }

    private void setThousandAxis(TextView tv, float value){
        value /= 1000f;
        Log.d(TAG, "setThousandAxis: printV: " + getStringForAxis(value));
        tv.setText(getResources().getString(R.string.s_thousand_short, getStringForAxis(value)));
    }

    private void setMillionAxis(TextView tv, float value) {
        value /= 1_000_000f;
        Log.d(TAG, "setMillionAxis: printV: " + getStringForAxis(value));
        tv.setText(getResources().getString(R.string.s_million_short, getStringForAxis(value)));


    }

    private String getStringForAxis(float num){

        String twoSF = String.valueOf(num);

        if(twoSF.substring(0,1).equals("0")){
            twoSF = twoSF.substring(1);
        }

        if(twoSF.length() >= 3) twoSF = twoSF.substring(0,3);


        while(twoSF.matches(".*\\.0*")){
            twoSF = twoSF.substring(0,twoSF.length()-1);
        }

        if(twoSF.isEmpty()) return "0";
        return twoSF;
    }

    private void initHighestValueTextView(int highestValue) {

        TextView highestValueLabel = getRootView().findViewById(R.id.time_graph_max_value_tv);


        if (!mShowHighestValue) {
            highestValueLabel.setVisibility(View.GONE);
            return;
        } else {
            highestValueLabel.setVisibility(View.VISIBLE);
        }

        highestValueLabel.setText(String.valueOf(highestValue));

        if (mAxisTextColor != 0) {
            highestValueLabel.setTextColor(mAxisTextColor);
        } else if (mTimeLabelTextColor != 0) {
            highestValueLabel.setTextColor(mTimeLabelTextColor);
        }

    }

    private void initIntervalLabel() {

        TextView intervalLabel = getRootView().findViewById(R.id.time_graph_interval_tv);

        if (!mShowIntervalLabel) {
            intervalLabel.setVisibility(View.GONE);
            return;
        } else {
            intervalLabel.setVisibility(View.VISIBLE);
        }

        if (mTimeLabelTextColor != 0) {
            intervalLabel.setTextColor(mTimeLabelTextColor);
        }

        switch (mGraphType) {
            case DAY:
                intervalLabel.setText(getResources().getString(R.string.day_colon));
                break;
            case WEEK:
                intervalLabel.setText(getResources().getString(R.string.week_colon));
                break;
            case MONTH:
                intervalLabel.setText(getResources().getString(R.string.month_colon));
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    private void initTimeLabelsLayout() {

        mTimeLabelLayout.removeAllViews();

        if (!mShowTimeLabels) {
            mTimeLabelLayout.setVisibility(View.GONE);
            return;
        } else {
            mTimeLabelLayout.setVisibility(VISIBLE);
        }

        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(0, 0, 1);

        for (int i = 6; i >= 0; i--) {

            View leftSpace = new View(getContext());
            leftSpace.setLayoutParams(spaceParams);

            TextView tv = new TextView(getContext());
            initTextView(tv, getTimeLabelText(i), mTimeLabelTextColor);

            View rightSpace = new View(getContext());
            rightSpace.setLayoutParams(spaceParams);

            mTimeLabelLayout.addView(leftSpace);
            mTimeLabelLayout.addView(tv);
            mTimeLabelLayout.addView(rightSpace);
        }

    }

    private String getTimeLabelText(int jumpBack) {

        mCurrentDate.add(mGraphType, -jumpBack);
        String returnValue;


        if (mGraphType == WEEK) {

            int i = 0;
            while (mCurrentDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                i += 1;
                mCurrentDate.add(Calendar.DAY_OF_YEAR, -1);
            }
            //  16th/23rd/...
            returnValue = mOrdinalDates[mCurrentDate.get(Calendar.DAY_OF_MONTH) - 1];
            mCurrentDate.add(Calendar.DAY_OF_YEAR, i);

        } else if (mGraphType == DAY) {

            // M/T/W/T/F/S/S
            returnValue = mCurrentDate.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.SHORT, Locale.getDefault()).substring(0, 1);

        } else if (mGraphType == MONTH) {

            // Sep/Oct/Nov/Dec/Jan...
            returnValue = mCurrentDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        } else {
            throw new IllegalArgumentException();
        }

        mCurrentDate.add(Calendar.DAY_OF_YEAR, jumpBack);
        return returnValue;
    }

    private void initTextView(TextView tv, String text, int textColor) {

        ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tv.setText(text);
        tv.setLayoutParams(textViewParams);
        if (textColor != 0) tv.setTextColor(textColor);


    }


    public int getGraphWindowHeight() {
        return mGraphWindowHeight;
    }

    public void setGraphWindowHeight(int mGraphWindowHeight) {
        this.mGraphWindowHeight = mGraphWindowHeight;
    }

    public int getGraphBarMargin() {
        return mGraphBarMargin;
    }

    public void setGraphBarMargin(int mGraphBarMargin) {
        this.mGraphBarMargin = mGraphBarMargin;
    }


    public void setGraphWindowColor(int mGraphWindowColor) {
        this.mGraphWindowColor = mGraphWindowColor;
    }

    public void setGraphFrameColor(int mGraphFrameColor) {
        this.mGraphFooterColor = mGraphFrameColor;
    }

    public void setTimeLabelTextColor(int mTimeLabelTextColor) {
        this.mTimeLabelTextColor = mTimeLabelTextColor;
    }

    public void setNumberTextColor(int mNumberTextColor) {
        this.mAxisTextColor = mNumberTextColor;
    }

    public void setShowIntervalLabel(boolean mShowIntervalLabel) {
        this.mShowIntervalLabel = mShowIntervalLabel;
    }

    public void setShowTimeLabels(boolean mShowTimeLabels) {
        this.mShowTimeLabels = mShowTimeLabels;
    }


    public void setShowGraphAxis(boolean mShowGraphAxis) {
        this.mShowGraphAxis = mShowGraphAxis;
    }

    public void setShowHighestValue(boolean mShowHighestValue) {
        this.mShowHighestValue = mShowHighestValue;
    }

    public void setPointerDrawable(Drawable mPointerDrawable) {
        this.mPointerDrawable = mPointerDrawable;
    }

    public void setGraphBarResource(Drawable mGraphBarResource) {
        this.mGraphBarResource = mGraphBarResource;
    }

    public int getGraphType() {
        return mGraphType;
    }

    public void setGraphType(int mGraphType) {
        this.mGraphType = mGraphType;
    }

    public void setGraphFormat(int mGraphFormat) {
        this.mGraphFormat = mGraphFormat;
    }

    public Calendar getCurrentDate() {
        return mCurrentDate;
    }

    public void setCurrentDate(Calendar mCalendar) {
        this.mCurrentDate = mCalendar;
    }

    public int getGraphAxisColor() {
        return mGraphAxisColor;
    }

    public void setGraphAxisColor(int graphAxisColor) {
        this.mGraphAxisColor = graphAxisColor;
    }
}
