package com.fraserbrooks.progresstracker.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.TouchInterceptor;
import com.fraserbrooks.progresstracker.addTrackerActivity.AddTrackerActivity_;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

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
        mListAdapter = new TrackersAdapter(getContext(), R.layout.frag_trackers_tracker_item, mPresenter);

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
        View root = inflater.inflate(R.layout.frag_trackers,
                container, false);

        // Attach the adapter to a ListView
        mTrackerListView = root.findViewById(R.id.tracker_list);

        View addTrackerButton = inflater.inflate(R.layout.shared_ui_add_item_button,
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
                    if(mListAdapter.getCount() == 0){
                        Log.e(TAG, "onItemClick: adding dummy test data");
                        mPresenter.addTestData();
                    }else{
                        mPresenter.addTrackerButtonClicked();
                    }
                }else {
                    Tracker tracker = (Tracker) parent.getItemAtPosition(position);

                    if(!tracker.isExpanded()){
                        mPresenter.setTrackerExpandCollapse(tracker);
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
        //eupdateOrAddTracker(new Tracker("NO_TRACKERS", 0));
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
        private int mResource;
        private int[] levelIcons = {
                R.drawable.frag_trackers_heart_red,
                R.drawable.frag_trackers_gem_blank,
                R.drawable.frag_trackers_gem_yellow,
                R.drawable.frag_trackers_gem_orange,
                R.drawable.frag_trackers_gem_green,
                R.drawable.frag_trackers_gem_purple,
                R.drawable.frag_trackers_gem_lightblue,
                R.drawable.frag_trackers_gem_blue,
                R.drawable.frag_trackers_gem_brown,
                R.drawable.frag_trackers_gem_black};
        private int[] levelRects = {
                R.drawable.frag_trackers_heart_colour_rect,
                R.drawable.frag_trackers_level1_colour_rect,
                R.drawable.frag_trackers_level2_colour_rect,
                R.drawable.frag_trackers_level3_colour_rect,
                R.drawable.frag_trackers_level4_colour_rect,
                R.drawable.frag_trackers_level5_colour_rect,
                R.drawable.frag_trackers_level6_colour_rect,
                R.drawable.frag_trackers_level7_colour_rect,
                R.drawable.frag_trackers_level8_colour_rect
        };


        public TrackersAdapter(Context context, int resourceId, TrackersContract.Presenter presenter){
            super(context, resourceId);
            mResource = resourceId;
            mPresenter = presenter;
        }

        @NonNull
        @Override
        public View getView(int i, View convertView, @NonNull ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null || //don't reuse barGraph view
                    convertView.findViewById(R.id.expanded_tracker_layout) == null) {
                convertView = LayoutInflater.from(getContext()).inflate(mResource,
                        parent, false);
            }

            final Tracker tracker = getItem(i);
            assert tracker != null;

            //Top ConstraintLayout of expanded view used as button to de-expand the view
            ViewGroup expandedLayout = convertView.findViewById(R.id.tracker_layout_top);
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

            //Make sure correct view is showing
            View smallLayout = view.findViewById(R.id.small_tracker_layout);
            View expandedLayout = view.findViewById(R.id.expanded_tracker_layout);
            if(tracker.isExpanded()){
                // Show expanded view
                expandedLayout.setVisibility(View.VISIBLE);
                smallLayout.setVisibility(View.GONE);
            }else{
                // Hide expanded part of view
                expandedLayout.setVisibility(View.GONE);
                smallLayout.setVisibility(View.VISIBLE);
            }

            if(tracker.isExpanded()){
                view = expandedLayout;
            }else{
                view = smallLayout;
            }

            // Set the level indicator text view
            TextView levelIndicator = view.findViewById(R.id.text_level_tv);
            String levelToDisplay = mPresenter.getLevelIndicator(tracker);
            levelIndicator.setText(levelToDisplay);

            //Get Widgets
            TextView tvName = view.findViewById(R.id.name_tv);
            TextView tvQuantifier = view.findViewById(R.id.quantifier_tv);
            ImageView iconImage = view.findViewById(R.id.gem_bitmap);
            TextView tvFilledPart = view.findViewById(R.id.progress_bar_filled_part);
            TextView tvNotFilledPart = view.findViewById(R.id.progress_bar_not_filled_part);

            // Set name
            String name = tracker.getTitle();
            tvName.setText(name);

            // Set quantifier ( - 45 hours, 6 lectures, etc.)
            String quantifier = mPresenter.getTrackerQuantifier(tracker);
            tvQuantifier.setText(quantifier);

            //Icon and progress bar
            float progress = tracker.getPercentageToNextLevel();
            setIconAndProgressBarColours(tracker, tvFilledPart, iconImage);
            setProgressBarDimensions(progress, tvFilledPart, tvNotFilledPart);


            if(tracker.isExpanded()){
                // init extra widgets

                // Second quantifier (eg. minutes)
                TextView secondQuantifier = view.findViewById(R.id.quantifier_two_tv);
                if(tracker.isTimeTracker() && tracker.getCountSoFar() > 59){
                    secondQuantifier.setText(mPresenter.getTrackerQuantifierTwo(tracker));
                }else{
                    secondQuantifier.setVisibility(View.GONE);
                }

                // Count to max/level 8
                TextView countToMaxTv = view.findViewById(R.id.count_to_max_tv);
                countToMaxTv.setText(getContext().getResources().getString(
                        R.string.count_to_complete,
                        tracker.getCountToMaxLevel()));

                initButtons(view, tracker);
            }

        }

        private void initButtons(View view, final Tracker tracker) {
            //Get buttons
            Button topButton1 = view.findViewById(R.id.top_button_1);
            Button topButton2 = view.findViewById(R.id.top_button_2);
            Button topButton3 = view.findViewById(R.id.top_button_3);
            Button topButton4 = view.findViewById(R.id.top_button_4);


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

                topButton2.setText(getContext().getResources()
                        .getString(R.string.add_1_count,tracker.getCounterLabel()));
                topButton3.setText(getContext().getResources()
                        .getString(R.string.add_5_count,tracker.getCounterLabel()));


                button2AddAmount = 1;
                button3AddAmount = 5;

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
    }

}
