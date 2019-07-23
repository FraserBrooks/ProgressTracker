package com.fraserbrooks.progresstracker.trackers;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.trackers.view.trackergraphs.TrackerTimeGraphView;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.dialogs.AddSubCustomInputDialog;
import com.fraserbrooks.progresstracker.dialogs.DialogCallerContract;
import com.fraserbrooks.progresstracker.dialogs.DifficultySelectorDialog;
import com.fraserbrooks.progresstracker.dialogs.TextInputDialog;
import com.fraserbrooks.progresstracker.dialogs.YesNoDialog;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;


public class TrackerDetailsActivity extends AppCompatActivity
        implements TrackerDetailsContract.View, DialogCallerContract{

    private final String TAG = "TrackerDetailsActivity";
    private TrackerDetailsContract.Presenter mPresenter;
    private Tracker mTracker;
    private DialogCallerContract mDialogCallback;


    @Override
    public void setPresenter(TrackerDetailsContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");

        setContentView(R.layout.activity_tracker_details);

        new TrackerDetailsPresenter(AppExecutors.getInstance(), this,
                Injection.provideUseCaseHandler(),
                Injection.provideGetTracker(this),
                Injection.provideIncrementTracker(this),
                Injection.provideStartStopTrackerTimer(this),
                Injection.provideClearRange(this),
                Injection.provideUpdateTracker(this),
                Injection.provideDeleteTracker(this));


        mPresenter.getTracker().observe(this, this::trackerChanged);

    }

    public void trackerChanged(Tracker tracker) {

        mTracker = tracker;

        initButtons();

        initTextViews();

        initClickListeners();

        initGraph();
    }


    private void initGraph(){

        TrackerTimeGraphView graph = findViewById(R.id.tracker_details_graph_view);

        graph.setGraphType(TrackerTimeGraphView.DAY);

        graph.setGraphBarResource(getResources().getDrawable(R.drawable.frag_trackers_heart_colour_rect));

        graph.initGraph(mTracker.getPastEightDaysCounts());

    }

    @Override
    public String getTrackerId() {
        return getIntent().getStringExtra("id");
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void returnToTrackersScreen() {
        finish();
    }

    @Override
    public void showNoNumberError() {
        Toast.makeText(this, R.string.must_enter_a_number, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showBlankNameError() {
        Toast.makeText(this, R.string.name_cannot_be_blank, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInvalidDifficultyError() {
        Toast.makeText(this, R.string.label_cannot_be_blank, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showTrackerLoadError() {
        // TODO
        Log.e(TAG, "showTrackerLoadError: Error loading tracker");
    }


    @Override
    public void onPositiveClicked(String string) {
        if(mDialogCallback != null) mDialogCallback.onPositiveClicked(string);
    }

    @Override
    public void onNegativeClicked() {
        if(mDialogCallback != null) mDialogCallback.onNegativeClicked();
    }

    @Override
    public String getPositiveButtonText() {
        if(mDialogCallback != null) return mDialogCallback.getPositiveButtonText();
        else return "";
    }

    @Override
    public String getNegativeButtonText() {
        if(mDialogCallback != null) return mDialogCallback.getNegativeButtonText();
        else return "";
    }

    @Override
    public String getTitleText() {
        return mDialogCallback.getTitleText();
    }

    @Override
    public String getDescriptiveText() {
        return mDialogCallback.getDescriptiveText();
    }

    @Override
    public Tracker getTracker() {
        return mTracker;
    }


    @Override
    public void returnInt(int amount) {

    }


    private void showNewNameDialog() {

        mDialogCallback = new DialogCallerContract() {
            @Override
            public void onPositiveClicked(String string) {
                if(string.isEmpty()) showBlankNameError();
                else mPresenter.newTrackerName(mTracker, string);
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
                // Do Nothing
            }

            @Override
            public String getPositiveButtonText() {
                return getString(R.string.Save);
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                // Not used
                return null;
            }

            @Override
            public String getDescriptiveText() {
                // Not used
                return null;
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // not used
            }
        };
        DialogFragment newNameDialog = new TextInputDialog();
        newNameDialog.show(getSupportFragmentManager(), "new_name");

    }

    private void showNewLabelDialog() {
        mDialogCallback = new DialogCallerContract(){
            @Override
            public void onPositiveClicked(String string) {
                if(string.isEmpty()) showBlankNameError();
                else mPresenter.newTrackerLabel(mTracker, string);
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
                // Do nothing
            }

            @Override
            public String getPositiveButtonText() {
                return getString(R.string.Save);
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                // Not used
                return null;
            }

            @Override
            public String getDescriptiveText() {
                // Not used
                return null;
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // not used
            }
        };
        DialogFragment newLabelDialog = new TextInputDialog();
        newLabelDialog.show(getSupportFragmentManager(), "new_label");
    }

    private void showNewDifficultyDialog() {
        mDialogCallback = new DialogCallerContract(){
            @Override
            public void onPositiveClicked(String difficulty) {
                if(difficulty.isEmpty()) showNoNumberError();
                else{
                    int i;
                    try{
                        i = Integer.parseInt(difficulty);
                    } catch (NumberFormatException e){
                        Log.e(TAG, "onPositiveClicked: NumberFormatException");
                        return;
                    }
                    mPresenter.newTrackerProgressionRate(mTracker, i);
                }
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
                // Do nothing
            }

            @Override
            public String getPositiveButtonText() {
                return getString(R.string.Confirm);
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                // Not used
                return null;
            }

            @Override
            public String getDescriptiveText() {
                // Not used
                return null;
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // not used
            }
        };
        DialogFragment newDifficultyDialog = new DifficultySelectorDialog();
        newDifficultyDialog.show(getSupportFragmentManager(), "new_difficulty_dialog");
    }

    private void showAddSubCustomDialog(){
        mDialogCallback = new DialogCallerContract() {
            @Override
            public void onPositiveClicked(String string) {
                // not used
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
            }

            @Override
            public String getPositiveButtonText() {
                // Not used
                return null;
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                // Not used
                return null;
            }

            @Override
            public String getDescriptiveText() {
                // Not used
                return null;
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // Amount returned is the amount to add
                // to tracker (could be negative)
                mPresenter.incrementTrackerScore(mTracker, amount);
            }
        };
        DialogFragment addSubCustomDialog = new AddSubCustomInputDialog();
        addSubCustomDialog.show(getSupportFragmentManager(), "add_sub_custom");

    }

    private void showArchiveTrackerDialog(){

        mDialogCallback = new DialogCallerContract() {
            @Override
            public void onPositiveClicked(String result) {
                mPresenter.archiveTracker(mTracker);
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
                // Nothing to do
            }

            @Override
            public String getPositiveButtonText() {
                return getString(R.string.Confirm);
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                if(mTracker.isArchived()){
                    return getString(R.string.unarchive_tracker);
                }else{
                    return getString(R.string.archive_tracker);
                }
            }

            @Override
            public String getDescriptiveText() {
                if(mTracker.isArchived()){
                    return getString(R.string.unarchive_tracker_desc);
                }else{
                    return getString(R.string.archive_tracker_desc);
                }
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // Not used
            }
        };

        DialogFragment yesNoDialog = new YesNoDialog();
        yesNoDialog.show(getSupportFragmentManager(), "yes_no_archive_dialog");

    }

    private void showDeleteTrackerDialog(){

        mDialogCallback = new DialogCallerContract() {
            @Override
            public void onPositiveClicked(String result) {
                mPresenter.deleteTracker(mTracker);
            }

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "onNegativeClicked: ");
                // Nothing to do
            }

            @Override
            public String getPositiveButtonText() {
                return getString(R.string.Confirm);
            }

            @Override
            public String getNegativeButtonText() {
                return getString(R.string.Cancel);
            }

            @Override
            public String getTitleText() {
                return getString(R.string.delete_tracker);
            }

            @Override
            public String getDescriptiveText() {
                return getString(R.string.delete_tracker_desc);
            }

            @Override
            public Tracker getTracker() {
                return mTracker;
            }

            @Override
            public void returnInt(int amount) {
                // Not used
            }
        };

        DialogFragment yesNoDialog = new YesNoDialog();
        yesNoDialog.show(getSupportFragmentManager(), "yes_no_delete_dialog");

    }

    private void initTextViews() {

        Log.d(TAG, "initTextViews: tracker title = " + mTracker.getTitle());

        TextView nameTextView = findViewById(R.id.current_name_tv);
        nameTextView.setText(mTracker.getTitle());

        TextView labelEditText = findViewById(R.id.label_tv);
        labelEditText.setText(mTracker.getCounterLabel());

        // Capitalise first letter
        String unCapitalisedLabel = getString(R.string.string_to_colon, mTracker.getCounterLabel());
        String editDifficultyLabel = unCapitalisedLabel.substring(0,1).toUpperCase() + unCapitalisedLabel.substring(1);
        TextView editDifficultyTv = findViewById(R.id.edit_difficulty_tv);
        editDifficultyTv.setText(editDifficultyLabel);

        TextView maxCountTv = findViewById(R.id.max_count_tv);
        maxCountTv.setText(getString(R.string.x_ys, mTracker.getLevelRate(), mTracker.getCounterLabel()));

        TextView archiveTv = findViewById(R.id.archive_tracker_button_text_view);
        if(mTracker.isArchived()){
            archiveTv.setText(R.string.unarchive_tracker);
        }else{
            archiveTv.setText(R.string.archive_tracker);
        }

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
        topButton1.setOnClickListener(view -> mPresenter.timerButtonClicked(mTracker));

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

        topButton2.setOnClickListener(view -> mPresenter.incrementTrackerScore(mTracker, button2AddAmount));

        topButton3.setOnClickListener(view -> mPresenter.incrementTrackerScore(mTracker, button3AddAmount));


        topButton4.setOnClickListener(v -> showAddSubCustomDialog());

    }

    private void initClickListeners(){

        View newNameLayout = findViewById(R.id.new_name_layout);
        newNameLayout.setOnClickListener(view -> showNewNameDialog());

        View newDifficultyLayout = findViewById(R.id.new_difficulty_layout);
        newDifficultyLayout.setOnClickListener(view -> showNewDifficultyDialog());

        View newLabelLayout = findViewById(R.id.new_counter_label_layout);
        newLabelLayout.setOnClickListener(view -> showNewLabelDialog());

        View deleteTrackerLayout = findViewById(R.id.tracker_details_delete_button_layout);
        deleteTrackerLayout.setOnClickListener(view -> showDeleteTrackerDialog());

        View archiveTrackerLayout = findViewById(R.id.tracker_details_archive_button_layout);
        archiveTrackerLayout.setOnClickListener(view -> showArchiveTrackerDialog());

    }

}
