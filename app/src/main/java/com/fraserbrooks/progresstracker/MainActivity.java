package com.fraserbrooks.progresstracker;

import android.os.Bundle;

import com.fraserbrooks.progresstracker.targets.TargetsFragment;
import com.fraserbrooks.progresstracker.targetscalendar.CalendarFragment;
import com.fraserbrooks.progresstracker.trackers.TrackersFragment;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.fraserbrooks.progresstracker.settings.SettingsScreenFragment;

/**
 * Created by Fraser on 06/04/2018.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main>MainActivity";
    private static final int NUMBER_OF_TABS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViewPager();



    }

    private void initViewPager() {
        final ViewPager viewPager = findViewById(R.id.pager);
        final MainPageAdapter adapter = new MainPageAdapter(
                getSupportFragmentManager(),
                NUMBER_OF_TABS);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setSelectedTabIndicatorHeight(TAB_HEIGHT);

        int[] tabIcons = {
                R.drawable.activity_main_tab_button_home,
                R.drawable.activity_main_tab_button_targets,
                R.drawable.activity_main_tab_button_calendar,
                R.drawable.activity_main_tab_button_settings
        };

        for(int i = 0; i < NUMBER_OF_TABS ; i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if(tab != null) tab.setIcon(tabIcons[i]);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int newPosition) {
                if(newPosition == 2){
                    Log.d(TAG, "onPageSelected: calendar screen loaded");

                    if(adapter.getCalendarFragment() != null){
                        adapter.getCalendarFragment().onResume();
                    }

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @SuppressWarnings("unused")
    private static class MainPageAdapter extends FragmentStatePagerAdapter{

        private final String TAG = "MainPageAdapter";
        private int numberOfTabs;

        private TrackersFragment trackersFragment;
        private TargetsFragment targetFragment;
        private CalendarFragment calendarFragment;


        MainPageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numberOfTabs = numOfTabs;
            trackersFragment = null;
            targetFragment = null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    TrackersFragment trackerFragment = TrackersFragment.newInstance();
                    setTrackersFragment(trackerFragment);
                    return trackerFragment;
                case 1:
                    TargetsFragment targetFragment = TargetsFragment.newInstance();
                    setTargetFragment(targetFragment);
                    return targetFragment;
                case 2:
                    CalendarFragment calendarFragment = new CalendarFragment();
                    setCalendarFragment(calendarFragment);
                    return calendarFragment;
                case 3:
                    return new SettingsScreenFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numberOfTabs;
        }

        private void setTrackersFragment(TrackersFragment f){
            this.trackersFragment = f;
        }

        public TrackersFragment getTrackersFragment(){
            return trackersFragment;
        }


        private void setTargetFragment(TargetsFragment f) {
            this.targetFragment = f;
        }

        public TargetsFragment getTargetFragment() {
            return targetFragment;
        }

        CalendarFragment getCalendarFragment() {
            return calendarFragment;
        }

        void setCalendarFragment(CalendarFragment calendarFragment) {
            this.calendarFragment = calendarFragment;
        }
    }


}
