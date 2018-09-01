package com.fraserbrooks.progresstracker.trackerDetailsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Tracker;

public class TrackerDetailsActivity extends AppCompatActivity implements TrackerDetailsContract.View{

    private final String TAG = "TrackerDetailsActivity";
    private TrackerDetailsContract.Presenter mPresenter;
    private Tracker mTracker;



    @Override
    public void setPresenter(TrackerDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        setContentView(R.layout.activity_tracker_details);

        new TrackerDetailsPresenter(Injection.provideRepository(getApplicationContext()), this);

        Intent intent = getIntent();

        String mTrackerId = intent.getStringExtra("id");

        mPresenter.getTracker(mTrackerId);

        initButtons();

        initEditTexts();

        initSpinner();

    }

    private void initSpinner() {

        final View customDifficultyLayout = findViewById(R.id.custom_difficulty_layout);
        customDifficultyLayout.setVisibility(View.GONE);

        Spinner maxCountSpinner = findViewById(R.id.max_count_spinner);
        maxCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);

                if(selected.equals(getResources().getString(R.string.custom))){
                    customDifficultyLayout.setVisibility(View.VISIBLE);
                }else{
                    customDifficultyLayout.setVisibility(View.VISIBLE);
                    mPresenter.newMaxCountSelected(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // DO Nothing
            }
        });

    }

    private void initEditTexts() {

        EditText nameEditText = findViewById(R.id.name_etv);
        nameEditText.setText(mTracker.getTitle());
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.newTrackerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do Nothing
            }
        });

        EditText labelEditText = findViewById(R.id.label_etv);
        labelEditText.setText(mTracker.getCounterLabel());
        labelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.newTrackerLabel(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do Nothing
            }
        });

    }

    private void initButtons() {

        //Get buttons
        Button topButton1 = findViewById(R.id.top_button_1);
        Button topButton2 = findViewById(R.id.top_button_2);
        Button topButton3 = findViewById(R.id.top_button_3);
        Button topButton4 = findViewById(R.id.top_button_4);


        // Timer button
        if(mTracker.isCurrentlyTiming()){
            topButton1.setText(R.string.end_timer);
            topButton1.setTextColor(getResources().getColor(R.color.colorAccent));
        }else{
            topButton1.setText(R.string.start_timer);
            topButton1.setTextColor(getResources().getColor(R.color.default_text_color));
        }
        topButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.timerButtonClicked();
            }
        });

        final int button2AddAmount;
        final int button3AddAmount;

        if(mTracker.isTimeTracker()){

            //Show timer button
            topButton1.setVisibility(View.VISIBLE);

            topButton2.setText(R.string.plus_15_minutes);
            topButton3.setText(R.string.plus_1_hour);

            button2AddAmount = 15;
            button3AddAmount = 60;

        }else{

            //hide timer button
            topButton1.setVisibility(View.GONE);

            topButton2.setText(getResources()
                    .getString(R.string.add_1_count,mTracker.getCounterLabel()));
            topButton3.setText(getResources()
                    .getString(R.string.add_5_count,mTracker.getCounterLabel()));


            button2AddAmount = 1;
            button3AddAmount = 5;

        }

        topButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addToTrackerScore(button2AddAmount);
            }
        });

        topButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addToTrackerScore( button3AddAmount);
            }
        });


        topButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View customTimeLayout = findViewById(R.id.custom_time_layout);

                if(customTimeLayout.getVisibility() == View.VISIBLE){
                    customTimeLayout.setVisibility(View.GONE);
                }else{
                    customTimeLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        // Get buttons
        Button subButton = findViewById(R.id.sub_button);
        Button addButton = findViewById(R.id.add_button);

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.subButtonClicked();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addButtonClicked();
            }
        });

        // Get Button
        Button updateDifficultyButton = findViewById(R.id.update_difficulty_button);
        updateDifficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateDifficultyButtonClicked();
            }
        });

    }


    @Override
    public int getDecrementValue() {
        return 0;
    }

    @Override
    public int getIncrementValue() {
        return 0;
    }

    @Override
    public String getNewName() {
        return null;
    }

    @Override
    public String getNewLabel() {
        return null;
    }

    @Override
    public void returnToTrackersScreen() {

    }

    @Override
    public void setTracker(Tracker t) {
        mTracker = t;
    }


}
