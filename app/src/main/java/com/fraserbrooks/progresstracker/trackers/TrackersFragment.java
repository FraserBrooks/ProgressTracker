package com.fraserbrooks.progresstracker.trackers;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.RecyclerTouchInterceptor;
import com.fraserbrooks.progresstracker.RecyclerTouchInterceptorAdapter;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.customviews.graphs.BarGraph;
import com.fraserbrooks.progresstracker.trackers.view.TrackerView;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Fraser on 08/04/2018.
 */

public class TrackersFragment extends Fragment implements TrackersContract.View {
    private final String TAG = "TrackersFragment";

    private TrackersContract.Presenter mPresenter;

    private RecyclerTouchInterceptorAdapter
            <Tracker, TrackerView> mListAdapter;

    private RecyclerTouchInterceptor mTrackerListView;
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
        new TrackersPresenter(AppExecutors.getInstance(), this,
                Injection.provideUseCaseHandler(),
                Injection.provideGetTrackers(getContext()),
                Injection.provideGetTrackers(getContext()),
                Injection.provideIncrementTracker(getContext()),
                Injection.provideStartStopTrackerTimer(getContext()),
                Injection.provideClearRange(getContext()));


        mListAdapter = new RecyclerTouchInterceptorAdapter<>(DIFF_CALLBACK
                , context -> {
            // View Creator
            TrackerView v = new TrackerView(context);
            v.setTrackerInterface(mPresenter);
            return v; }
                , (oldList, newList) -> {
            // List change callback
            // Scroll to top on first load
            if(oldList.size() == 0) mTrackerListView.getLayoutManager().scrollToPosition(0); }
        );

        Log.d(TAG, "onCreate: observing tracker live data");
        mPresenter.getTrackersList().observe(this, list -> {

            if (Looper.myLooper() == Looper.getMainLooper()) Log.d(TAG, "asdff observeTrackerList runningOnMainThread");
            else Log.d(TAG, "asdff observeTrackerList runningOffMainThread");

            Log.d(TAG, "asdff trackerListObserver: submitting list ");
            mListAdapter.submitList(list);
        });

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
        mTrackerListView = root.findViewById(R.id.root_tracker_list);


        View listFooterView = inflater.inflate(R.layout.shared_ui_list_footer_add_and_reorder_button,
                mTrackerListView, false);


        final View enableDragAndDropButton = listFooterView.findViewById(R.id.footer_enable_drag_and_drop_button);
        View addButton = listFooterView.findViewById(R.id.list_footer_add_button_layout);

        enableDragAndDropButton.setOnClickListener(view -> enableDragAndDropButtonClicked());
        addButton.setOnClickListener(view -> mPresenter.addTrackerButtonClicked());


        mTrackerListView.setAdapter(mListAdapter);
        //mTrackerListView.getAdapter().addHeader(mBarGraph);
        mTrackerListView.getAdapter().addFooter(listFooterView);


        mTrackerListView.setDropListener(this.mDropListener);
        mTrackerListView.setDragEnabled(false);

        return root;
    }

    private void enableDragAndDropButtonClicked() {

        if(mTrackerListView.dragEnabled()){
            mTrackerListView.setDragEnabled(false);
        }else{
            mTrackerListView.setDragEnabled(true);
        }

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
    public void showLoading() {
        // todo
    }

    @Override
    public void hideLoading() {
        // todo
    }


    private DiffUtil.ItemCallback<Tracker> DIFF_CALLBACK = new DiffUtil.ItemCallback<Tracker>() {
        @Override
        public boolean areItemsTheSame(@NonNull Tracker oldItem, @NonNull Tracker newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Tracker oldItem, @NonNull Tracker newItem) {
            return oldItem.equals(newItem);
        }
    };



    private RecyclerTouchInterceptor.DropListener mDropListener = new RecyclerTouchInterceptor.DropListener() {
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

            Tracker draggedTracker = mListAdapter.getCurrentList().get(from);
            Tracker swappedTracker    = mListAdapter.getCurrentList().get(to);

            Log.d(TAG, "dropListener: moving Tracker from pos=" + from + " to pos=" + to);
            int newIndex = swappedTracker.getIndex();
            swappedTracker.setIndex(draggedTracker.getIndex());
            draggedTracker.setIndex(newIndex);

            mPresenter.updateTracker(draggedTracker);
            mPresenter.updateTracker(swappedTracker);

        }

    };

}
