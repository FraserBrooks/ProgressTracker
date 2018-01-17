package com.fraserbrooks.progresstracker;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarScreenFragment extends Fragment {

    private final String TAG = "Main>CalendarScreenFrag";
    private DataWrapper dataWrapper;
    private ArrayList<Target> dayTargets;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private ViewGroup fragmentScreen;

    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;

    private CalendarView calendarView;

    AdapterView.OnItemSelectedListener  oisListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    String selected = (String) adapterView.getSelectedItem();
                    switch (adapterView.getId()){
                        case R.id.calendar_spinner_1:
                            Log.d(TAG, "onItemSelected: storing " + selected
                            + " in spinner1 pref");
                            editor.putString( getString(R.string.calendar_target_1), selected);
                            break;
                        case R.id.calendar_spinner_2:
                            Log.d(TAG, "onItemSelected: storing " + selected
                                    + " in spinner2 pref");
                            editor.putString( getString(R.string.calendar_target_2), selected);
                            break;
                        case R.id.calendar_spinner_3:
                            Log.d(TAG, "onItemSelected: storing " + selected
                                    + " in spinner3 pref");
                            editor.putString( getString(R.string.calendar_target_3), selected);
                            break;
                        default:
                            Log.e(TAG, "onItemSelected: can't match spinner id");
                    }
                    editor.commit();
                    updateCalendar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };

    public CalendarScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataWrapper = new DataWrapper();
        // Inflate the layout for this fragment
        fragmentScreen = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_screen,
                        container, false);
        sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        updateDayTargets();
        setSpinners();

        calendarView =
                fragmentScreen.findViewById(R.id.calendar_view);

        calendarView.setBackgroundColor(getResources().getColor(R.color.appBG));

        return fragmentScreen;
    }

    private void setSpinners() {
        String[] stored_target_titles = getStoredTargetTitles(sharedPref);
        ArrayList<String> target_titles = getTargetTitles();
        // Target may have been deleted since storing it in the sharedPref
        for(String name: stored_target_titles){
            if(!target_titles.contains(name)){
                name = "None";
            }
        }
        target_titles.add("None");

        spinner1 = fragmentScreen.findViewById(R.id.calendar_spinner_1);
        spinner2 = fragmentScreen.findViewById(R.id.calendar_spinner_2);
        spinner3 = fragmentScreen.findViewById(R.id.calendar_spinner_3);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                target_titles);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);


        spinner1.setOnItemSelectedListener(oisListener);
        spinner2.setOnItemSelectedListener(oisListener);
        spinner3.setOnItemSelectedListener(oisListener);

        Log.d(TAG, "setSpinners: set target names read from prefs:\n"
        + stored_target_titles[0]
        + stored_target_titles[1]
        + stored_target_titles[2]);


        spinner1.setSelection(adapter.getPosition(stored_target_titles[0]));
        spinner2.setSelection(adapter.getPosition(stored_target_titles[1]));
        spinner3.setSelection(adapter.getPosition(stored_target_titles[2]));
    }

    @NonNull
    private ArrayList<String> getTargetTitles() {
        ArrayList<String> target_titles = new ArrayList<>();
        for (Target t: dayTargets) {
            target_titles.add(t.getTargetTitle());
        }
        return target_titles;
    }

    @NonNull
    private String[] getStoredTargetTitles(SharedPreferences sharedPref) {
        String[] set_target_names = new String[3];

        set_target_names[0] = sharedPref.
                getString(getString(R.string.calendar_target_1), "None");
        set_target_names[1] = sharedPref.
                getString(getString(R.string.calendar_target_2), "None");
        set_target_names[2] = sharedPref.
                getString(getString(R.string.calendar_target_3), "None");
        return set_target_names;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDayTargets();
        setSpinners();
        updateCalendar();
        Log.d(TAG, "onResume: called");
    }


    private void updateCalendar(){
        List<EventDay> events = new ArrayList<>();

        String selected = (String) spinner1.getSelectedItem();
        Log.d(TAG, "onCreateView: selected1:" + selected);
        if(!selected.equals("None")){
            events.addAll(getEventDays(selected,
                    R.drawable.calendar_square_1));
        }
        selected = (String) spinner2.getSelectedItem();
        Log.d(TAG, "onCreateView: selected2:" + selected);
        if(!selected.equals("None")){
            events.addAll(getEventDays(selected,
                    R.drawable.calendar_square_2));
        }
        selected = (String) spinner3.getSelectedItem();
        Log.d(TAG, "onCreateView: selected3:" + selected);
        if(!selected.equals("None")){
            events.addAll(getEventDays(selected,
                    R.drawable.calendar_square_3));
        }

        calendarView.setEvents(events);
    }

    private void updateDayTargets(){

        ArrayList<Target> targets = dataWrapper.readTargets(getContext());
        for (Target target: targets) {
            if(target instanceof RollingTarget){
                if(((RollingTarget) target).getRolloverTime() != Target.DAY){
                    targets.remove(target);
                }
            }else{
                targets.remove(target);
            }
        }

        dayTargets = targets;
    }

    private ArrayList<EventDay> getEventDays(String target_title,
                                             int resourceID){
        ArrayList<EventDay> days = new ArrayList<>();
        Target target = null;
        for (Target t: dayTargets) {
            if(t.getTargetTitle().equals(target_title)){
                target = t;
            }
        }
        if(target == null){
            //this should never happen
            throw new InvalidParameterException("Invalid target selected");
        }

        ArrayList<Calendar> calendars
                = ((RollingTarget) target).getDaysCompleted();

        for (Calendar c: calendars) {
            days.add(new EventDay(c, resourceID));
        }

        return days;

    }




}
