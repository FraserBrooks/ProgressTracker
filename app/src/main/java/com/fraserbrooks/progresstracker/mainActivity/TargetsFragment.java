package com.fraserbrooks.progresstracker.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.Injection;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.TouchInterceptor;
import com.fraserbrooks.progresstracker.addTargetActivity.AddTargetActivity;
import com.fraserbrooks.progresstracker.data.Target;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TargetsFragment extends Fragment implements TargetsContract.View{

    private final String TAG = "TargetFragment";

    private TargetsContract.Presenter mPresenter;

    private TargetAdapter mListAdapter;



    public TargetsFragment(){
        // Required empty public constructor
    }

    public static TargetsFragment newInstance() { return new TargetsFragment();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);

        assert (getContext() != null);
        new TargetsPresenter(Injection.provideRepository(getContext()),
                this, AppExecutors.getInstance());

        mListAdapter = new TargetAdapter(getContext(),R.layout.frag_targets_target_item, mPresenter);

    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume: called");
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(TargetsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.d(TAG, "onCreateView: called");
        View root = inflater.inflate(R.layout.frag_targets, container, false);

        // Attach the adapter to a ListView
        TouchInterceptor targetListView = root.findViewById(R.id.target_list);

        View addItemButton = inflater.inflate(R.layout.shared_ui_add_item_button,
                targetListView, false);

        targetListView.setAdapter(mListAdapter);

        targetListView.addFooterView(addItemButton);

        targetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                if (position == parent.getCount() - 1 ) {
                    Log.d(TAG, "onItemClick: listfooterclicked");
                    mPresenter.addTargetButtonClicked();
                }
            }
        });

        TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {

            public void drop(int from, int to) {

                mPresenter.changeTargetOrder(from, to);

//                ArrayList<Target> ls = mListAdapter.getItems();
//                Log.d(TAG, "drop: ls size = " + ls.size());
//                //Assuming that item is moved up the list
//                int direction = -1;
//                //For instance where the item is dragged down the list
//                if (from < to) {
//                    direction = 1;
//                }
//                if (from == (ls.size()) || to == (ls.size())) {
//                    Log.d(TAG, "drop: can't move footer");
//                    return;
//                }
//                Log.d(TAG, "drop: " + " from " + from + " to " + to);
//
//                if (from == to) {
//                    return;
//                }
//                Object target = ls.get(from);
//                for (int i = from; i != to; i += direction) {
//                    Log.d(TAG, "drop: loop");
//                    ls.set(i, ls.get(i + direction));
//                }
//                ls.set(to, (Target) target);
//                mListAdapter.replaceData(ls);
            }

        };

        targetListView.setDropListener(mDropListener);
        registerForContextMenu(targetListView);
        return root;
    }

    @Override
    public void showTargets(List<Target> targets) {
        mListAdapter.clear();
        mListAdapter.addAll(targets);
    }

    @Override
    public void updateOrAddTarget(Target target) {
        int i = mListAdapter.getPosition(target);
        mListAdapter.remove(target);

        if (i == -1) Log.d(TAG, "updateOrAddTarget: does not exist in adapter. Placing at bottom");
        i = (i == -1) ? mListAdapter.getCount() : i;

        mListAdapter.insert(target, i);
    }

    @Override
    public void removeTarget(Target target) {
        mListAdapter.remove(target);
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
    public void showNoTargets() {
        // todo
    }

    @Override
    public void showNoDataAvailable() {
        // todo
    }

    @Override
    public void showTargetDetailsScreen(String targetId) {
        // todo
    }

    @Override
    public void showAddTargetScreen() {
        Log.d(TAG, "moving to new AddTargetActivity");

        Intent intent = new Intent(this.getActivity(),
                AddTargetActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }



    private class TargetAdapter extends ArrayAdapter<Target> {

        private ViewGroup.LayoutParams defaultParams;

        private final TargetsContract.Presenter mPresenter;
        private final int mResource;


        public TargetAdapter(@NonNull Context context, int resourceId, @NonNull TargetsContract.Presenter presenter) {
            super(context, resourceId);
            this.mPresenter = presenter;
            this.mResource = resourceId;
            this.defaultParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent){


            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null ) {
                convertView = LayoutInflater.from(getContext()).inflate(mResource,
                        parent, false);
            }else{
                convertView.setLayoutParams(defaultParams);
            }


            final Target target = getItem(position);

            inflateTarget(convertView, target);
            return convertView;
        }

        private void inflateTarget(View convertView, Target target) {

            String title = mPresenter.getTargetTitle(target);

            float progress = mPresenter.getTargetCurrentPercentage(target) / 100f;

            String percentageString = mPresenter.getTargetAverageCompletion(target) + "%";

            String label = mPresenter.getTopRightLabel(target);

            String lowerLabel = mPresenter.getLowerLeftLabel(target);

            //Get views:
            TextView titleTv = convertView.findViewById(R.id.title_tv);
            TextView secondaryTv = convertView.findViewById(R.id.secondary_tv);
            TextView lowerTv = convertView.findViewById(R.id.this_period_tv);
            TextView percentageTv = convertView.findViewById(R.id.percentage_tv);
            TextView tvColourRect = convertView.findViewById(R.id.filled_rect);
            TextView tvWhiteRect = convertView.findViewById(R.id.not_filled_rect);


            //Progress bar layouts
            LinearLayout.LayoutParams param_colour = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    progress
            );
            LinearLayout.LayoutParams param_white = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (1f - progress)
            );
            tvColourRect.setLayoutParams(param_colour);
            tvWhiteRect.setLayoutParams(param_white);

            int[] colourBounds = new int[6];

            colourBounds[0] = getContext().getResources().
                    getInteger(R.integer.target_bar_red_lower);
            colourBounds[1] = getContext().getResources().
                    getInteger(R.integer.target_bar_red_higher);
            colourBounds[2] = getContext().getResources().
                    getInteger(R.integer.target_bar_green_lower);
            colourBounds[3] = getContext().getResources().
                    getInteger(R.integer.target_bar_green_higher);
            colourBounds[4] = getContext().getResources().
                    getInteger(R.integer.target_bar_blue_lower);
            colourBounds[5] = getContext().getResources().
                    getInteger(R.integer.target_bar_blue_higher);

            if(progress == 1){
                tvColourRect.setBackgroundResource(R.color.dark1_level3);
            }else{

                int[] progressBarColours = getRGB(progress, colourBounds);

                tvColourRect.setBackgroundColor(Color.rgb(progressBarColours[0],
                        progressBarColours[1],
                        progressBarColours[2]));
            }




            titleTv.setText(title);
            secondaryTv.setText(label);
            lowerTv.setText(lowerLabel);
            percentageTv.setText(percentageString);
            if(!percentageString.equals("0%")){
                String[] p = percentageString.split("%");
                if(p.length > 0){
                    colourBounds[0] = getContext().getResources().
                            getInteger(R.integer.target_percentage_red_lower);
                    colourBounds[1] = getContext().getResources().
                            getInteger(R.integer.target_percentage_red_higher);
                    colourBounds[2] = getContext().getResources().
                            getInteger(R.integer.target_percentage_green_lower);
                    colourBounds[3] = getContext().getResources().
                            getInteger(R.integer.target_percentage_green_higher);
                    colourBounds[4] = getContext().getResources().
                            getInteger(R.integer.target_percentage_blue_lower);
                    colourBounds[5] = getContext().getResources().
                            getInteger(R.integer.target_percentage_blue_higher);
                    float percentage =  Float.parseFloat(p[0]) / 100;
                    int[] percentageStringColour = getRGB(percentage, colourBounds);
                    percentageTv.setTextColor(Color.rgb(percentageStringColour[0],
                            percentageStringColour[1],
                            percentageStringColour[2]));
                }
            } else{
                percentageTv.setTextColor(convertView.
                        getResources().getColor(R.color.appBG));
            }

        }

        // todo: abstract all this setting of colours out (use resources or use presenter)
        private int[] getRGB(float progress, int[] colourBounds) {
            int[] progBarColours = new int[3];

            for (int i = 0; i < 3; i++) {
                int low = colourBounds[2*i];
                int high = colourBounds[(2*i)+1];
                float toAdd = (high - low) * progress;
                progBarColours[i] = (int) (low + toAdd);
            }
            return progBarColours;
        }


    }


}
