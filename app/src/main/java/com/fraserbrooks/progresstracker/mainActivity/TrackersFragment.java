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

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.TouchInterceptor;
import com.fraserbrooks.progresstracker.addTrackerActivity.AddTrackerActivity;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.fraserbrooks.progresstracker.graphs.BarGraph;
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
    private TouchInterceptor mTrackerListView;
    private BarGraph mBarGraph;

    private String rememberExpanded;

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


        View listFooterView = inflater.inflate(R.layout.shared_ui_list_footer_add_and_reorder_button,
                mTrackerListView, false);

        final View reorderButton = listFooterView.findViewById(R.id.list_footer_drag_and_drop_button_layout);
        View addButton = listFooterView.findViewById(R.id.list_footer_add_button_layout);

        reorderButton.setOnClickListener(view -> reorderButtonClicked());

        addButton.setOnClickListener(view -> {
            //TODO: find a better way of adding test data
            if(mListAdapter.getCount() == 0){
                Log.d(TAG, "onItemClick: adding dummy test data");
                mPresenter.addTestData();
            }else{
                mPresenter.addTrackerButtonClicked();
            }
        });

        mTrackerListView.setAdapter(mListAdapter);
        mTrackerListView.addHeaderView(mBarGraph);

        mTrackerListView.addFooterView(listFooterView);

        mTrackerListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "onItemClick: " + id + " position: " + position);
            if(position == 0){
                Log.d(TAG, "onItemClick: graph clicked");
                mPresenter.graphClicked();
            }else if (position == parent.getCount() - 1 ){
                Log.d(TAG, "onItemClick: footer clicked");
            }else {
                Tracker tracker = (Tracker) parent.getItemAtPosition(position);
                if(!tracker.isExpanded()){
                    // Disable drag and drop if needed and show expanded view
                    if(mTrackerListView.dragEnabled()){
                        reorderButtonClicked();
                    }else{
                        mPresenter.setTrackerExpandCollapse(tracker);
                    }
                }
            }
        });


        mTrackerListView.setDropListener(this.mDropListener);
        mTrackerListView.setDragEnabled(false);

        registerForContextMenu(mTrackerListView);


        return root;

    }

    private void reorderButtonClicked() {

        if(mTrackerListView.dragEnabled()){
            mTrackerListView.setDragEnabled(false);
            mListAdapter.setShowDragButton(false);
            showDragAndDrop(false);
        }else{

            // un-expand views
            for(int i = 0; i < mListAdapter.getCount(); i++){
                Tracker t =  mListAdapter.getItem(i);
                if(t != null && t.isExpanded()){
                    //enableDrag = false;
                    mPresenter.setTrackerExpandCollapse(t);
                    updateOrAddTracker(t);
                }
            }

            mTrackerListView.setDragEnabled(true);
            mListAdapter.setShowDragButton(true);
            showDragAndDrop(true);
        }

    }

    private void showDragAndDrop(boolean show){

        for(int i = 0; i < mTrackerListView.getCount(); i++){
            View listItemView = mTrackerListView.getChildAt(i);

            if(listItemView == null){
                Log.d(TAG, "showDragAndDrop: null @ " + i);
                break;
            }

            View dragButtonView = listItemView.findViewById(R.id.reorder_drag_button_layout);
            if(dragButtonView != null){

                if(show){
                    dragButtonView.setVisibility(View.VISIBLE);
                } else {
                    dragButtonView.setVisibility(View.GONE);
                }
            }else{
                Log.d(TAG, "showDragAndDrop: could not find drag button at i = " + i);
            }

        }

    }

    @Override
    public void showTrackers(List<Tracker> trackers) {
        mListAdapter.clear();
        mListAdapter.addAll(trackers);
    }

    @Override
    public void updateOrAddTracker(Tracker tracker) {

        if(tracker.getId().equals(rememberExpanded)) tracker.setExpanded(true);

        int i = mListAdapter.getPosition(tracker);
        mListAdapter.remove(tracker);

        if (i == -1) Log.d(TAG, "updateOrAddTracker: does not exist in adapter. Placing at bottom");
        i = (i == -1) ? mListAdapter.getCount() : i;

        mListAdapter.insert(tracker, i);
    }

    @Override
    public void rememberExpanded(Tracker trackerAboutToRefresh) {
        if(trackerAboutToRefresh.isExpanded()){
            rememberExpanded = trackerAboutToRefresh.getId();
        }else{
            rememberExpanded = "";
        }
    }

    @Override
    public void removeTracker(Tracker tracker) {
        mListAdapter.remove(tracker);
        mBarGraph.remove(tracker);

        if(mListAdapter.getCount() == 0) showNoTrackers();
    }

    @Override
    public void showNoTrackers() {
        // TODO
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
                AddTrackerActivity.class);
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
        private boolean mShowDragButton = false;

        public TrackersAdapter(Context context, int resourceId,
                               TrackersContract.Presenter presenter){
            super(context, resourceId);
            mResource = resourceId;
            mPresenter = presenter;
            mTrackerInflater = new TrackerViewInflater(getContext(), mPresenter);

        }



        @NonNull
        @Override
        public View getView(int i, View convertView, @NonNull ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(mResource,
                        parent, false);
            }

            final Tracker tracker = getItem(i);
            assert tracker != null;

            // Show or hide drag button based on dragEnabled
            View dragButton = convertView.findViewById(R.id.reorder_drag_button_layout);
            if(mShowDragButton){
                dragButton.setVisibility(View.VISIBLE);

                //Hide expanded view
                tracker.setExpanded(false);
            }else{
                dragButton.setVisibility(View.GONE);
            }


            //Top ConstraintLayout of expanded view used as button to de-expand the view
            ViewGroup expandedLayout = convertView.findViewById(R.id.expanded_tracker_layout_top);
            expandedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.setTrackerExpandCollapse(tracker);
                }
            });

            mTrackerInflater.inflateTracker(convertView, tracker, tracker.isExpanded());

            return convertView;
        }


        public void setShowDragButton(boolean showDragButton) {
            this.mShowDragButton = showDragButton;
        }
    }

    TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
        public void drop(int from, int to) {

            Log.d(TAG, "drop: from = " + from + " to = " + to);
            to += 1;

            // Convert from ListView index to Adapter index
            // (ListView includes the header and footer views while the adapter doesn't)
            from -= mTrackerListView.getHeaderViewsCount();
            to -= mTrackerListView.getHeaderViewsCount();
            int lastTrackerIndex = mTrackerListView.getCount() - 1
                    - mTrackerListView.getFooterViewsCount();
            if (from < 0) from = 0;
            if (to < 0) to = 0;
            if (from > lastTrackerIndex) from = lastTrackerIndex;
            if (to > lastTrackerIndex) to = lastTrackerIndex;

            Log.d(TAG, "drop: listCount = " + mTrackerListView.getCount());
            Log.d(TAG, "drop: lastTrackerIndex = " + lastTrackerIndex);

            if (from == to) {
                Log.d(TAG, "drop: item is not moving anywhere");
                return;
            }

            Tracker tracker = mListAdapter.getItem(from);

            Log.d(TAG, "dropListener: moving Tracker from pos=" + from + " to pos=" + to);

            // Remove and insert at new position
            mListAdapter.remove(tracker);
            if (from < to) {
                mListAdapter.insert(tracker, to - 1);
            } else {
                mListAdapter.insert(tracker, to);
            }

            // Persist ordering
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                Tracker t = mListAdapter.getItem(i);
                if (t == null) continue;
                t.setIndex(i);
                mPresenter.updateTracker(t);
            }

        }

    };

}
