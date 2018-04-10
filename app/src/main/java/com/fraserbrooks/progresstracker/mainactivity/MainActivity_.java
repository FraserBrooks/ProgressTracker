package com.fraserbrooks.progresstracker.mainactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.fraserbrooks.progresstracker.CalendarScreenFragment;
import com.fraserbrooks.progresstracker.HomeScreenFragment;
import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.MainPageAdapter;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.SettingsScreenFragment;
import com.fraserbrooks.progresstracker.TargetScreenFragment;
import com.fraserbrooks.progresstracker.Tracker;
import com.fraserbrooks.progresstracker.data.source.Repository;

/**
 * Created by Frase on 06/04/2018.
 */

public class MainActivity_ extends AppCompatActivity {

    private static final String TAG = "main>MainActivity";
    private static final int NUMBER_OF_TABS = 4;
    private static final int TAB_HEIGHT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViewPager();

        Repository r = Injection.provideRepository(getApplicationContext());

    }

    private void initViewPager() {
        final ViewPager viewPager = findViewById(R.id.pager);
        final MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager(), NUMBER_OF_TABS);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(TAB_HEIGHT);

        int[] tabIcons = {
                R.drawable.button_home,
                R.drawable.button_targets,
                R.drawable.button_calendar,
                R.drawable.button_settings
        };

        for(int i = 0; i < NUMBER_OF_TABS ; i++){
            if(i<tabIcons.length){
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if(tab != null) tab.setIcon(tabIcons[i]);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int newPosition) {
                if(newPosition == 1 && oldPosition == 0){
                    Log.d(TAG, "onPageSelected: home to target screen");

                    if(adapter.getHomeFragment() != null){
                        adapter.getHomeFragment().onPause();
                    }

                    if(adapter.getTargetFragment() != null){
                        adapter.getTargetFragment().onResume();
                    }
                }
                oldPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private static class MainPageAdapter extends FragmentStatePagerAdapter{

        private final String TAG = "Main>.MainPageAdapter";
        private int numberOfTabs;

        private HomeScreenFragment homeFragment;
        private TargetScreenFragment targetFragment;


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

        @Override
        public int getCount() {
            return numberOfTabs;
        }

        private void setHomeFragment(HomeScreenFragment f){
            this.homeFragment = f;
        }

        public HomeScreenFragment getHomeFragment(){
            return homeFragment;
        }


        private void setTargetFragment(TargetScreenFragment f) {
            this.targetFragment = f;
        }

        public TargetScreenFragment getTargetFragment() {
            return targetFragment;
        }
    }


    private static class TargetAdapter extends ArrayAdapter<String /*Target*/  >{


        public TargetAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }
    }

    private static class TrackerAdapter extends ArrayAdapter<String /*Tracker*/  >{

        public TrackerAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }
    }

}
