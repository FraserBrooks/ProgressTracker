package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Fraser on 26/12/2017.
 */

public class TrackerAdapter extends ArrayAdapter<Tracker> {

    private final String TAG = "main>TrackerAdapter";
    private final BarGraph barGraph;
    private DataWrapper dataWrapper;
    private ViewGroup.LayoutParams defaultParams;

    public TrackerAdapter(@NonNull Context context) {
        super(context, 0);
        this.dataWrapper = new DataWrapper();
        this.barGraph = new BarGraph(getContext());
        this.defaultParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        refreshItems();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(position == 0){
            //todo add other graphs?
            barGraph.setLayoutParams(defaultParams);;
            return barGraph;
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || //don't reuse barGraph view
                convertView.findViewById(R.id.expanded_dabble_layout) == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tracker_item,
                    parent, false);
        }else{
            convertView.setLayoutParams(defaultParams);
        }


        final Tracker tracker = getItem(position);

        //Top constraint layout = collapse button
        ViewGroup topLayout = convertView.findViewById(R.id.expanded_tracker_layout_top);
        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.collapse();
                notifyDataSetChanged();
            }
        });

        inflateDabble(convertView, tracker);
        return convertView;
    }

    public void refreshItems(){
        clear();
        add(null);// for graph View
        setItems(dataWrapper.readTrackers(getContext()));
    }

    public void setItems(ArrayList<Tracker> items) {
        clear();
        if(items.get(0) != null){
            add(null);
        }
        addAll(items);
        notifyDataSetChanged();
        updateGraph();
    }


    public ArrayList<Tracker> getItems(){
        ArrayList<Tracker> ls = new ArrayList<Tracker>();
        for(int i=1/*1 to skip graph view*/ ; i<getCount() ; i++){
            ls.add(getItem(i));
        }
        return ls;
    }


    public void inflateDabble(View view, Tracker tracker){

        float progress = tracker.getProgressPercentage();
        int level = tracker.getLevel();
        String name = tracker.getName();
        String timeString;
        String hours = " - " + (tracker.getMinutes()/60) + " hours";
        String minutes = " - " + tracker.getMinutes() + " minutes";
        if(tracker.getMinutes() > 59){
            timeString = hours;
        } else{
            timeString = minutes;
        }

        //Make sure correct view is showing
        View smallLayout = view.findViewById(R.id.small_tracker_layout);
        View expandedLayout = view.findViewById(R.id.expanded_dabble_layout);

        //Progress bar layouts
        LinearLayout.LayoutParams param_colour = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                progress
        );
        LinearLayout.LayoutParams param_white = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (1f - progress)
        );

        TextView levelIndicator = view.findViewById(R.id.text_level);
        TextView levelIndicatorExpanded = view.findViewById(R.id.expanded_text_level);
        if (tracker.getLevel() > 8 || (tracker.isNoDifficulty() && tracker.getLevel() > 0)){
            int l = tracker.getLevel();
            l = (l < 9) ? l : l-8; //don't subtract 8 if difficulty is not important
            String levelString = "" + l;
            levelIndicator.setText(levelString);
            levelIndicatorExpanded.setText(levelString);
        } else{
            levelIndicator.setText("");
            levelIndicatorExpanded.setText("");
        }

        if(!tracker.isExpanded()){
            // Small view Data Population
            expandedLayout.setVisibility(View.GONE);
            smallLayout.setVisibility(View.VISIBLE);

            //Get Widgets
            TextView tvName = view.findViewById(R.id.name_tv);
            TextView tvTime = view.findViewById(R.id.quantifier_tv);
            TextView tvColourRect = view.findViewById(R.id.filled_rect);
            TextView tvWhiteRect = view.findViewById(R.id.not_filled_rect);
            ImageView gemImage = view.findViewById(R.id.gem_bitmap);

            tracker.setColourAndIcon(gemImage, tvColourRect);
            tvName.setText(name);
            tvTime.setText(timeString);
            tvColourRect.setLayoutParams(param_colour);
            tvWhiteRect.setLayoutParams(param_white);
        } else {
            // Expanded View Data Population
            expandedLayout.setVisibility(View.VISIBLE);
            smallLayout.setVisibility(View.GONE);

            //Get Widgets
            TextView expanded_tvName = view.findViewById(R.id.expanded_name_tv);
            TextView minutesTV = view.findViewById(R.id.expanded_quantifier_two_tv);
            TextView hoursTV = view.findViewById(R.id.expanded_quantifier_one_tv);
            TextView timeToBlackTV = view.findViewById(R.id.expanded_count_to_max_tv);
            TextView expanded_tvColourRect = view.findViewById(R.id.expanded_filled_rect);
            TextView expanded_tvWhiteRect = view.findViewById(R.id.expanded_not_filled_rect);
            ImageView expanded_gemImage = view.findViewById(R.id.expanded_gem_bitmap);

            hoursTV.setText(hours);
            expanded_tvName.setText(name);
            minutesTV.setText(minutes);
            tracker.setColourAndIcon(expanded_gemImage, expanded_tvColourRect);
            expanded_tvColourRect.setLayoutParams(param_colour);
            expanded_tvWhiteRect.setLayoutParams(param_white);

            int hoursToBlack = tracker.getMinutesToMaxLevel() / 60;

            timeToBlackTV.setText(" /" + hoursToBlack + " to master");

            setListeners(view, tracker);
        }
        return;
    }

    private void setListeners(View view, final Tracker tracker){


        // Edit Name
        final EditText etvName = view.findViewById(R.id.name_etv);
        etvName.setHint(tracker.getName());
        final Button eNameButton = view.findViewById(R.id.edit_name_button);
        eNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              ViewGroup buttonLayout = (ViewGroup) v.getParent();
//              EditText eNameText = buttonLayout.findViewById(R.id.name_etv);
                String newName = etvName.getText().toString();
                tracker.setName(newName);
                clearEditTextView(etvName);
                etvName.setHint(newName);
                updateGraph();
            }
        });
        etvName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    eNameButton.callOnClick();
                    return true;
                }
                return false;
            }
        });

        //delete tracker
        Button deleteButton = view.findViewById(R.id.bottom_button_1);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(tracker);
                updateGraph();
            }
        });

        final View input_layout = view.findViewById(R.id.custom_max_count_layout);
        final EditText hours_input = view.findViewById(R.id.add_custom_count_etv);
        hours_input.setText(Integer.toString(tracker.getMinutesToMaxLevel()/60));
        hours_input.setTransformationMethod(null);

        // Difficulty setting
        Spinner difficulty_spinner = view.findViewById(R.id.max_count_spinner);
        switch (tracker.getMinutesToMaxLevel()/60){
            case 20:
                difficulty_spinner.setSelection(0);
                break;
            case 50:
                difficulty_spinner.setSelection(1);
                break;
            case 100:
                difficulty_spinner.setSelection(2);
                break;
            case 500:
                difficulty_spinner.setSelection(3);
                break;
            case 1000:
                difficulty_spinner.setSelection(4);
                break;
            case 10000:
                difficulty_spinner.setSelection(5);
                break;
            default:
                difficulty_spinner.setSelection(7);
                input_layout.setVisibility(View.VISIBLE);
                break;
        }

        if (tracker.isNoDifficulty()){
            difficulty_spinner.setSelection(6);
        }

        difficulty_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getItemAtPosition(position).toString()){
                    case "20 Hours":
                        tracker.setMinutesToMaxLevel(20 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "50 Hours":
                        tracker.setMinutesToMaxLevel(50 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "100 Hours":
                        tracker.setMinutesToMaxLevel(100 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "500 Hours":
                        tracker.setMinutesToMaxLevel(500 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "1,000 Hours":
                        tracker.setMinutesToMaxLevel(1000 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "Lifetime (10,000 Hours)":
                        tracker.setMinutesToMaxLevel(10000 * 60);
                        tracker.setNoDifficulty(false);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "Doesn\'t Matter":
                        tracker.setMinutesToMaxLevel(-1);
                        tracker.setNoDifficulty(true);
                        input_layout.setVisibility(View.GONE);
                        break;
                    case "Custom":
                        input_layout.setVisibility(View.VISIBLE);
                        return;
                }
                updateGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button commitCustomTime = view.findViewById(R.id.commit_custom_max_count_button);
        commitCustomTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = hours_input.getText().toString();
                if(input.equals("")){
                    Toast.makeText(getContext(), "You must enter a time!", Toast.LENGTH_LONG).show();
                    // todo replace with border
                    hours_input.setBackgroundColor(getContext().getResources().getColor(R.color.colorError));
                    return;
                }
                int difficulty = Integer.parseInt(hours_input.getText().toString());
                if(difficulty < 1){
                    Toast.makeText(view.getContext(), "Custom time must be greater than 1!", Toast.LENGTH_LONG).show();
                    return;
                }
                tracker.setMinutesToMaxLevel(difficulty * 60);
                tracker.setNoDifficulty(false);
                clearSoftKeyboard(hours_input);
                updateGraph();
            }
        });


        hours_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    commitCustomTime.callOnClick();
                    return true;
                }
                return false;
            }
        });

        //findViews of time buttons
        Button addOneHourButton = view.findViewById(R.id.top_button_3);
        Button add15Minutes = view.findViewById(R.id.top_button_2);
        final View addTimeInputLayout = view.findViewById(R.id.inputs_for_custom_add_layout);
        Button showAddCustomLayoutButton = view.findViewById(R.id.top_button_4);
        final EditText inputHours = view.findViewById(R.id.add_hour_etv);
        final EditText inputMinutes = view.findViewById(R.id.add_minutes_etv);
        final Button addCustomButton = view.findViewById(R.id.add_time_from_text_button);

        Button subOneHourButton = view.findViewById(R.id.bottom_button_3);
        Button sub15Minutes = view.findViewById(R.id.bottom_button_2);
        final View subTimeInputLayout = view.findViewById(R.id.inputs_for_custom_subtract_layout);
        Button showSubCustomLayoutButton = view.findViewById(R.id.bottom_button_4);
        final EditText inputSubHours = view.findViewById(R.id.sub_hour_etv);
        final EditText inputSubMinutes = view.findViewById(R.id.sub_minutes_etv);
        final Button subCustomButton = view.findViewById(R.id.sub_time_from_text_button);

        //todo: find a better way to do this if possible
        // to stop focus going to empty EditTexts
        configEditText(inputHours);
        configEditText(inputMinutes);
        configEditText(etvName);
        configEditText(inputSubHours);
        configEditText(inputSubMinutes);

        // add time button
        inflateTimeButtons(tracker, addOneHourButton, add15Minutes,
                addTimeInputLayout, showAddCustomLayoutButton,
                inputHours, inputMinutes, addCustomButton, 1);

        // sub time buttons
        inflateTimeButtons(tracker, subOneHourButton, sub15Minutes,
                subTimeInputLayout, showSubCustomLayoutButton,
                inputSubHours, inputSubMinutes, subCustomButton, (-1));

        // start timer button
        Button timerButton = view.findViewById(R.id.top_button_1);
        if(tracker.isBeingTimed()){
            //todo replace with animation or graphic
            timerButton.setText(R.string.end_timer);
            timerButton.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            timerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tracker.endTimerCount();
                    //barGraph.refresh(getItems());
                    notifyDataSetChanged();
                }
            });
        }else{
            timerButton.setText(R.string.start_timer);
            timerButton.setTextColor(getContext().getResources().getColor(R.color.default_text_color));
            timerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tracker.setTimer(System.currentTimeMillis());
                    notifyDataSetChanged();
                }
            });
        }

    }

    private void inflateTimeButtons(final Tracker tracker,
                                    Button oneHourButton,
                                    Button _15MinutesButton,
                                    final View timeInputLayout,
                                    Button showCustomLayoutButton,
                                    final EditText inputHours,
                                    final EditText inputMinutes,
                                    final Button commitCustomButton,
                                    final int sign) {
        // 1 hour
        oneHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.addMinutes(60 * sign);
                updateGraph();
            }
        });
        // Add 15 minutes
        _15MinutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.addMinutes(15 * sign);
                updateGraph();
            }
        });

        // Add custom amount of time button
        showCustomLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeInputLayout.getVisibility() == View.GONE){
                    timeInputLayout.setVisibility(View.VISIBLE);
                } else {
                    timeInputLayout.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        });

        // Stop xml numberPassword setting from obscuring text
        inputHours.setTransformationMethod(null);
        inputMinutes.setTransformationMethod(null);

        // Commit custom time button
        commitCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minutesString = inputMinutes.getText().toString();
                String hoursString = inputHours.getText().toString();
                int minutes = 0;
                if(!minutesString.equals("")){
                    minutes += Integer.parseInt(minutesString);
                }
                if(!hoursString.equals("")){
                    minutes += (Integer.parseInt(hoursString)*60);
                }
                tracker.addMinutes(minutes * sign);

                clearEditTextView(inputMinutes);
                clearEditTextView(inputHours);
                updateGraph();
            }
        });

        inputMinutes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    commitCustomButton.callOnClick();
                    clearSoftKeyboard(textView);
                    return true;
                }
                return false;
            }
        });
    }

    private void configEditText(final EditText et){
        final LinearLayout parent = (LinearLayout) et.getParent();
        if(!parent.isFocused()){
            parent.setFocusable(false);
            parent.setFocusableInTouchMode(false);
        }
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.setFocusable(true);
                parent.setFocusableInTouchMode(true);
                et.requestFocus();
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void updateGraph(){
        //barGraph.refresh(getItems());
        notifyDataSetChanged();
    }

    private void clearEditTextView(EditText tv){
        tv.setText("");
        tv.clearFocus();
        clearSoftKeyboard(tv);
    }

    private void clearSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }



}
