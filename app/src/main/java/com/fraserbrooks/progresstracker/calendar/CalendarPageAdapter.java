package com.fraserbrooks.progresstracker.calendar;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fraserbrooks.progresstracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CalendarPageAdapter extends PagerAdapter {

    /**
     * Number of months the calendar can display
     * 1201 months = 50 years in each direction
     */
    public static final int CALENDAR_SIZE = 1201;


    private static final String TAG = "CalendarPageAdapter";

    private CalendarSettings mCalendarSettings;
    private Context mContext;
    private CalendarGridView mCalendarGridView;

    private int mPageMonth;

    /** To keep a reference to adapters to allow updates without having to
     * redraw entire calendar **/
    private HashMap<Integer, CalendarDayAdapter> mCachedAdapters;

    public CalendarPageAdapter(Context context, CalendarSettings calendarSettings){
        mContext = context;
        mCalendarSettings = calendarSettings;

        mCachedAdapters = new LinkedHashMap<>();
    }

    @Override
    public int getCount() {
        return CALENDAR_SIZE;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCalendarGridView = (CalendarGridView) inflater.inflate(R.layout.calendar_view_grid, null);

        loadMonth(position);

        mCalendarGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Calendar day = new GregorianCalendar();

            day.setTime((Date) adapterView.getItemAtPosition(i));

            if(mCalendarSettings.getOnDayClickListener() != null){
                mCalendarSettings.getOnDayClickListener().onDayClicked(day);
            }
        });

        container.addView(mCalendarGridView);

        return mCalendarGridView;
    }

    private void loadMonth(int position){

        ArrayList<Date> days = new ArrayList<>();

        //Get Calendar object instance
        Calendar calendar = (Calendar) mCalendarSettings.getCurrentDate().clone();

        // Add months to Calendar (a number of months depends on ViewPager position)
        calendar.add(Calendar.MONTH, position);

        // Set day of month as 1
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get a number of the first day of the week (SUN = 1, Mon = 2..)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Count when month is beginning
        int monthStartCell = dayOfWeek + (dayOfWeek == 1 ? 5 : -2);

        // Subtract a number of days equal to the number of days from the month
        // before to be shown so the calendar is always square
        calendar.add(Calendar.DAY_OF_MONTH, -monthStartCell);

        /*
        Get all days of one page (42 is the number of cells that the calendar
        will display, some from previous/next month displayed slightly transparent,
        most part of the current month displayed normally)
         */
        while(days.size() < 42){
            days.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.add(Calendar.MONTH, -1);

        mPageMonth = calendar.get(Calendar.MONTH);

        CalendarDayAdapter adapter = new CalendarDayAdapter(mContext, mPageMonth, days, mCalendarSettings);

        // Save reference to view for quick updates
        mCachedAdapters.put(position, adapter);

        mCalendarGridView.setAdapter(adapter);

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mCachedAdapters.remove(position);
        container.removeView((View) object);
    }

    public void notifyChange(Date date){

        for(CalendarDayAdapter adapter : mCachedAdapters.values()){
            adapter.notifyChange(date);
        }
    }

}
