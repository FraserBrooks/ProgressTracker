package com.fraserbrooks.progresstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddTargetActivity extends AppCompatActivity {

    public final String TAG = "main>addTargetActivity";
    private DataWrapper dataWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);
        dataWrapper = new DataWrapper();

        Spinner spinner = findViewById(R.id.add_target_tracker_name_spinner);
        ArrayList<Tracker> trackers = dataWrapper.readTrackers(this);
        ArrayList<String> names = new ArrayList<>();
        for (Tracker t: trackers) {
            names.add(t.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        names);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);



    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    public void addButtonClicked(View view) {

        Spinner spinner = findViewById(R.id.add_target_tracker_name_spinner);
        String trackerName = (String) spinner.getSelectedItem();

        Log.d(TAG, "addButtonClicked: ");

        EditText hoursInput = findViewById(R.id.add_target_hour_input_etv);
        String hoursString = hoursInput.getText().toString();

        EditText minutesInput = findViewById(R.id.add_target_minutes_input_etv);
        String minutesString = minutesInput.getText().toString();

        int hours = 0;
        int  minutes = 0;

        if(hoursString.isEmpty() && minutesString.isEmpty()){
            Toast.makeText(this, "You must enter a time!", Toast.LENGTH_LONG).show();
            return;
        }
        if(!hoursString.isEmpty()){
            hours = Integer.parseInt(hoursString);
        }
        if(!minutesString.isEmpty()){
            minutes = Integer.parseInt(minutesString);
        }

        Spinner periodSpinner = findViewById(R.id.rollover_period_spinner);
        int time = Target.DAY;
        switch ((String) periodSpinner.getSelectedItem()){
            case "DAY":
                time = Target.DAY;
                break;
            case "WEEK":
                time = Target.WEEK;
                break;
            case "MONTH":
                time = Target.MONTH;
                break;
            case "YEAR":
                time = Target.YEAR;
                break;
            default:
                Log.e(TAG, "addButtonClicked: unrecognized target period given ");
        }

        RollingTarget t = new RollingTarget((hours * 60) + minutes,
                time, trackerName);
        addTarget(t, trackerName);
        finish();
    }

    private void addTarget(Target t, String parentName) {

        ArrayList<Tracker> trackers = dataWrapper.readTrackers(this);
        for (Tracker tracker :trackers) {
            if(tracker.getName().equals(parentName)){
                Log.d(TAG, "addTarget: added target to: " + tracker.getName());
                tracker.addTarget(t);
                break;
            }
        }
        dataWrapper.writeTrackers(this, trackers);
    }
}
