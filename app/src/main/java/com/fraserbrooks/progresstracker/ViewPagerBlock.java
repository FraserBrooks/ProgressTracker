package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Fraser on 15/01/2018.
 */

public class ViewPagerBlock extends ViewPager {


    private final String TAG = "Main>view_pager";
    private int calendarStart;
    private int calendarEnd;
    private boolean blockEvents;

    public ViewPagerBlock(Context context) {
        super(context);
    }

    public ViewPagerBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return blockEvents;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent: called ");
        setBlockEvents(false);
        return super.onTouchEvent(ev);
    }

    public void setBlockEvents(boolean blockEvents) {
        this.blockEvents = blockEvents;
    }
}
