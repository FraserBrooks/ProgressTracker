package com.fraserbrooks.progresstracker.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;


import com.fraserbrooks.progresstracker.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class CalendarSettings {

    private int mHeaderColor, mHeaderLabelColor, mTodayLabelColor,
                mItemLayoutResource, mPagesColor, mAbbreviationsBarColor,
                mAbbreviationsLabelsColor, mDaysLabelsColor,
                mAnotherMonthsDaysLabelsColor, mDayBackgroundColor;


    private Drawable mPreviousButtonSrc, mForwardButtonSrc,
            mTarget1Resource, mTarget2Resource, mTarget3Resource;

    private Calendar mCurrentDate = CalendarUtils.getCalendar();
    private Calendar mCalendar, mMinimumDate, mMaximumDate;

    private OnCalendarPageChangeListener mOnPreviousPageChangeListener;
    private OnCalendarPageChangeListener mOnForwardPageChangeListener;
    private OnDayClickListener mOnDayClickListener;

    private Set<Date> mTargetOneDays, mTargetTwoDays, mTargetThreeDays;

    private Context mContext;

    public CalendarSettings(Context context){
        mContext = context;
    }

    public Calendar getCalendar(){return  mCalendar;}

    public void setCalendar(Calendar calendar){mCalendar = calendar;}

    public int getHeaderColor(){
        if (mHeaderColor <= 0){
            return mHeaderColor;
        }

        return ContextCompat.getColor(mContext, mHeaderColor);
    }

    public void setHeaderColor(int headerColor){mHeaderColor = headerColor;}

    public int getHeaderLabelColor() {
        if (mHeaderLabelColor <= 0) {
            return mHeaderLabelColor;
        }

        return ContextCompat.getColor(mContext, mHeaderLabelColor);
    }

    public void setHeaderLabelColor(int headerLabelColor) {
        mHeaderLabelColor = headerLabelColor;
    }

    public Drawable getPreviousButtonSrc() {
        return mPreviousButtonSrc;
    }

    public void setPreviousButtonSrc(Drawable previousButtonSrc) {
        mPreviousButtonSrc = previousButtonSrc;
    }

    public Drawable getForwardButtonSrc() {
        return mForwardButtonSrc;
    }

    public void setForwardButtonSrc(Drawable forwardButtonSrc) {
        mForwardButtonSrc = forwardButtonSrc;
    }

    public int getTodayLabelColor() {
        if (mTodayLabelColor == 0) {
            return ContextCompat.getColor(mContext, R.color.defaultColor);
        }

        return mTodayLabelColor;
    }

    public void setTodayLabelColor(int todayLabelColor) {
        mTodayLabelColor = todayLabelColor;
    }

    public Calendar getMinimumDate() {
        return mMinimumDate;
    }

    public void setMinimumDate(Calendar minimumDate) {
        mMinimumDate = minimumDate;
    }

    public Calendar getMaximumDate() {
        return mMaximumDate;
    }

    public void setMaximumDate(Calendar maximumDate) {
        mMaximumDate = maximumDate;
    }

    public int getItemLayoutResource() {
        return mItemLayoutResource;
    }

    public void setItemLayoutResource(int itemLayoutResource) {
        mItemLayoutResource = itemLayoutResource;
    }

    public OnCalendarPageChangeListener getOnPreviousPageChangeListener() {
        return mOnPreviousPageChangeListener;
    }

    public void setOnPreviousPageChangeListener(OnCalendarPageChangeListener onPreviousButtonClickListener) {
        mOnPreviousPageChangeListener = onPreviousButtonClickListener;
    }

    public OnCalendarPageChangeListener getOnForwardPageChangeListener() {
        return mOnForwardPageChangeListener;
    }

    public void setOnForwardPageChangeListener(OnCalendarPageChangeListener onForwardButtonClickListener) {
        mOnForwardPageChangeListener = onForwardButtonClickListener;
    }

    public Calendar getCurrentDate() {
        return mCurrentDate;
    }

    public int getPagesColor() {
        return mPagesColor;
    }

    public void setPagesColor(int pagesColor) {
        mPagesColor = pagesColor;
    }

    public int getDayBackgroundColor() {
        return mDayBackgroundColor;
    }

    public void setDayBackgroundColor(int resource){
        mDayBackgroundColor = resource;
    }

    public int getAbbreviationsBarColor() {
        return mAbbreviationsBarColor;
    }

    public void setAbbreviationsBarColor(int abbreviationsBarColor) {
        mAbbreviationsBarColor = abbreviationsBarColor;
    }

    public int getAbbreviationsLabelsColor() {
        return mAbbreviationsLabelsColor;
    }

    public void setAbbreviationsLabelsColor(int abbreviationsLabelsColor) {
        mAbbreviationsLabelsColor = abbreviationsLabelsColor;
    }

    public int getDaysLabelsColor() {
        if (mDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, R.color.currentMonthDayColor);
        }

        return mDaysLabelsColor;
    }

    public void setDaysLabelsColor(int daysLabelsColor) {
        mDaysLabelsColor = daysLabelsColor;
    }

    public int getAnotherMonthsDaysLabelsColor() {
        if (mAnotherMonthsDaysLabelsColor == 0) {
            return ContextCompat.getColor(mContext, R.color.nextMonthDayColor);
        }

        return mAnotherMonthsDaysLabelsColor;
    }

    public void setAnotherMonthsDaysLabelsColor(int anotherMonthsDaysLabelsColor) {
        mAnotherMonthsDaysLabelsColor = anotherMonthsDaysLabelsColor;
    }

    public OnDayClickListener getOnDayClickListener() {
        return mOnDayClickListener;
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public void setTarget1Resource(Drawable drawable){
        mTarget1Resource = drawable;
    }

    public void setTarget2Resource(Drawable drawable){
        mTarget2Resource = drawable;
    }

    public void setTarget3Resource(Drawable drawable){
        mTarget3Resource = drawable;
    }


    public Drawable getTarget1Resource() {
        return mTarget1Resource;
    }

    public Drawable getTarget2Resource() {
        return mTarget2Resource;
    }

    public Drawable getTarget3Resource() {
        return mTarget3Resource;
    }

    public boolean target1MetOn(Date day) {
        return mTargetOneDays != null && mTargetOneDays.contains(day);
    }

    public boolean target2MetOn(Date day) {
        return mTargetTwoDays != null && mTargetTwoDays.contains(day);
    }

    public boolean target3MetOn(Date day) {
        return mTargetThreeDays != null && mTargetThreeDays.contains(day);
    }


    public void setTargetOneDays(Set<Date> days) {
        mTargetOneDays = days;
    }

    public void setTargetTwoDays(Set<Date> days){
        mTargetTwoDays = days;
    }

    public void setTargetThreeDays(Set<Date> days){
        mTargetThreeDays = days;
    }


}
