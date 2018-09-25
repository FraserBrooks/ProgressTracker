package com.fraserbrooks.progresstracker.addtrackeractivity;

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

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddTrackerActivity extends AppCompatActivity implements AddTrackerContract.View{

    private final String TAG = "AddTrackerActivity";

    private AddTrackerContract.Presenter mPresenter;

    @Override
    public void setPresenter(AddTrackerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        setContentView(R.layout.activity_add_tracker);

        new AddTrackerPresenter(Injection.provideRepository(getApplicationContext()), this);



        EditText nameInput = findViewById(R.id.add_tracker_name_etv);
        nameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    mPresenter.addTracker();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        //To stop numberPassword mode from hiding text
        EditText hours_input = findViewById(R.id.custom_max_count_etv);
        hours_input.setTransformationMethod(null);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }


    @Override
    public int getProgressionRate() {
        int maxScore = 0;
        RadioButtonTableLayout max_score_buttons = findViewById(R.id.radio_button_table);
        switch (max_score_buttons.getCheckedRadioButtonId()){
            case R.id.rad1_20:
                maxScore = 20 * 60;
                break;
            case R.id.rad2_50:
                maxScore = 50 * 60;
                break;
            case R.id.rad3_100:
                maxScore = 100 * 60;
                break;
            case R.id.rad4_500:
                maxScore = 500 * 60;
                break;
            case R.id.rad5_1000:
                maxScore = 1000 * 60;
                break;
            case R.id.rad6_lifetime:
                maxScore = 10000 * 60;
                break;
            case R.id.rad7_custom:
                EditText number_input = findViewById(R.id.custom_max_count_etv);
                String input = number_input.getText().toString();
                if(input.equals("")){
                    longToast("You must enter a number!");
                    // todo replace with border
                    number_input.setBackgroundColor(getResources().getColor(R.color.colorError));
                }
                maxScore = Integer.parseInt(number_input.getText().toString()) * 60;

                break;
            case R.id.rad8_none:
                maxScore = -1;
                break;
            default:
                break;
        }

        return maxScore;
    }

    @Override
    public String getNewTrackerName() {
        EditText nameETV = findViewById(R.id.add_tracker_name_etv);
        return nameETV.getText().toString();
    }

    @Override
    public void longToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }


    @Override
    public void backToTrackersScreen() {
        finish();

    }

    public void addButtonClicked(View view){
        mPresenter.addTracker();
    }



}
