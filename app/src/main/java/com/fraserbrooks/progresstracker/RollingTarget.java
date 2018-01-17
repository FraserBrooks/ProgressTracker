package com.fraserbrooks.progresstracker;

import android.util.Log;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Fraser on 11/01/2018.
 */

public class RollingTarget implements Target, Serializable {

    private final String TAG = "Main>RollingTarget";

    private boolean completed = false;
    private String parentName;
    private int rolloverTime;
    private int targetTime;
    private HashMap<String, Integer> timeLog;
    private Calendar startDate;

    public RollingTarget(int targetTime,
                         int rolloverTime,
                         String parentName){
        if(!(rolloverTime == DAY ||
                rolloverTime == WEEK ||
                rolloverTime == MONTH ||
                rolloverTime == YEAR)){
            throw new InvalidParameterException("Target class: Invalid rolloverTime: " + rolloverTime);
        }
        this.targetTime = targetTime;
        this.rolloverTime = rolloverTime;
        this.startDate = Calendar.getInstance();
        this.timeLog = new HashMap<>();
        this.parentName = parentName;
    }

    @Override
    public String getTrackerName() {
        return parentName;
    }

    @Override
    public String getTargetTitle() {
        String val = "";
        if (targetTime < 60){
            val += targetTime + " minutes of ";
        } else{
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            float hours = ((float) targetTime)/60f;
            String hourString = " hours of ";
            if(hours == 1){
                hourString = " hour of ";
            }
            val += df.format(hours) + hourString;
        }
        val += parentName;
        switch (getRolloverTime()){
            case DAY:
                val += " a day:";
                break;
            case WEEK:
                val += " a week:";
                break;
            case MONTH:
                val += " a month:";
                break;
            case YEAR:
                val += " a year:";
                break;
            default:
                val = "error";
        }
        return val;
    }

    @Override
    public String getSecondaryLabel() {
        return "Average:";
    }

    @Override
    public String getLowerLabel() {
        String val = "";
        switch (getRolloverTime()){
            case DAY:
                val = "today:";
                break;
            case WEEK:
                val = "this week:";
                break;
            case MONTH:
                val = "this month:";
                break;
            case YEAR:
                val = "this year:";
                break;
            default:
                val = "error";
        }
        return val;
    }

    @Override
    public Calendar getStartDate() {
        return startDate;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public float getTargetProgress() {
        String key = getKey();
        Integer soFar = timeLog.get(key);
        if(soFar == null){
            return 0;
        }else{
            float p = (float) soFar;
            p = p / (float) targetTime;
            if (p > 1){
                completed = true;
                return 1f;
            } else{
                completed = false;
                return p;
            }
        }
    }

    @Override
    public String getPercentageString() {

        Calendar now = Calendar.getInstance();
        int achieved = 0;
        int total = 0;
        Calendar cal = (Calendar) startDate.clone();

        while(cal.getTime().compareTo(now.getTime()) < 0){
            if(metOnDate(cal)){
                achieved++;
            }
            total++;
            addToCalendar(cal);
        }

        if(sameRolloverPeriod(cal, now)){
            Log.d(TAG, "getPercentageString: sameRolloverPeriod");
            if(metOnDate(cal)){
                achieved++;
            }
            total++;
        }

        if (total == 0){
            return "0%";
        }else{
            float p =  100f * ((float) achieved)/ ((float) total);
            return (int) p + "%";
        }
    }

    @Override
    public void updateTime(int minutes) {
        String key = getKey();
        Integer soFar = timeLog.get(key);
        if (soFar == null){
            timeLog.put(key, minutes);
        } else {
            int toAdd = (soFar + minutes > 0) ? (soFar + minutes) : 0;
            timeLog.put(key, toAdd);
        }

        // Just to update completed
        getTargetProgress();

    }

    @Override
    public void updateTrackerName(String name){
        this.parentName = name;
    }

    @Override
    public void updateLevel(int lvl) {
        // do nothing
    }

    private String getKey(Calendar c){
        String key = "";
        key += c.get(Calendar.YEAR);
        if(getRolloverTime() == MONTH){
            key += " - ";
            key += c.get(Calendar.MONTH);
        } else if (getRolloverTime() == WEEK){
            key += " - ";
            key += c.get(Calendar.WEEK_OF_YEAR);
        } else if (getRolloverTime() == DAY){
            key += " - ";
            key += c.get(Calendar.DAY_OF_YEAR);
        }
        return key;
    }

    private Calendar getCalendarFromKey(String k){
        Calendar c = Calendar.getInstance();
        if(getRolloverTime() == MONTH){
            String[] yearMonth = k.split(" - ");
            c.set(Calendar.YEAR, Integer.parseInt(yearMonth[0]));
            c.set(Calendar.MONTH, Integer.parseInt(yearMonth[1]));
        } else if (getRolloverTime() == WEEK){
            String[] yearMonth = k.split(" - ");
            c.set(Calendar.YEAR, Integer.parseInt(yearMonth[0]));
            c.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(yearMonth[1]));
        } else if (getRolloverTime() == DAY){
            String[] yearMonth = k.split(" - ");
            c.set(Calendar.YEAR, Integer.parseInt(yearMonth[0]));
            c.set(Calendar.DAY_OF_YEAR, Integer.parseInt(yearMonth[1]));
        } else {
            c.set(Calendar.YEAR, Integer.parseInt(k));
        }
        return c;
    }

    private String getKey(){
        return getKey(Calendar.getInstance());
    }

    private boolean metOnDate(Calendar calendar){
        String key = getKey(calendar);
        Integer soFar = timeLog.get(key);
        if (soFar == null){
            return false;
        }
        return soFar >= targetTime;
    }

    private boolean sameRolloverPeriod(Calendar c, Calendar now){

        switch (getRolloverTime()){
            case DAY:
                return c.get(Calendar.DAY_OF_YEAR)
                        == now.get(Calendar.DAY_OF_YEAR);
            case WEEK:
                return c.get(Calendar.WEEK_OF_YEAR)
                        == now.get(Calendar.WEEK_OF_YEAR);
            case MONTH:
                return c.get(Calendar.MONTH)
                        == now.get(Calendar.MONTH);
            case YEAR:
                return c.get(Calendar.YEAR)
                        == now.get(Calendar.YEAR);
            default:
                throw new InvalidParameterException(
                        "RollingTarget: Invalid rollover time in sameRolloverPeriod");
        }
    }

    private void addToCalendar(Calendar cal){
        switch (getRolloverTime()){
            case DAY:
                cal.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case WEEK:
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case MONTH:
                cal.add(Calendar.MONTH, 1);
                break;
            case YEAR:
                cal.add(Calendar.YEAR, 1);
                break;
            default:
                throw new InvalidParameterException(
                        "RollingTarget: Invalid time added to calendar");
        }
    }

    public int getRolloverTime() {
        return rolloverTime;
    }

    public ArrayList<Calendar> getDaysCompleted(){
        ArrayList<Calendar> calendars = new ArrayList<>();

        for (String key:timeLog.keySet()) {
            if(timeLog.get(key) >= targetTime){
                calendars.add(getCalendarFromKey(key));
            }
        }

        return calendars;
    }
}
