package com.fraserbrooks.progresstracker;

import java.util.Calendar;

/**
 * Created by Fraser on 11/01/2018.
 */

public interface Target {

    int DAY = 1;
    int WEEK = 2;
    int MONTH = 3;
    int YEAR = 4;

    public String getTrackerName();

    public String getTargetTitle();

    public String getSecondaryLabel();

    public String getLowerLabel();

    public Calendar getStartDate();

    public boolean isCompleted();

    public float getTargetProgress();

    public String getPercentageString();

    public void updateTime(int minutes);

    public void updateLevel(int lvl);

    public void updateTrackerName(String name);

}
