package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddTrackerActivity extends AppCompatActivity {

    public final String TAG = "ADD_TRACKER_ACTIVITY";
    private DataWrapper dataWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tracker);
        dataWrapper = new DataWrapper();

        EditText nameInput = findViewById(R.id.add_tracker_name_etv);
        nameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    addButtonClicked(null);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        //To stop numberPassword mode from hiding text
        EditText hours_input = findViewById(R.id.add_custom_count_etv);
        hours_input.setTransformationMethod(null);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    public void addButtonClicked(View view) {
        EditText nameETV = findViewById(R.id.add_tracker_name_etv);
        String newName = nameETV.getText().toString();

        Log.d(TAG, "addButtonClicked: " + newName);
        if(newName.equals("")){
            Toast.makeText(this, "Name can't be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        int difficulty = 0;
        RadioButtonTableLayout difficulty_buttons = findViewById(R.id.radio_button_table);
        switch (difficulty_buttons.getCheckedRadioButtonId()){
            case R.id.rad1_20:
                difficulty = 20;
                break;
            case R.id.rad2_50:
                difficulty = 50;
                break;
            case R.id.rad3_100:
                difficulty = 100;
                break;
            case R.id.rad4_500:
                difficulty = 500;
                break;
            case R.id.rad5_1000:
                difficulty = 1000;
                break;
            case R.id.rad6_lifetime:
                difficulty = 10000;
                break;
            case R.id.rad7_custom:
                EditText hours_input = findViewById(R.id.add_custom_count_etv);
                String input = hours_input.getText().toString();
                if(input.equals("")){
                    Toast.makeText(this, "You must enter a time!", Toast.LENGTH_LONG).show();
                    // todo replace with border
                    hours_input.setBackgroundColor(getResources().getColor(R.color.colorError));
                    return;
                }
                difficulty = Integer.parseInt(hours_input.getText().toString());
                if(difficulty < 1){
                    Toast.makeText(this, "Custom time must be greater than 1!", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case R.id.rad8_none:
                difficulty = -1;
                break;
            default:
                Toast.makeText(this, "You must select a time!", Toast.LENGTH_LONG).show();
                return;

        }


        Tracker d = new Tracker(newName, difficulty, 0);
        addTracker(d);
        finish();
    }

    private void addTracker(Tracker d) {
        ArrayList<Tracker> trackers = dataWrapper.readTrackers(this);
        trackers.add(d);
        dataWrapper.writeTrackers(this, trackers);
    }
}
