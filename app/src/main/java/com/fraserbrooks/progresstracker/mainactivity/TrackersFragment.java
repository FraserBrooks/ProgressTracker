package com.fraserbrooks.progresstracker.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.TouchInterceptor;
import com.fraserbrooks.progresstracker.addTrackerActivity.AddTrackerActivity_;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Fraser on 08/04/2018.
 */

public class TrackersFragment extends Fragment implements TrackersContract.View {
    private final String TAG = "TrackersFragment";

    private TrackersContract.Presenter mPresenter;

    private TrackersAdapter mListAdapter;
    private TouchInterceptor.DropListener mDropListener;
    private TouchInterceptor mTrackerListView;
    private BarGraph mBarGraph;

    public TrackersFragment(){
        // Required empty public constructor
    }

    public static TrackersFragment newInstance(){return new TrackersFragment();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);

        assert (getContext() != null);
        new TrackersPresenter(Injection.provideRepository(getContext()), this, AppExecutors.getInstance());
        mListAdapter = new TrackersAdapter(getContext(), R.layout.tracker_item, mPresenter);

        mBarGraph = new BarGraph(getContext());


    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume: called");
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull TrackersContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.d(TAG, "onCreateView: called");
        View root = inflater.inflate(R.layout.fragment_home_screen,
                container, false);

        // Attach the adapter to a ListView
        mTrackerListView = root.findViewById(R.id.tracker_list);

        View addTrackerButton = inflater.inflate(R.layout.add_item_button,
                mTrackerListView, false);

        mTrackerListView.setAdapter(mListAdapter);

        mTrackerListView.addHeaderView(mBarGraph);
        mTrackerListView.addFooterView(addTrackerButton);

        mTrackerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + id + " position: " + position);
                if(position == 0){
                    Log.d(TAG, "onItemClick: graph clicked");
                    mPresenter.graphClicked();
                }else if (position == parent.getCount() - 1 ){
                    Log.d(TAG, "onItemClick: footer clicked");
                    mPresenter.addTrackerButtonClicked();
                }else {
                    Tracker tracker = (Tracker) parent.getItemAtPosition(position);

                    if(!tracker.isExpanded()){
                        mPresenter.setTrackerExpandCollapse(tracker);
                        mTrackerListView.smoothScrollToPositionFromTop(position, 0, 200);
                    }
                }
            }

        });
        mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                mPresenter.changeTrackerOrder(from , to);
            }

        };
        mTrackerListView.setDropListener(mDropListener);
        registerForContextMenu(mTrackerListView);


        return root;

    }


    @Override
    public void showTrackers(List<Tracker> trackers) {
        mListAdapter.clear();
        mListAdapter.addAll(trackers);
    }

    @Override
    public void updateOrAddTracker(Tracker tracker) {

        int i = mListAdapter.getPosition(tracker);
        mListAdapter.remove(tracker);

        if (i == -1) Log.d(TAG, "updateOrAddTracker: does not exist in adapter. Placing at bottom");
        i = (i == -1) ? mListAdapter.getCount() : i;

        mListAdapter.insert(tracker, i);
    }

    @Override
    public void removeTracker(Tracker tracker) {
        mListAdapter.remove(tracker);
        mBarGraph.remove(tracker);

        if(mListAdapter.getCount() == 0) showNoTrackers();
    }

    @Override
    public void showNoTrackers() {
        updateOrAddTracker(new Tracker("NO_TRACKERS", 0));
    }

    @Override
    public void showNoDataAvailable() {
        // Todo
    }

    @Override
    public void showTrackerDetailsScreen(String trackerId) {
        // Todo
    }

    @Override
    public void showAddTrackerScreen() {
        Log.d(TAG, "moving to new AddTrackerActivity");

        Intent intent = new Intent(this.getActivity(),
                AddTrackerActivity_.class);
        startActivity(intent);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void populateGraph(List<Tracker> trackers) {
        mBarGraph.refresh(trackers);
    }

    @Override
    public void updateInGraph(Tracker tracker){
        mBarGraph.updateInGraph(tracker);
    }

    @Override
    public void showLoading() {
        // todo
    }

    @Override
    public void hideLoading() {
        // todo
    }


    private static class TrackersAdapter extends ArrayAdapter<Tracker> {

        private TrackersContract.Presenter mPresenter;
        private ViewGroup.LayoutParams mDefaultParams;
        private int mResource;
        private int[] levelIcons = {
                R.drawable.heart_red,
                R.drawable.gem_blank,
                R.drawable.gem_yellow,
                R.drawable.gem_orange,
                R.drawable.gem_green,
                R.drawable.gem_purple,
                R.drawable.gem_lightblue,
                R.drawable.gem_blue,
                R.drawable.gem_brown,
                R.drawable.gem_black};
        private int[] levelRects = {
                R.drawable.heart_colour_rect,
                R.drawable.level1_colour_rect,
                R.drawable.level2_colour_rect,
                R.drawable.level3_colour_rect,
                R.drawable.level4_colour_rect,
                R.drawable.level5_colour_rect,
                R.drawable.level6_colour_rect,
                R.drawable.level7_colour_rect,
                R.drawable.level8_colour_rect
        };


        public TrackersAdapter(Context context, int resourceId, TrackersContract.Presenter presenter){
            super(context, resourceId);
            mResource = resourceId;
            mPresenter = presenter;
            mDefaultParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @NonNull
        @Override
        public View getView(int i, View convertView, @NonNull ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null || //don't reuse barGraph view
                    convertView.findViewById(R.id.expanded_dabble_layout) == null) {
                convertView = LayoutInflater.from(getContext()).inflate(mResource,
                        parent, false);
            }else{
                convertView.setLayoutParams(mDefaultParams);
            }

            final Tracker tracker = (Tracker) getItem(i);
            assert tracker != null;

            //Top ConstraintLayout of expanded view used as button to de-expand the view
            ViewGroup expandedLayout = convertView.findViewById(R.id.expanded_tracker_layout_top);
            expandedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.setTrackerExpandCollapse(tracker);
                }
            });

            inflateTracker(convertView, tracker);

            return convertView;
        }


        private void inflateTracker(View view, Tracker tracker){

            float progress = tracker.getPercentageToNextLevel();

            String name = tracker.getTitle();
            String quantifier = mPresenter.getTrackerQuantifier(tracker);

            //Make sure correct view is showing
            View smallLayout = view.findViewById(R.id.small_tracker_layout);
            View expandedLayout = view.findViewById(R.id.expanded_dabble_layout);


            // Set the level indicator text view
            TextView levelIndicator = view.findViewById(R.id.text_level);
            TextView levelIndicatorExpanded = view.findViewById(R.id.expanded_text_level);
            String levelToDisplay = mPresenter.getLevelIndicator(tracker);
            levelIndicator.setText(levelToDisplay);
            levelIndicatorExpanded.setText(levelToDisplay);

            if(!tracker.isExpanded()){
                // Hide expanded part of view
                expandedLayout.setVisibility(View.GONE);
                smallLayout.setVisibility(View.VISIBLE);

                //Get Widgets
                TextView tvName = view.findViewById(R.id.name_tv);
                TextView tvQuantifier = view.findViewById(R.id.quantifier_tv);
                TextView tvFilledRect = view.findViewById(R.id.filled_rect);
                TextView tvNotFilledRect = view.findViewById(R.id.not_filled_rect);
                ImageView iconImage = view.findViewById(R.id.gem_bitmap);

                tvName.setText(name);
                tvQuantifier.setText(quantifier);

                //Icon and progress bar
                setIconAndProgressBarColours(tracker, tvFilledRect, iconImage);
                setProgressBarDimensions(progress, tvFilledRect, tvNotFilledRect);

            } else {
                // Show expanded view
                expandedLayout.setVisibility(View.VISIBLE);
                smallLayout.setVisibility(View.GONE);

                //Get Widgets
                TextView expanded_tvName = view.findViewById(R.id.expanded_name_tv);
                TextView quantifierTwoTV = view.findViewById(R.id.expanded_quantifier_two_tv);
                TextView quantifierOneTV = view.findViewById(R.id.expanded_quantifier_one_tv);

                TextView countToMaxTV = view.findViewById(R.id.expanded_count_to_max_tv);
                TextView expanded_tvFilledRect = view.findViewById(R.id.expanded_filled_rect);
                TextView expanded_tvNotFilledRect = view.findViewById(R.id.expanded_not_filled_rect);
                ImageView expanded_iconImage = view.findViewById(R.id.expanded_gem_bitmap);

                expanded_tvName.setText(name);

                TextView editDifficultyTV = view.findViewById(R.id.edit_difficulty_tv);
                editDifficultyTV.setText(getContext().getResources().getString(
                        R.string.edit_xxx_to_max_level,
                        tracker.getCounterLabel().toLowerCase()));

                //Icon and progress bar
                setIconAndProgressBarColours(tracker, expanded_tvFilledRect, expanded_iconImage);
                setProgressBarDimensions(progress, expanded_tvFilledRect, expanded_tvNotFilledRect);

                quantifierOneTV.setText(mPresenter.getTrackerQuantifierOne(tracker));
                if(tracker.isTimeTracker()){
                    quantifierTwoTV.setText(mPresenter.getTrackerQuantifierTwo(tracker));
                }else{
                    quantifierTwoTV.setVisibility(View.GONE);
                }

                countToMaxTV.setText(getContext().getResources().getString(
                        R.string.count_to_complete,
                        tracker.getCountToMaxLevel()));

                initEditTitle(view, tracker);
                initCustomMaxCountLayout(view, tracker);
                initButtons(view, tracker);

            }



        }

        private void initEditTitle(View view, final Tracker tracker) {

            // Edit Name
            final EditText etvName = view.findViewById(R.id.name_etv);
            etvName.setHint(tracker.getTitle());
            final Button eNameButton = view.findViewById(R.id.edit_name_button);
            eNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = etvName.getText().toString();
                    mPresenter.changeTrackerTitle(tracker, newTitle);
                    etvName.clearFocus();
                    etvName.setText("");
                    etvName.setHint(newTitle);
                    hideSoftInput(etvName);
                }
            });
            etvName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE){
                        eNameButton.callOnClick();
                        return true;
                    }else{
                        return false;
                    }
                }
            });

        }

        private void initButtons(View view, final Tracker tracker) {
            //Get buttons
            Button topButton1 = view.findViewById(R.id.top_button_1);
            Button topButton2 = view.findViewById(R.id.top_button_2);
            Button topButton3 = view.findViewById(R.id.top_button_3);
            Button topButton4 = view.findViewById(R.id.top_button_4);

            //Todo
            View bottomButtonRow = view.findViewById(R.id.bottom_row_buttons);
            bottomButtonRow.setVisibility(View.GONE);

            // Timer button
            if(tracker.isCurrentlyTiming()){
                topButton1.setText(R.string.end_timer);
                topButton1.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            }else{
                topButton1.setText(R.string.start_timer);
                topButton1.setTextColor(getContext().getResources().getColor(R.color.default_text_color));
            }
            topButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.timerButtonClicked(tracker);
                }
            });

            final int button2AddAmount;
            final int button3AddAmount;

            // More details button
            topButton4.setText(R.string.more);
            topButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    // todo
                    mPresenter.moreDetailsButtonClicked(tracker);
                }
            });


            if(tracker.isTimeTracker()){

                //Show timer button
                topButton1.setVisibility(View.VISIBLE);

                topButton2.setText(R.string.plus_15_minutes);
                topButton3.setText(R.string.plus_1_hour);

                button2AddAmount = 15;
                button3AddAmount = 60;

            }else{

                //hide timer button
                topButton1.setVisibility(View.GONE);

                topButton3.setText(getContext().getResources().
                        getString(R.string.add_5_count,tracker.getCounterLabel()));
                topButton2.setText(getContext().getResources().
                        getString(R.string.add_1_count,tracker.getCounterLabel()));

                button2AddAmount = 5;
                button3AddAmount = 1;

            }

            topButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.addToTrackerScore(tracker, button2AddAmount);
                }
            });

            topButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.addToTrackerScore(tracker, button3AddAmount);
                }
            });
        }

        private void initCustomMaxCountLayout(View view, final Tracker tracker) {

            final View custom_max_count_layout = view.findViewById(R.id.custom_max_count_layout);
            final EditText max_count_etv = view.findViewById(R.id.add_custom_max_count_etv);

            int countToMax = (tracker.isTimeTracker())
                    ? tracker.getCountToMaxLevel()/60 //convert minutes to hours
                    : tracker.getCountToMaxLevel();  // else leave it as is
            max_count_etv.setText(String.format(
                    Locale.getDefault(), "%d", countToMax));

            // Using numberPassword input type to allow only digits
            // but we don't want to hide the digits as they're typed.
            max_count_etv.setTransformationMethod(null);

            // Difficulty setting spinner
            Spinner difficulty_spinner = view.findViewById(R.id.max_count_spinner);
            String[] max_count_strings = getContext().getResources().
                    getStringArray(R.array.tracker_count_strings);
            for (String s: max_count_strings) {
                s += " " + tracker.getCounterLabel();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    max_count_strings);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            difficulty_spinner.setAdapter(adapter);
            final int[] max_count_options = getContext().getResources().
                    getIntArray(R.array.tracker_count_ints);


            // Assume custom max count set
            difficulty_spinner.setSelection(0);
            custom_max_count_layout.setVisibility(View.VISIBLE);

            for(int i = 0; i < max_count_options.length; i++){
                if(max_count_options[i] == countToMax){
                    difficulty_spinner.setSelection(i+1);

                    // Not custom so hide custom input
                    custom_max_count_layout.setVisibility(View.GONE);
                }
            }

            difficulty_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position == 0){
                        custom_max_count_layout.setVisibility(View.VISIBLE);
                    }else{
                        mPresenter.changeTrackerMaxScore(tracker, max_count_options[position-1]);
                        custom_max_count_layout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final Button commitCustomMaxCount = view.findViewById(R.id.commit_custom_max_count_button);
            commitCustomMaxCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String input = max_count_etv.getText().toString();
                    if(input.equals("")){
                        Toast.makeText(getContext(), R.string.must_enter_a_time, Toast.LENGTH_LONG).show();
                        return;
                    }
                    int difficulty = Integer.parseInt(input);
                    if(difficulty < 1){
                        Toast.makeText(view.getContext(), R.string.custom_time_greater_than_0, Toast.LENGTH_LONG).show();
                        return;
                    }
                    mPresenter.changeTrackerMaxScore(tracker, difficulty);
                    hideSoftInput(max_count_etv);
                }
            });

            max_count_etv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE){
                        commitCustomMaxCount.callOnClick();
                        return true;
                    }
                    return false;
                }
            });
        }

        private void setProgressBarDimensions(float progress, TextView tvFilledRect, TextView tvNotFilledRect) {
            //create Progress bar layouts
            LinearLayout.LayoutParams paramForFilledPart = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    progress
            );
            LinearLayout.LayoutParams paramForNotFilledPart = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (1f - progress)
            );
            tvFilledRect.setLayoutParams(paramForFilledPart);
            tvNotFilledRect.setLayoutParams(paramForNotFilledPart);
        }

        private void setIconAndProgressBarColours(Tracker tracker, TextView tvFilledRect, ImageView iconImage) {
            if(tracker.getCountToMaxLevel() == 0){
                iconImage.setImageResource(levelIcons[0]);
                tvFilledRect.setBackgroundResource(levelRects[0]);
            }else{
                int k = (tracker.getLevel() > 8) ? 8 : tracker.getLevel();
                iconImage.setImageResource(levelIcons[k+1]);
                int i = (k >= 8) ? 8 : k + 1;
                tvFilledRect.setBackgroundResource(levelRects[i]);
            }
        }

        private void hideSoftInput(View v) {
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null){
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

}
