package com.fraserbrooks.progresstracker.trackers.view.trackergraphs;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;

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

    //private int mGraphWindowHeight;
    //private int mGraphBarMargin;

    //private int mGraphWindowColor, mGraphBackgroundColour,
    //        mTimeLabelTextColor, mAxisTextColor;

    //private boolean mShowIntervalLabel, mShowTimeLabels, mShowGraphAxis, mShowHighestValue;

    private Drawable mGraphBarResource;

    private int mGraphType, mGraphFormat;

    private LinearLayout mGraphWindow;

    private View graphBox1, graphBox2, graphBox3, graphBox4, graphBox5, graphBox6, graphBox7;

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
        initTimeLabelsLayout();
        initGraphWindow(counts, max);
    }

    private void init(AttributeSet attrs){

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);
        inflater.inflate(R.layout.shared_ui_time_graph_light, this);

        mGraphWindow = getRootView().findViewById(R.id.time_graph_graph_window);


        xAxis1 = getRootView().findViewById(R.id.time_graph_text_view_1);
        xAxis2 = getRootView().findViewById(R.id.time_graph_text_view_2);
        xAxis3 = getRootView().findViewById(R.id.time_graph_text_view_3);
        xAxis4 = getRootView().findViewById(R.id.time_graph_text_view_4);
        xAxis5 = getRootView().findViewById(R.id.time_graph_text_view_5);
        xAxis6 = getRootView().findViewById(R.id.time_graph_text_view_6);
        xAxis7 = getRootView().findViewById(R.id.time_graph_text_view_7);

        graphBox1 = getRootView().findViewById(R.id.graph_box_1);
        graphBox2 = getRootView().findViewById(R.id.graph_box_2);
        graphBox3 = getRootView().findViewById(R.id.graph_box_3);
        graphBox4 = getRootView().findViewById(R.id.graph_box_4);
        graphBox5 = getRootView().findViewById(R.id.graph_box_5);
        graphBox6 = getRootView().findViewById(R.id.graph_box_6);
        graphBox7 = getRootView().findViewById(R.id.graph_box_7);


        mOrdinalDates = getResources().getStringArray(R.array.ordinal_dates);

        mCurrentDate = Calendar.getInstance();
    }



    private void initGraphWindow(int[] counts, float highestValue){

        float graphHeight = getResources().getDimension(R.dimen.graph_height_small);

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

        /**
         if (mGraphType == DAY){

         for(int height : heights){

         Log.d(TAG, "initGraphWindow: werty werty ::::::::" + height);
         }

         }**/

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

    private void initTimeLabelsLayout() {
        switch (mGraphType) {
            case DAY:

                initTextView(xAxis1, getTimeLabelText(6));
                initTextView(xAxis2, getTimeLabelText(5));
                initTextView(xAxis3, getTimeLabelText(4));
                initTextView(xAxis4, getTimeLabelText(3));
                initTextView(xAxis5, getTimeLabelText(2));
                initTextView(xAxis6, getTimeLabelText(1));
                initTextView(xAxis7, getTimeLabelText(0));

                break;
            case WEEK:

                initTextView(xAxis1, getTimeLabelText(4));
                initTextView(xAxis2, getTimeLabelText(3));
                initTextView(xAxis3, getTimeLabelText(2));
                initTextView(xAxis4, getTimeLabelText(1));
                initTextView(xAxis5, getTimeLabelText(0));

                xAxis6.setVisibility(GONE);
                xAxis7.setVisibility(GONE);

                break;
            case MONTH:

                initTextView(xAxis1, getTimeLabelText(5));
                initTextView(xAxis2, getTimeLabelText(4));
                initTextView(xAxis3, getTimeLabelText(3));
                initTextView(xAxis4, getTimeLabelText(2));
                initTextView(xAxis5, getTimeLabelText(1));
                initTextView(xAxis6, getTimeLabelText(0));

                xAxis7.setVisibility(GONE);

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

    private void initTextView(TextView tv, String text) {
        tv.setText(text);
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