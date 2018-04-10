package com.fraserbrooks.progresstracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main>MainActivity";



    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        final ViewPager viewPager = findViewById(R.id.pager);
        final MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager(),
                4);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(10);

        tabLayout.getTabAt(0).setIcon(R.drawable.button_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.button_targets);
        tabLayout.getTabAt(2).setIcon(R.drawable.button_calendar);
        tabLayout.getTabAt(3).setIcon(R.drawable.button_settings);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int newPosition) {
                if(newPosition == 1 && oldPosition == 0){
                    Log.d(TAG, "onPageSelected: home to target screen");
                    //todo: re-implement this in fragment class

                    if(adapter.getHomeFragment() != null){
                        adapter.getHomeFragment().onPause();
                    }

                    if(adapter.getTargetFragment() != null){
                        adapter.getTargetFragment().onResume();
                    }

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(adapter.getHomeFragment() != null
//                                            && adapter.getTargetFragment() != null){
//                                        adapter.getHomeFragment().onPause();
//                                        adapter.getTargetFragment().onResume();
//                                    }
//                                }
//                            });
//                        }
//                    }).start();
                }

                oldPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
