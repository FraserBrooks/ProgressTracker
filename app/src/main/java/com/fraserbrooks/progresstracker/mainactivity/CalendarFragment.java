package com.fraserbrooks.progresstracker.mainactivity;

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
import android.widget.Spinner;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
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
    private CalendarView mCalendarView;
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
        Log.d(TAG, "onCreate: called");


        //todo load preferences earlier to reduce lag
        if (getActivity() == null) return;
        sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);

        if(getContext() == null) return;


        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        new CalendarPresenter(Injection.provideRepository(getContext()),
                this, AppExecutors.getInstance());
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: called");
        mPresenter.start();
    }

    @Override
    public void updateOrAddTarget(Target target) {
        int i = mAdapter.getPosition(target);
        mAdapter.remove(target);

        if (i == -1) Log.d(TAG, "updateOrAddTarget: does not exist in adapter. Placing at bottom");
        i = (i == -1) ? mAdapter.getCount() : i;

        mAdapter.insert(target, i);
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

        mCalendarView = fragmentScreen.findViewById(R.id.calendar_view);

        mCalendarView.setBackgroundColor(getResources().getColor(R.color.appBG));
        mCalendarView.setOnForwardPageChangeListener(pageChangeListener);
        mCalendarView.setOnPreviousPageChangeListener(pageChangeListener);

        try {
            mCalendarView.setDate(Calendar.getInstance());
        } catch (OutOfDateRangeException e) {
            Log.e(TAG, "onCreateView: error, invalid date range");
        }


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
    public void deleteTarget(Target target) {
        Log.d(TAG, "deleteTarget: called with " + target.getTargetTitle());
        if(target.isRollingTarget() && target.getInterval().equals("DAY")){
            int position = mAdapter.getPosition(target);

            if(position < 0){
                Log.e(TAG, "deleteTarget: no target to delete");
                return;
            }

            mAdapter.remove(target);
            int[] stored = getStoredSpinnerSelections(sharedPref);

            editor = sharedPref.edit();
            if(stored[0] > position) {
                editor.putInt( getString(R.string.calendar_saved_target_1), stored[0] - 1);
            } else if (stored[0] == position){
                editor.putInt( getString(R.string.calendar_saved_target_1), 0);
            }
            if(stored[1] > position) {
                editor.putInt( getString(R.string.calendar_saved_target_2), stored[1] - 1);
            } else if (stored[1] == position){
                editor.putInt( getString(R.string.calendar_saved_target_2), 0);
            }
            if(stored[2] > position) {
                editor.putInt( getString(R.string.calendar_saved_target_3), stored[2] - 1);
            } else if (stored[2] == position){
                editor.putInt( getString(R.string.calendar_saved_target_3), 0);
            }
            editor.apply();
        }
    }

//    @Override
//    public void updateCalendar(final List<Calendar> firstTargetDays, final List<Calendar> secondTargetDays, final List<Calendar> thirdTargetDays) {
//
//        final ArrayList<EventDay> days = new ArrayList<>();
//
//        if(getActivity() == null) return;
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                CalendarView calendarView =
//                        fragmentScreen.findViewById(R.id.calendar_view);
//                calendarView.setEvents(days);
//            }
//        });
//
//
//        if(firstTargetDays != null){
//            Log.d(TAG, "updateCalendar: firstTarget number of days met = " + firstTargetDays.size());
//            for(Calendar c : firstTargetDays){
//                days.add(new EventDay(c, R.drawable.calendar_square_1));
//            }
//        }else{
//            Log.d(TAG, "updateCalendar: first = null");
//        }
//
//        if(secondTargetDays != null){
//            Log.d(TAG, "updateCalendar: secondTarget number of days met = " + secondTargetDays.size());
//            for(Calendar c : secondTargetDays){
//                days.add(new EventDay(c, R.drawable.calendar_square_2));
//            }
//        }else{
//            Log.d(TAG, "updateCalendar: second = null");
//        }
//
//        if(thirdTargetDays != null){
//            Log.d(TAG, "updateCalendar: thirdTarget number of days met = " + thirdTargetDays.size());
//            for(Calendar c : thirdTargetDays){
//                days.add(new EventDay(c, R.drawable.calendar_square_3));
//            }
//        }else{
//            Log.d(TAG, "updateCalendar: third = null");
//        }
//
//
//        if(getActivity() == null) return;
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                CalendarView calendarView =
//                        fragmentScreen.findViewById(R.id.calendar_view);
//                calendarView.setEvents(days);
//            }
//        });
//
//
//    }


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

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public Calendar getCalendarViewMonth() {
        return mCalendarView.getCurrentPageDate();
    }

    @Override
    public void addDayIcon(EventDay day) {
        mCalendarView.addEventDay(day);
    }

    @Override
    public void clearDayIcons() {
        mCalendarView.clearEvents();
    }

    @Override
    public void refreshCalendarView() {
        mCalendarView.notifyDataSetChanged();
    }

    @Override
    public int getTarget1ResourceId() {
        return R.drawable.calendar_square_1;
    }

    @Override
    public int getTarget2ResourceId() {
        return R.drawable.calendar_square_2;
    }

    @Override
    public int getTarget3ResourceId() {
        return R.drawable.calendar_square_3;
    }


    @Override
    public void setTargetSpinners() {

        storedSpinnerPositions = getStoredSpinnerSelections(sharedPref);

        Log.d(TAG, "setTargetSpinners: adapter count = " + mAdapter.getCount());
        for(int i = 0; i < storedSpinnerPositions.length; i++){
            if(storedSpinnerPositions[i] > mAdapter.getCount()) storedSpinnerPositions[i] = 0;
        }

        mSpinner1.setSelection(storedSpinnerPositions[0]);
        mSpinner2.setSelection(storedSpinnerPositions[1]);
        mSpinner3.setSelection(storedSpinnerPositions[2]);

    }

    @NonNull
    private int[] getStoredSpinnerSelections(SharedPreferences sharedPref) {

        int[] setSpinnerSelections = new int[3];
        setSpinnerSelections[0] = sharedPref.getInt(getString(R.string.calendar_saved_target_1), 0);
        setSpinnerSelections[1] = sharedPref.getInt(getString(R.string.calendar_saved_target_2), 0);
        setSpinnerSelections[2] = sharedPref.getInt(getString(R.string.calendar_saved_target_3), 0);

        Log.d(TAG, "getStoredSpinnerSelections: stored_1 = " + setSpinnerSelections[0]);
        Log.d(TAG, "getStoredSpinnerSelections: stored_2 = " + setSpinnerSelections[1]);
        Log.d(TAG, "getStoredSpinnerSelections: stored_3 = " + setSpinnerSelections[2]);


        return setSpinnerSelections;
    }



    private AdapterView.OnItemSelectedListener  oisListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.d(TAG, "onItemSelected: called");

                    boolean change = true;
                    int old = -1;

                    editor = sharedPref.edit();
                    switch (adapterView.getId()){
                        case R.id.calendar_spinner_1:
                            Log.d(TAG, "onItemSelected: storing spinner 1 selected pos = " + position
                                    + " in spinner1 pref");
                            old = sharedPref.getInt(getString(R.string.calendar_saved_target_1), -1);
                            if(old == position) change = false;
                            editor.putInt( getString(R.string.calendar_saved_target_1), position);
                            break;
                        case R.id.calendar_spinner_2:
                            Log.d(TAG, "onItemSelected: storing spinner 2 selected pos = " + position
                                    + " in spinner2 pref");
                            old = sharedPref.getInt(getString(R.string.calendar_saved_target_2), -1);
                            if(old == position) change = false;
                            editor.putInt( getString(R.string.calendar_saved_target_2), position);
                            break;
                        case R.id.calendar_spinner_3:
                            Log.d(TAG, "onItemSelected: storing spinner 3 selected pos = " + position
                                    + " in spinner3 pref");
                            old = sharedPref.getInt(getString(R.string.calendar_saved_target_3), -1);
                            if(old == position) change = false;
                            editor.putInt( getString(R.string.calendar_saved_target_3), position);
                            break;
                        default:
                            Log.e(TAG, "onItemSelected: can't match spinner id");
                            change = false;
                    }
                    editor.apply();
                    if(change) mPresenter.loadCalendar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };

    private OnCalendarPageChangeListener pageChangeListener = new OnCalendarPageChangeListener() {
        @Override
        public void onChange() {
            Log.d(TAG, "onChange:  called, calendar page changed");
            mPresenter.loadCalendar();
        }
    };

}
