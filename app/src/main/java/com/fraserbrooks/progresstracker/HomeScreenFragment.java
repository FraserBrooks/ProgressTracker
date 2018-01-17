package com.fraserbrooks.progresstracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment{
    private final String TAG = "Main>HomeScreenFragment";

    private TrackerAdapter trackerAdapter;
    private TouchInterceptor.DropListener mDropListener;

    private TouchInterceptor trackerListView;

    public HomeScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: called");

        //Get the dataWrapper and read list
        final DataWrapper dataWrapper = new DataWrapper();

        // Inflate the layout for this fragment
        final ViewGroup fragmentScreen = (ViewGroup) inflater.inflate(R.layout.fragment_home_screen,
                container, false);

        // Attach the adapter to a ListView
        trackerListView = fragmentScreen.findViewById(R.id.dabble_list);

        View addItemButton = inflater.inflate(R.layout.add_item_button,
                trackerListView, false);

        // Create the adapter to convert the array to views
        trackerAdapter = new TrackerAdapter(fragmentScreen.getContext());
        trackerListView.setAdapter(trackerAdapter);

        trackerListView.addFooterView(addItemButton);

        trackerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + id + " position: " + position);
                if(position == 0){ // Graph
                    //todo: add other graphs?
                    return;
                }else if (position == parent.getCount() - 1 ){
                    Log.d(TAG, "onItemClick: listfooterclicked");
                    Intent intent = new Intent(HomeScreenFragment.this.getActivity(),
                            AddTrackerActivity.class);
                    startActivity(intent);
                }else {
                    Tracker tracker = (Tracker) parent.getItemAtPosition(position);

                    if(tracker.isExpanded()){
                        tracker.collapse();
                        return;
                    } else {
                        tracker.expand();
                    }
                    trackerAdapter.notifyDataSetChanged();
                    trackerListView.smoothScrollToPositionFromTop(position, 0, 200);
                }
            }

        });

        mDropListener = new TouchInterceptor.DropListener() {

            public void drop(int from, int to) {

                ArrayList<Tracker> ls = trackerAdapter.getItems();

                //Assuming that item is moved up the list
                int direction = -1;

                //For instance where the item is dragged down the list
                if(from < to) {
                    direction = 1;
                }

                if(from == 0 || from == (ls.size() + 1)){
                    Log.d(TAG, "drop: can't move graph or footer");
                    return;
                }
                if(to == 0){
                    to = 1;
                }
                if (to  == (ls.size() + 1)){
                    to = ls.size();
                }

                from -= 1;
                to -= 1;

                Object target = ls.get(from);
                for(int i = from; i != to ; i += direction){
                    ls.set(i, ls.get(i+direction));
                }
                ls.set(to, (Tracker) target);

                trackerAdapter.setItems(ls);
                trackerAdapter.updateGraph();
            }

        };
        trackerListView.setDropListener(mDropListener);
        registerForContextMenu(trackerListView);
        return fragmentScreen;
    }

    @Override
    public void onPause() {
        super.onPause();
        trackerAdapter.writeItems();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        trackerAdapter.refreshItems();
    }

}
