package com.fraserbrooks.progresstracker.trackers.view.trackergraphs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("unused")
public class TrackerTimeGraphView extends FrameLayout {

    private static final String TAG = "TrackerTimeGraph";

    // graph types
    public static final int DAY = Calendar.DAY_OF_YEAR;
    public static final int WEEK = Calendar.WEEK_OF_YEAR;
    public static final int MONTH = Calendar.MONTH;

    // graph formats
    public static final int ONE_BAR = 0;
    public static final int THREE_BAR = 1;
    public static final int FIVE_BAR = 2;
    public static final int NINE_BAR = 3;

    private int mGraphWindowHeight;
    private int mGraphBarMargin;

    private int mGraphWindowColor, mGraphBackgroundColour,
            mTimeLabelTextColor, mAxisTextColor;

    private boolean mShowIntervalLabel, mShowTimeLabels, mShowGraphAxis, mShowHighestValue;

    private Drawable mPointerDrawable, mGraphBarResource;

    private int mGraphType, mGraphFormat;

    private LinearLayout mGraphWindow;

    private View xAxisSpacer10, xAxisSpacer11, xAxisSpacer12, xAxisSpacer13,
                    graphBox1, graphBox2, graphBox3, graphBox4, graphBox5, graphBox6, graphBox7;

    private TextView xAxis1, xAxis2, xAxis3, xAxis4, xAxis5, xAxis6, xAxis7;

    private String[] mOrdinalDates;

    private Calendar mCurrentDate;


