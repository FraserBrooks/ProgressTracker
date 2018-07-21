package com.fraserbrooks.progresstracker.addTargetActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

public class AddTargetActivity extends AppCompatActivity implements AddTargetContract.View {

    public final String TAG = "AddTargetActivity";

    private AddTargetContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_target);

        new AddTargetPresenter(Injection.provideRepository(getApplicationContext()),
                this, AppExecutors.getInstance());

        mPresenter.start();
    }

    @Override
    public boolean newTargetIsRollingTarget() {
        // todo
        return true;
    }

    @Override
    public String getSingleNumberInput() {
        // todo
        return "";
    }

    @Override
    public String getHoursInput() {
        EditText hoursInput = findViewById(R.id.add_target_hour_input_etv);
        return hoursInput.getText().toString();
    }

    @Override
    public String getMinutesInput() {
        EditText minutesInput = findViewById(R.id.add_target_minutes_input_etv);
        return minutesInput.getText().toString();
    }

    @Override
    public String getPeriodInput() {
        Spinner periodSpinner = findViewById(R.id.rollover_period_spinner);
        return ((String) periodSpinner.getSelectedItem());
    }

    @Override
    public String getTrackerName() {
        Spinner spinner = findViewById(R.id.add_target_tracker_name_spinner);
        Log.d(TAG, "getTrackerName: name = " + ((String) spinner.getSelectedItem()) );
        return (String) spinner.getSelectedItem();
    }

    @Override
    public void showNoTrackers() {
        // todo
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
    public void setSpinner(List<String> trackerNames) {
        Spinner spinner = findViewById(R.id.add_target_tracker_name_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, trackerNames);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void longToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void backToTargetsScreen() {
        finish();
    }

    @Override
    public void setPresenter(AddTargetContract.Presenter presenter){
        mPresenter = presenter;
    }

    public void addButtonClicked(View view){
        mPresenter.addTarget();
    }

}
