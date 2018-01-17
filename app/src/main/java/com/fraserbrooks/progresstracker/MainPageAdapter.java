package com.fraserbrooks.progresstracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Fraser on 31/12/2017.
 */

public class MainPageAdapter extends FragmentStatePagerAdapter{

    private final String TAG = "Main>.MainPageAdapter";
    private int numberOfTabs;

    private Fragment homeFragment;
    private Fragment targetFragment;


    public MainPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numberOfTabs = numOfTabs;
        homeFragment = null;
        targetFragment = null;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                HomeScreenFragment homeScreen = new HomeScreenFragment();
                setHomeFragment(homeScreen);
                return homeScreen;
            case 1:
                TargetScreenFragment targetScreen = new TargetScreenFragment();
                setTargetFragment(targetScreen);
                return targetScreen;
            case 2:
                CalendarScreenFragment calendarScreen = new CalendarScreenFragment();
                return calendarScreen;
            case 3:
                SettingsScreenFragment settingsScreen = new SettingsScreenFragment();
                return settingsScreen;
            default:
                return null;
        }
    }

    private void setHomeFragment(Fragment f){
        this.homeFragment = f;
    }

    public Fragment getHomeFragment(){
        return homeFragment;
    }



    @Override
    public int getCount() {
        return numberOfTabs;
    }


    private void setTargetFragment(Fragment f) {
        this.targetFragment = f;
    }

    public Fragment getTargetFragment() {
        return targetFragment;
    }
}
