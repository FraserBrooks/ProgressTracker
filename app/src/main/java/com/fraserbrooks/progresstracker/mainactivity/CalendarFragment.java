package com.fraserbrooks.progresstracker.mainactivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment implements CalendarContract.View {

    private final String TAG = "CalendarFragment";

    private ViewGroup fragmentScreen;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ArrayAdapter<Target> mAdapter;
    private Spinner mSpinner1;
    private Spinner mSpinner2;
    private Spinner mSpinner3;
    private int[] storedSpinnerPositions;

    private CalendarContract.Presenter mPresenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        //todo load preferences earlier to reduce lag
        if (getActivity() == null) return;
        sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);

        if(getContext() == null) return;

        storedSpinnerPositions = getStoredSpinnerSelections(sharedPref);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        new CalendarPresenter(Injection.provideRepository(getContext()),
                this, AppExecutors.getInstance());
        mPresenter.start();
//        try {
//            Thread.sleep(445);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }



    @Override
    public void setTargetSpinners() {

        for(int i = 0; i < storedSpinnerPositions.length; i++){
            if(storedSpinnerPositions[i] > mAdapter.getCount()) storedSpinnerPositions[i] = 0;
        }

        mSpinner1.setSelection(storedSpinnerPositions[0]);
        mSpinner2.setSelection(storedSpinnerPositions[1]);
        mSpinner3.setSelection(storedSpinnerPositions[2]);

    }

    @Override
    public void addToTargetSpinners(Target target) {
        mAdapter.add(target);
    }

    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        // Inflate the layout for this fragment
        fragmentScreen = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_screen,
                container, false);

        CalendarView calendarView =
                fragmentScreen.findViewById(R.id.calendar_view);

        calendarView.setBackgroundColor(getResources().getColor(R.color.appBG));

        mSpinner1 = fragmentScreen.findViewById(R.id.calendar_spinner_1);
        mSpinner2 = fragmentScreen.findViewById(R.id.calendar_spinner_2);
        mSpinner3 = fragmentScreen.findViewById(R.id.calendar_spinner_3);

        mSpinner1.setAdapter(mAdapter);
        mSpinner2.setAdapter(mAdapter);
        mSpinner3.setAdapter(mAdapter);


        mSpinner1.setOnItemSelectedListener(oisListener);
        mSpinner2.setOnItemSelectedListener(oisListener);
        mSpinner3.setOnItemSelectedListener(oisListener);

        return fragmentScreen;
    }

    @Override
    public Target getFirstSelectedTarget() {
        return (Target) mSpinner1.getSelectedItem();
    }

    @Override
    public Target getSecondSelectedTarget() {
        return (Target) mSpinner2.getSelectedItem();
    }

    @Override
    public Target getThirdSelectedTarget() {
        return (Target) mSpinner3.getSelectedItem();
    }

    @Override
    public void updateCalendar(final List<Calendar> firstTargetDays, final List<Calendar> secondTargetDays, final List<Calendar> thirdTargetDays) {

        final ArrayList<EventDay> days = new ArrayList<>();

        if(firstTargetDays != null){
            for(Calendar c : firstTargetDays){
                days.add(new EventDay(c, R.drawable.calendar_square_1));
            }
        }

        if(secondTargetDays != null){
            for(Calendar c : secondTargetDays){
                days.add(new EventDay(c, R.drawable.calendar_square_2));
            }
        }

        if(thirdTargetDays != null){
            for(Calendar c : thirdTargetDays){
                days.add(new EventDay(c, R.drawable.calendar_square_3));
            }
        }


        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarView calendarView =
                        fragmentScreen.findViewById(R.id.calendar_view);
                calendarView.setEvents(days);
            }
        });


    }


    @Override
    public void showLoading() {
        // todo
    }

    @Override
    public void hideLoading() {
        // todo
    }

    @Override
    public void showNoDataAvailable() {
        // todo
    }

    @NonNull
    private int[] getStoredSpinnerSelections(SharedPreferences sharedPref) {

        int[] setSpinnerSelections = new int[3];
        setSpinnerSelections[0] = sharedPref.getInt(getString(R.string.calendar_saved_target_1), 0);
        setSpinnerSelections[1] = sharedPref.getInt(getString(R.string.calendar_saved_target_2), 0);
        setSpinnerSelections[2] = sharedPref.getInt(getString(R.string.calendar_saved_target_3), 0);

        return setSpinnerSelections;
    }



    private AdapterView.OnItemSelectedListener  oisListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    editor = sharedPref.edit();
                    switch (adapterView.getId()){
                        case R.id.calendar_spinner_1:
                            Log.d(TAG, "onItemSelected: storing spinner 1 selected pos = " + position
                                    + " in spinner1 pref");
                            editor.putInt( getString(R.string.calendar_saved_target_1), position);
                            break;
                        case R.id.calendar_spinner_2:
                            Log.d(TAG, "onItemSelected: storing spinner 2 selected pos = " + position
                                    + " in spinner2 pref");
                            editor.putInt( getString(R.string.calendar_saved_target_2), position);
                            break;
                        case R.id.calendar_spinner_3:
                            Log.d(TAG, "onItemSelected: storing spinner 3 selected pos = " + position
                                    + " in spinner3 pref");
                            editor.putInt( getString(R.string.calendar_saved_target_3), position);
                            break;
                        default:
                            Log.e(TAG, "onItemSelected: can't match spinner id");
                    }
                    editor.apply();
                    mPresenter.initCalendar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };

//    private class TargetSpinnerAdapter extends ArrayAdapter<Target> {
//
//        private int mResource;
//
//        public TargetSpinnerAdapter(@NonNull Context context, int resource) {
//            super(context, resource);
//            mResource = resource;
//        }
//
//
//        @NonNull
//        @Override
//        public View getView(int i, View convertView, ViewGroup viewGroup){
//            // Check if an existing view is being reused, otherwise inflate the view
//            if (convertView == null){
//                convertView = super.getView(i, null, viewGroup);
//            }
//
//            final Target target = getItem(i);
//
//            if (target != null){
//                TextView tv = (TextView) convertView;
//                tv.setText(target.getTargetTitle());
//            }
//
//            return convertView;
//        }
//
//    }

}
