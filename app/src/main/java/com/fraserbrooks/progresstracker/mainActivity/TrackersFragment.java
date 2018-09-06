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
import com.fraserbrooks.progresstracker.trackerDetailsActivity.TrackerDetailsActivity;
import com.fraserbrooks.progresstracker.util.AppExecutors;
import com.fraserbrooks.progresstracker.util.TrackerViewInflater;

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
                Tracker t = (Tracker) mTrackerListView.getItemAtPosition(from);
                mPresenter.changeTrackerOrder(t, from , to);
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
        Log.d(TAG, "moving to new TrackerDetailsActivity");

        Intent intent = new Intent(this.getActivity(),
                TrackerDetailsActivity.class);
        intent.putExtra("id", trackerId);
        startActivity(intent);
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
        private TrackerViewInflater mTrackerInflater;
        private int mResource;


        public TrackersAdapter(Context context, int resourceId, TrackersContract.Presenter presenter){
            super(context, resourceId);
            mResource = resourceId;
            mPresenter = presenter;
            mTrackerInflater = new TrackerViewInflater(getContext(), mPresenter);
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

            mTrackerInflater.inflateTracker(convertView, tracker, tracker.isExpanded());

            return convertView;
        }





    }

}
