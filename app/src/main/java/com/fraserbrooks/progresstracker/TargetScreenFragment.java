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
public class TargetScreenFragment extends Fragment {

    private final String TAG = "Main>TargetScreenFrag";
    private TargetAdapter targetAdapter;
    private DataWrapper dataWrapper;

    public TargetScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialise the DataWrapper
        dataWrapper = new DataWrapper();

        // Inflate the layout for this fragment
        ViewGroup fragmentScreen = (ViewGroup) inflater.inflate(R.layout.fragment_target_screen,
                        container, false);

        // Attach the adapter to a ListView
        TouchInterceptor targetListView = fragmentScreen.findViewById(R.id.target_list);

        View addItemButton = inflater.inflate(R.layout.add_tracker_button,
                targetListView, false);

        targetAdapter = new TargetAdapter(getContext());
        targetListView.setAdapter(targetAdapter);

        targetListView.addFooterView(addItemButton);

        targetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                // todo

                if (position == parent.getCount() - 1 ) {
                    Log.d(TAG, "onItemClick: listfooterclicked");
                    Intent intent = new Intent(getActivity(),
                            AddTargetActivity.class);
                    startActivity(intent);
                }
            }
        });

        TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {

            public void drop(int from, int to) {

                ArrayList<Target> ls = targetAdapter.getItems();

                Log.d(TAG, "drop: ls size = " + ls.size());

                //Assuming that item is moved up the list
                int direction = -1;

                //For instance where the item is dragged down the list
                if (from < to) {
                    direction = 1;
                }

                if (from == (ls.size()) || to == (ls.size())) {
                    Log.d(TAG, "drop: can't move footer");
                    return;
                }

                Log.d(TAG, "drop: " + " from " + from + " to " + to);

                if (from == to) {
                    return;
                }


                Object target = ls.get(from);
                for (int i = from; i != to; i += direction) {
                    Log.d(TAG, "drop: loop");
                    ls.set(i, ls.get(i + direction));
                }
                ls.set(to, (Target) target);

                targetAdapter.setItems(ls);
            }

        };
        targetListView.setDropListener(mDropListener);
        registerForContextMenu(targetListView);
        return fragmentScreen;
    }

    @Override
    public void onPause() {
        super.onPause();
        dataWrapper.writeTargetOrdering(getContext(), targetAdapter.getItems());
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        new ReadAndUpdateDataTask(true, targetAdapter, getContext()).execute();
    }


}
