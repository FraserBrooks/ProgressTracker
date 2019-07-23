package com.fraserbrooks.progresstracker.targetscalendar;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.calendar.CalendarView;
import com.fraserbrooks.progresstracker.calendar.OnCalendarPageChangeListener;
import com.fraserbrooks.progresstracker.targets.domain.model.Target;
import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class CalendarFragment extends Fragment implements CalendarContract.View {

    private final String TAG = "CalendarFragment";

    private CalendarView mCalendarView;
    private ArrayAdapter<Target> mAdapter;

    private Spinner mSpinner1;
    private Spinner mSpinner2;
    private Spinner mSpinner3;

    private CalendarContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        if(getContext() == null) return;


        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        new CalendarPresenter(this);


    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        // Inflate the layout for this fragment
        ViewGroup fragmentScreen = (ViewGroup) inflater.inflate(R.layout.frag_calendar,
                container, false);

        mCalendarView = fragmentScreen.findViewById(R.id.calendar_view);

        mCalendarView.setOnForwardPageChangeListener(pageChangeListener);
        mCalendarView.setOnPreviousPageChangeListener(pageChangeListener);

        mSpinner1 = fragmentScreen.findViewById(R.id.calendar_spinner_1);
        mSpinner2 = fragmentScreen.findViewById(R.id.calendar_spinner_2);
        mSpinner3 = fragmentScreen.findViewById(R.id.calendar_spinner_3);

        mSpinner1.setTag(1);
        mSpinner2.setTag(2);
        mSpinner3.setTag(3);

        mSpinner1.setAdapter(mAdapter);
        mSpinner2.setAdapter(mAdapter);
        mSpinner3.setAdapter(mAdapter);


        mSpinner1.setOnItemSelectedListener(oisListener);
        mSpinner2.setOnItemSelectedListener(oisListener);
        mSpinner3.setOnItemSelectedListener(oisListener);

        mPresenter.start();

        return fragmentScreen;
    }

    @Override
    public void updateOrAddTarget(Target target) {

        // Only add DAILY Targets to spinner options
        if(!target.isRollingTarget() || target.getInterval() != Target.EVERY_DAY) return;

        int i = mAdapter.getPosition(target);
        mAdapter.remove(target);

        if (i == -1) Log.d(TAG, "updateOrAddTarget: does not exist in adapter. Placing at bottom");
        i = (i == -1) ? mAdapter.getCount() : i;

        mAdapter.insert(target, i);
        hideNoDataAvailable();
    }


    @Override
    public void removeTargetOption(Target target) {
        Log.d(TAG, "removeTargetOption: called with " + target.getTargetTitle());
        if(target.isRollingTarget() && target.getInterval() == Target.EVERY_DAY){
            int position = mAdapter.getPosition(target);

            if(position < 0){
                Log.e(TAG, "removeTargetOption: no target to delete");
                return;
            }

            mAdapter.remove(target);

            updateSpinner(mSpinner1);
            updateSpinner(mSpinner2);
            updateSpinner(mSpinner3);

            if(mAdapter.getCount() == 0){
                showNoDataAvailable();
            }
        }
    }

    private void updateSpinner(Spinner spinner){

        Target t = (Target) spinner.getSelectedItem();

        if(t == null){
            // No item selected so try and select the
            // first item in the spinner
            if(spinner.getCount() > 0){
                spinner.setSelection(0);
            }
        }

    }


    @Override
    public String tryToSetTargetSpinner(@NonNull UserSetting.Setting targetSpinnerNum,
                                        @Nullable  String targetIdFromSettings) {

        switch (targetSpinnerNum){
            case CALENDAR_TARGET_1:
                return setSpinner(mSpinner1, targetIdFromSettings);
            case CALENDAR_TARGET_2:
                return setSpinner(mSpinner2, targetIdFromSettings);
            case CALENDAR_TARGET_3:
                return setSpinner(mSpinner3, targetIdFromSettings);
        }

        return null;
    }

    /**
     * Set the spinner to the targetId the user has selected previously. If this target no longer
     * exist then default to the first item in the spinner.
     *
     * @param   targetId   -- targetId persisted in local storage from a previous session
     * @return    - the targetId that this spinner is now set to or null if there are no day targets
     */
    private String setSpinner(@NonNull Spinner spinner,@Nullable String targetId){

        for(int i = 0; i < spinner.getCount(); i++){
            Target t = (Target) spinner.getItemAtPosition(i);
            if(t.getId().equals(targetId)){
                if(!(spinner.getSelectedItemPosition() == i))spinner.setSelection(i);
                return t.getId();
            }
        }

        updateSpinner(spinner);

        if(spinner.getCount() < 1) return null;
        else{
            Target t = (Target) spinner.getSelectedItem();
            if(t != null) return t.getId();
            else return null;
        }
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
        // todo: no day targets at all
    }

    private void hideNoDataAvailable() {
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
    public void showCalendarLoading() {
        if(mCalendarView != null) mCalendarView.disableButtons();
    }

    @Override
    public void hideCalendarLoading() {
        if(mCalendarView != null) mCalendarView.enableButtons();
    }

    @Override
    public void setCalendarTargetDays(@NonNull UserSetting.Setting targetSpinnerNum,
                                      Set<Date> days) {
        switch (targetSpinnerNum){
            case CALENDAR_TARGET_1:
                mCalendarView.setTargetOneDays(days);
                break;
            case CALENDAR_TARGET_2:
                mCalendarView.setTargetTwoDays(days);
                break;
            case CALENDAR_TARGET_3:
                mCalendarView.setTargetThreeDays(days);
                break;
        }
    }

    @Override
    public void showNoTargetDays(@NonNull UserSetting.Setting targetSpinnerNum) {
        switch (targetSpinnerNum){
            case CALENDAR_TARGET_1:
                mCalendarView.setTargetOneDays(null);
                break;
            case CALENDAR_TARGET_2:
                mCalendarView.setTargetTwoDays(null);
                break;
            case CALENDAR_TARGET_3:
                mCalendarView.setTargetThreeDays(null);
                break;
        }
    }

    @Override
    public void calendarNotifyDataSetChange() {
        mCalendarView.notifyDataSetChanged();
    }

    @Override
    public void updateDateInCalendar(Date date) {
        mCalendarView.notifyDateChange(date);
    }

    @Override
    public void disableSpinnerSelectionListeners() {
        // Remove the listeners while the app is updating the spinner
        mSpinner1.setOnItemSelectedListener(null);
        mSpinner2.setOnItemSelectedListener(null);
        mSpinner3.setOnItemSelectedListener(null);
    }

    @Override
    public void enableSpinnerSelectionListeners() {
        // Reset spinner listeners to listen for user initiated changes
        mSpinner1.setOnItemSelectedListener(oisListener);
        mSpinner2.setOnItemSelectedListener(oisListener);
        mSpinner3.setOnItemSelectedListener(oisListener);
    }


    private AdapterView.OnItemSelectedListener  oisListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.d(TAG, "onItemSelected: called");

                    Target t = (Target) adapterView.getItemAtPosition(position);

                    switch (adapterView.getId()){
                        case R.id.calendar_spinner_1:
                            mPresenter.newTargetSelected(
                                    UserSetting.Setting.CALENDAR_TARGET_1,
                                    t.getId());
                            return;
                        case R.id.calendar_spinner_2:
                            mPresenter.newTargetSelected(
                                    UserSetting.Setting.CALENDAR_TARGET_2,
                                    t.getId());
                            return;
                        case R.id.calendar_spinner_3:
                            mPresenter.newTargetSelected(
                                    UserSetting.Setting.CALENDAR_TARGET_3,
                                    t.getId());
                            return;
                        default:
                            Log.e(TAG, "onItemSelected: can't match spinner id");
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };


    private OnCalendarPageChangeListener pageChangeListener = new OnCalendarPageChangeListener() {
        @Override
        public void onChange() {
            Log.d(TAG, "onChange:  called, calendar page changed");
            mPresenter.calendarPositionChanged();
        }
    };

}
