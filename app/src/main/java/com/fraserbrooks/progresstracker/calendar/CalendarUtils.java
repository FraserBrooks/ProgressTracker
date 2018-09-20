package com.fraserbrooks.progresstracker.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalendarUtils {

    /**
     * @return An instance of the Calendar object with hour set to 00:00:00:00
     */
    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        setMidnight(calendar);

        return calendar;
    }

    /**
     * This method sets an hour in the calendar object to 00:00:00:00
     *
     * @param calendar Calendar object which hour should be set to 00:00:00:00
     */
    public static void setMidnight(Calendar calendar) {
        if (calendar != null) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }

    /**
     * This method compares calendars using month and year
     *
     * @param firstCalendar  First calendar object to compare
     * @param secondCalendar Second calendar object to compare
     * @return Boolean value if first calendar is before the second one
     */
    public static boolean isMonthBefore(Calendar firstCalendar, Calendar secondCalendar) {
        if (secondCalendar == null) {
            return false;
        }

        Calendar firstDay = (Calendar) firstCalendar.clone();
        setMidnight(firstDay);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar secondDay = (Calendar) secondCalendar.clone();
        setMidnight(secondDay);
        secondDay.set(Calendar.DAY_OF_MONTH, 1);

        return firstDay.before(secondDay);
    }

    /**
     * This method compares calendars using month and year
     *
     * @param firstCalendar  First calendar object to compare
     * @param secondCalendar Second calendar object to compare
     * @return Boolean value if first calendar is after the second one
     */
    public static boolean isMonthAfter(Calendar firstCalendar, Calendar secondCalendar) {
        if (secondCalendar == null) {
            return false;
        }

        Calendar firstDay = (Calendar) firstCalendar.clone();
        setMidnight(firstDay);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar secondDay = (Calendar) secondCalendar.clone();
        setMidnight(secondDay);
        secondDay.set(Calendar.DAY_OF_MONTH, 1);

        return firstDay.after(secondDay);
    }

    /**
     * This method returns a string containing a month's name and a year (in number).
     * It's used instead of new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format([Date]);
     * because that method returns a month's name in incorrect form in some languages (i.e. in Polish)
     *
     * @param context  An array of months names
     * @param calendar A Calendar object containing date which will be formatted
     * @return A string of the formatted date containing a month's name and a year (in number)
     */
    public static String getMonthAndYearDate(Context context, Calendar calendar) {
        return String.format("%s  %s",
                context.getResources().getStringArray(R.array.material_calendar_months_array)[calendar.get(Calendar.MONTH)],
                calendar.get(Calendar.YEAR));
    }

    /**
     * This method returns a list of calendar objects between to dates
     * @param firstDay Calendar representing a first selected date
     * @param lastDay Calendar representing a last selected date
     * @return List of selected dates between two dates
     */
    public static ArrayList<Calendar> getDatesRange(Calendar firstDay, Calendar lastDay) {
        if (lastDay.before(firstDay)) {
            return getCalendarsBetweenDates(lastDay.getTime(), firstDay.getTime());
        }

        return getCalendarsBetweenDates(firstDay.getTime(), lastDay.getTime());
    }

    private static ArrayList<Calendar> getCalendarsBetweenDates(Date dateFrom, Date dateTo) {
        ArrayList<Calendar> calendars = new ArrayList<>();

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(dateFrom);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(dateTo);

        long daysBetweenDates = TimeUnit.MILLISECONDS.toDays(
                calendarTo.getTimeInMillis() - calendarFrom.getTimeInMillis());

        for (int i = 1; i < daysBetweenDates; i++) {
            Calendar calendar = (Calendar) calendarFrom.clone();
            calendars.add(calendar);
            calendar.add(Calendar.DATE, i);
        }

        return calendars;
    }

    /**
     * This method is used to count a number of months between two dates
     * @param startCalendar Calendar representing a first date
     * @param endCalendar Calendar representing a last date
     * @return Number of months
     */
    public static int getMonthsBetweenDates(Calendar startCalendar, Calendar endCalendar) {
        int years = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return years * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }


    public static void setAbbreviationsLabelsColor(View view, int color) {
        if (color == 0) {
            return;
        }

        ((TextView) view.findViewById(R.id.mondayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.tuesdayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.wednesdayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.thursdayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.fridayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.saturdayLabel)).setTextColor(color);
        ((TextView) view.findViewById(R.id.sundayLabel)).setTextColor(color);
    }

    public static void setHeaderColor(View view, int color) {
        if (color == 0) {
            return;
        }

        ConstraintLayout mCalendarHeader = (ConstraintLayout) view.findViewById(R.id.calendarHeader);
        mCalendarHeader.setBackgroundColor(color);
    }

    public static void setHeaderLabelColor(View view, int color) {
        if (color == 0) {
            return;
        }

        ((TextView) view.findViewById(R.id.currentDateLabel)).setTextColor(color);
    }

    public static void setAbbreviationsBarColor(View view, int color) {
        if (color == 0) {
            return;
        }

        view.findViewById(R.id.abbreviationsBar).setBackgroundColor(color);
    }

    public static void setPagesColor(View view, int color) {
        if (color == 0) {
            return;
        }

        view.findViewById(R.id.calendarViewPager).setBackgroundColor(color);
    }

    public static void setPreviousButtonImage(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }

        ((ImageButton) view.findViewById(R.id.previousButton)).setImageDrawable(drawable);
    }

    public static void setForwardButtonImage(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }

        ((ImageButton) view.findViewById(R.id.forwardButton)).setImageDrawable(drawable);
    }

}
