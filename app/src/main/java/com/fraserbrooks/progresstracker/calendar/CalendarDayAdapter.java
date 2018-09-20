package com.fraserbrooks.progresstracker.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarDayAdapter extends ArrayAdapter<Date> {

    private static final String TAG = "CalendarDayAdapter";

    private final float OTHER_MONTH_ALPHA = 0.18f;

    private LayoutInflater mLayoutInflater;
    private int mPageMonth;
    private Calendar mToday = CalendarUtils.getCalendar();
    private CalendarSettings mCalendarSettings;

    public CalendarDayAdapter(Context context,
                               int pageMonth,
                               ArrayList<Date> dates,
                               CalendarSettings calendarSettings){
        super(context, calendarSettings.getItemLayoutResource(), dates);

        mPageMonth = pageMonth;
        mCalendarSettings = calendarSettings;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent){

        if(view == null){
            view = mLayoutInflater.inflate(mCalendarSettings.getItemLayoutResource(),
                    parent, false);
        }

        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

        View targetIcon1 = view.findViewById(R.id.calendar_day_target_icon_1);
        View targetIcon2 = view.findViewById(R.id.calendar_day_target_icon_2);
        View targetIcon3 = view.findViewById(R.id.calendar_day_target_icon_3);

        targetIcon1.setBackground(mCalendarSettings.getTarget1Resource());
        targetIcon2.setBackground(mCalendarSettings.getTarget2Resource());
        targetIcon3.setBackground(mCalendarSettings.getTarget3Resource());

        Calendar day = new GregorianCalendar();
        day.setTime(getItem(position));

        if(isCurrentMonthDay(day)){
            if(mToday.equals(day)){
                dayLabel.setTextColor(mCalendarSettings.getTodayLabelColor());
                dayLabel.setTypeface(null, Typeface.BOLD);
            }else{
                dayLabel.setTextColor(mCalendarSettings.getDaysLabelsColor());
                dayLabel.setTypeface(null, Typeface.NORMAL);
            }
        }else{
            dayLabel.setTextColor(mCalendarSettings.getAnotherMonthsDaysLabelsColor());
            dayLabel.setTypeface(null, Typeface.NORMAL);

            targetIcon1.setAlpha(OTHER_MONTH_ALPHA);
            targetIcon2.setAlpha(OTHER_MONTH_ALPHA);
            targetIcon3.setAlpha(OTHER_MONTH_ALPHA);

        }
        dayLabel.setBackgroundColor(mCalendarSettings.getDayBackgroundColor());

        if(mCalendarSettings.target1MetOn(day.getTime())){
            targetIcon1.setVisibility(View.VISIBLE);
        }else{
            targetIcon1.setVisibility(View.INVISIBLE);
        }

        if(mCalendarSettings.target2MetOn(day.getTime())){
            targetIcon2.setVisibility(View.VISIBLE);
        }else{
            targetIcon2.setVisibility(View.INVISIBLE);
        }

        if(mCalendarSettings.target3MetOn(day.getTime())){
            targetIcon3.setVisibility(View.VISIBLE);
        }else{
            targetIcon3.setVisibility(View.INVISIBLE);
        }

        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        return view;
    }


    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth &&
                !((mCalendarSettings.getMinimumDate() != null && day.before(mCalendarSettings.getMinimumDate()))
                        || (mCalendarSettings.getMaximumDate() != null && day.after(mCalendarSettings.getMaximumDate())));
    }

    public void notifyChange(Date date) {

        int i = getPosition(date);

        if(i != -1){
            remove(date);
            insert(date, i);
        }else{
            Log.d(TAG, "update: could not find date in adapter" );
        }

    }
}