    public TrackerTimeGraphView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }



    public void initGraph(int[] counts){

        int max = Integer.MIN_VALUE;

        for( int i : counts){
            if(i > max){
                max = i;
            }
        }
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


        xAxis1 = getRootView().findViewById(R.id.time_graph_x_axis_1);
        xAxis2 = getRootView().findViewById(R.id.time_graph_x_axis_2);
        xAxis3 = getRootView().findViewById(R.id.time_graph_x_axis_3);
        xAxis4 = getRootView().findViewById(R.id.time_graph_x_axis_4);
        xAxis5 = getRootView().findViewById(R.id.time_graph_x_axis_5);
        xAxis6 = getRootView().findViewById(R.id.time_graph_x_axis_6);
        xAxis7 = getRootView().findViewById(R.id.time_graph_x_axis_7);

        xAxisSpacer10 = getRootView().findViewById(R.id.time_graph_spacer_10);
        xAxisSpacer11 = getRootView().findViewById(R.id.time_graph_spacer_11);
        xAxisSpacer12 = getRootView().findViewById(R.id.time_graph_spacer_12);
        xAxisSpacer13 = getRootView().findViewById(R.id.time_graph_spacer_13);

        graphBox1 = getRootView().findViewById(R.id.graph_box_1);
        graphBox2 = getRootView().findViewById(R.id.graph_box_2);
        graphBox3 = getRootView().findViewById(R.id.graph_box_3);
        graphBox4 = getRootView().findViewById(R.id.graph_box_4);
        graphBox5 = getRootView().findViewById(R.id.graph_box_5);
        graphBox6 = getRootView().findViewById(R.id.graph_box_6);
        graphBox7 = getRootView().findViewById(R.id.graph_box_7);


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
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TrackerTimeGraphView);

        try {
            initGraphSettings(typedArray);
            initAttributes();
        } finally {
            typedArray.recycle();
        }
    }


    private void initGraphSettings(TypedArray typedArray) {

        mGraphWindowHeight = typedArray.getDimensionPixelSize(R.styleable.TrackerTimeGraphView_graph_window_height, 50);
        mGraphBarMargin = typedArray.getDimensionPixelSize(R.styleable.TrackerTimeGraphView_graph_bar_margin, 0);

        mGraphWindowColor = typedArray.getColor(R.styleable.TrackerTimeGraphView_graph_window_background_color, 0);
        mGraphBackgroundColour = typedArray.getColor(R.styleable.TrackerTimeGraphView_graph_background_color, 0);
        mTimeLabelTextColor = typedArray.getColor(R.styleable.TrackerTimeGraphView_time_label_text_color, 0);
        mAxisTextColor = typedArray.getColor(R.styleable.TrackerTimeGraphView_axis_text_color, 0);

        mShowIntervalLabel = typedArray.getBoolean(R.styleable.TrackerTimeGraphView_show_interval_label, true);
        mShowTimeLabels = typedArray.getBoolean(R.styleable.TrackerTimeGraphView_show_time_labels, true);
        mShowGraphAxis = typedArray.getBoolean(R.styleable.TrackerTimeGraphView_show_graph_axis, true);
        mShowHighestValue = typedArray.getBoolean(R.styleable.TrackerTimeGraphView_show_highest_value_label, true);

        mPointerDrawable = typedArray.getDrawable(R.styleable.TrackerTimeGraphView_pointer_resource);
        mGraphBarResource = typedArray.getDrawable(R.styleable.TrackerTimeGraphView_graph_bar_resource);

        int i = typedArray.getInt(R.styleable.TrackerTimeGraphView_graph_type, 0);
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
        mGraphFormat = typedArray.getInt(R.styleable.TrackerTimeGraphView_bars_per_interval, FIVE_BAR);

    }

    private void initAttributes() {

        if (mGraphWindowColor != 0){
            mGraphWindow.setBackgroundColor(mGraphWindowColor);
        }else{
            mGraphWindow.setBackgroundColor(Color.TRANSPARENT);
        }

        if (mGraphBackgroundColour != 0){
            this.setBackgroundColor(mGraphBackgroundColour);
        }else{
            this.setBackgroundColor(Color.TRANSPARENT);
        }

        initTimeLabelsLayout();
        initIntervalLabel();

    }


    private void initGraphWindow(int[] counts, float highestValue, float graphHeight){

        TableRow.LayoutParams windowParams = (TableRow.LayoutParams) mGraphWindow.getLayoutParams();

        windowParams.height = (int) graphHeight;

        mGraphWindow.setLayoutParams(windowParams);

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
        
        for(int i = firstIndex; i < lastIndex; i++){



            float previousIntervalCount = counts[i];
            float thisIntervalCount = counts[i+1];
            float nextIntervalCount = (i+1 == lastIndex) ? counts[i+1] : counts[i+2];


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

            int baseIndex = ((i - firstIndex) * barsPerInterval);
            int middleBarNum = barsPerInterval/2 +1;
            int midIndex = baseIndex + middleBarNum -1;

            heights[midIndex] = (int) thisIntervalHeight;

            for(int k = 1; k < middleBarNum; k++){
                heights[midIndex-k] = (int) (thisIntervalHeight  + (((float)k) * backIncrement));
                heights[midIndex+k] = (int) (thisIntervalHeight  + (((float)k) * forwardIncrement));

            }
            
        }

        Log.d(TAG, "initGraphWindow: heights len = " + heights.length);

        if (mGraphType == DAY){

            for(int height : heights){

                Log.d(TAG, "initGraphWindow: werty werty ::::::::" + height);
            }
            
        }


        graphBox1.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox2.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox3.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox4.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox5.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox6.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());
        graphBox7.setBackground(mGraphBarResource.getConstantState().newDrawable().mutate());


        graphBox1.getLayoutParams().height = heights[0];
        graphBox2.getLayoutParams().height = heights[1];
        graphBox3.getLayoutParams().height = heights[2];
        graphBox4.getLayoutParams().height = heights[3];
        graphBox5.getLayoutParams().height = heights[4];

        if(heights.length > 5){

            graphBox6.getLayoutParams().height = heights[5];
            graphBox6.setVisibility(View.VISIBLE);
        }else{
            graphBox6.setVisibility(View.GONE);
        }

        if(heights.length > 6){
            graphBox7.getLayoutParams().height = heights[6];
            graphBox7.setVisibility(View.VISIBLE);
        }else{
            graphBox7.setVisibility(View.GONE);
        }

        /*

        for (int i = 0; i < heights.length ; i++) {

            View v = new View(getContext());
            v.setBackground(mGraphBarResource);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0, 0);

            lp.weight = 1;
            lp.height = heights[i];
            lp.width = 0;

            v.setLayoutParams(lp);

            mGraphWindow.addView(v);
            mGraphWindow.invalidate();

        }
        */

    }

    private void initGraphAxis(float highestValue) {


        TextView yAxis1 = this.findViewById(R.id.time_graph_axis_tv_1);
        TextView yAxis2 = this.findViewById(R.id.time_graph_axis_tv_2);
        TextView yAxis3 = this.findViewById(R.id.time_graph_axis_tv_3);
        TextView yAxis4 = this.findViewById(R.id.time_graph_axis_tv_4);

        if(!mShowGraphAxis){
            yAxis1.setVisibility(GONE);
            yAxis2.setVisibility(GONE);
            yAxis3.setVisibility(GONE);
            yAxis4.setVisibility(GONE);
            return;
        }else{
            yAxis1.setVisibility(VISIBLE);
            yAxis2.setVisibility(VISIBLE);
            yAxis3.setVisibility(VISIBLE);
            yAxis4.setVisibility(VISIBLE);
        }

        float value2 = (highestValue / 4f) * 3f;
        float value3 = (highestValue / 2f);
        float value4 = (highestValue / 4f);


        if (highestValue > 999_999) {
            Log.d(TAG, "initGraphAxis: high > 1mil");
            setMillionAxis(yAxis1, highestValue);
            setMillionAxis(yAxis2, value2);
            setMillionAxis(yAxis3, value3);
            setMillionAxis(yAxis4, value4);

        } else if (highestValue > 1000) {
            Log.d(TAG, "initGraphAxis: high > 1000");
            setThousandAxis(yAxis1, highestValue);
            setThousandAxis(yAxis2, value2);
            setThousandAxis(yAxis3, value3);
            setThousandAxis(yAxis4, value4);

        } else {
            Log.d(TAG, "initGraphAxis: high < 1k");
            setAxis(yAxis1, highestValue);
            setAxis(yAxis2, value2);
            setAxis(yAxis3, value3);
            setAxis(yAxis4, value4);
        }

        yAxis1.setBackground(mPointerDrawable);
        yAxis2.setBackground(mPointerDrawable);
        yAxis3.setBackground(mPointerDrawable);
        yAxis4.setBackground(mPointerDrawable);

        if (mAxisTextColor != 0) {
            yAxis1.setTextColor(mAxisTextColor);
            yAxis2.setTextColor(mAxisTextColor);
            yAxis3.setTextColor(mAxisTextColor);
            yAxis4.setTextColor(mAxisTextColor);
        } else if (mTimeLabelTextColor != 0) {
            yAxis1.setTextColor(mTimeLabelTextColor);
            yAxis2.setTextColor(mTimeLabelTextColor);
            yAxis3.setTextColor(mTimeLabelTextColor);
            yAxis4.setTextColor(mTimeLabelTextColor);
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

        TextView highestValueLabel = this.findViewById(R.id.time_graph_max_value_tv);


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

        TextView intervalLabel = this.findViewById(R.id.time_graph_interval_tv);

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



        if (!mShowTimeLabels) {
            xAxis1.setVisibility(GONE);
            xAxis2.setVisibility(GONE);
            xAxis3.setVisibility(GONE);
            xAxis4.setVisibility(GONE);
            xAxis5.setVisibility(GONE);
            xAxis6.setVisibility(GONE);
            xAxis7.setVisibility(GONE);
            return;
        } else {
            xAxis1.setVisibility(VISIBLE);
            xAxis2.setVisibility(VISIBLE);
            xAxis3.setVisibility(VISIBLE);
            xAxis4.setVisibility(VISIBLE);
            xAxis5.setVisibility(VISIBLE);
            xAxis6.setVisibility(VISIBLE);
            xAxis7.setVisibility(VISIBLE);
        }

        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(0, 0, 1);

        switch (mGraphType) {
            case DAY:

                initTextView(xAxis1, getTimeLabelText(6), mTimeLabelTextColor);
                initTextView(xAxis2, getTimeLabelText(5), mTimeLabelTextColor);
                initTextView(xAxis3, getTimeLabelText(4), mTimeLabelTextColor);
                initTextView(xAxis4, getTimeLabelText(3), mTimeLabelTextColor);
                initTextView(xAxis5, getTimeLabelText(2), mTimeLabelTextColor);
                initTextView(xAxis6, getTimeLabelText(1), mTimeLabelTextColor);
                initTextView(xAxis7, getTimeLabelText(0), mTimeLabelTextColor);

                xAxisSpacer10.setVisibility(VISIBLE);
                xAxisSpacer11.setVisibility(VISIBLE);
                xAxisSpacer12.setVisibility(VISIBLE);
                xAxisSpacer13.setVisibility(VISIBLE);

                break;
            case WEEK:

                initTextView(xAxis1, getTimeLabelText(4), mTimeLabelTextColor);
                initTextView(xAxis2, getTimeLabelText(3), mTimeLabelTextColor);
                initTextView(xAxis3, getTimeLabelText(2), mTimeLabelTextColor);
                initTextView(xAxis4, getTimeLabelText(1), mTimeLabelTextColor);
                initTextView(xAxis5, getTimeLabelText(0), mTimeLabelTextColor);

                xAxis6.setVisibility(GONE);
                xAxis7.setVisibility(GONE);
                xAxisSpacer10.setVisibility(GONE);
                xAxisSpacer11.setVisibility(GONE);
                xAxisSpacer12.setVisibility(GONE);
                xAxisSpacer13.setVisibility(GONE);

                break;
            case MONTH:

                initTextView(xAxis1, getTimeLabelText(5), mTimeLabelTextColor);
                initTextView(xAxis2, getTimeLabelText(4), mTimeLabelTextColor);
                initTextView(xAxis3, getTimeLabelText(3), mTimeLabelTextColor);
                initTextView(xAxis4, getTimeLabelText(2), mTimeLabelTextColor);
                initTextView(xAxis5, getTimeLabelText(1), mTimeLabelTextColor);
                initTextView(xAxis6, getTimeLabelText(0), mTimeLabelTextColor);

                xAxis7.setVisibility(GONE);
                xAxisSpacer10.setVisibility(VISIBLE);
                xAxisSpacer11.setVisibility(VISIBLE);
                xAxisSpacer12.setVisibility(GONE);
                xAxisSpacer13.setVisibility(GONE);

                break;
            default:
                throw new IllegalArgumentException();
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
            mCurrentDate.add(Calendar.DAY_OF_YEAR, 1);

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

        mCurrentDate.add(mGraphType, jumpBack);
        return returnValue;
    }

    private void initTextView(TextView tv, String text, int textColor) {

        tv.setText(text);
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

    public void setGraphBackgroundColor(int graphBackgroundColor) {
        this.mGraphBackgroundColour = graphBackgroundColor;
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

}
