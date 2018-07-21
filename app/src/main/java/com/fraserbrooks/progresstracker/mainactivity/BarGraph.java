package com.fraserbrooks.progresstracker.mainactivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Tracker;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Fraser on 30/12/2017.
 */

public class BarGraph extends LinearLayout {

    private final String TAG = "BarGraph";

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

    private class Pair{
        public View view;
        public int index;
    }

    private HashMap<String, Pair> mCachedViews;

    public BarGraph(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        mCachedViews = new LinkedHashMap<>();
    }

    public void refresh(List<Tracker> ls){
        this.removeAllViews();
        populate(ls);
    }

    public void updateInGraph(Tracker tracker){
        Log.d(TAG, "updateInGraph: updateInGraphCalled");
        View graph_entry = getGraphEntryView(tracker);
        Pair entry = mCachedViews.get(tracker.getId());
        int i = this.getChildCount();
        Log.d(TAG, "updateInGraph: " + i + " children found");
        if(entry != null){
            Log.d(TAG, "updateInGraph: old view found. removing old...");
            i = entry.index;
            this.removeView(entry.view);
        }
        Pair newView = new Pair();
        newView.view = graph_entry;
        newView.index = i;
        mCachedViews.put(tracker.getId(), newView);
        this.addView(graph_entry, i);
    }

    private void populate(List<Tracker> trackers) {
        for ( Tracker tracker : trackers) {
            View graph_entry = getGraphEntryView(tracker);
            Pair view = new Pair();
            view.view = graph_entry;
            view.index = this.getChildCount();
            mCachedViews.put(tracker.getId(), view);
            this.addView(graph_entry);
        }
    }

    @NonNull
    private View getGraphEntryView(Tracker tracker) {
        float progress = tracker.getPercentageToNextLevel();

        View graph_entry = LayoutInflater.from(getContext()).inflate(R.layout.bar_graph_item, this, false);

        // Data Population
        TextView tvName = graph_entry.findViewById(R.id.bar_graph_text);
        TextView tvColourRect = graph_entry.findViewById(R.id.bar_graph_colour_rect);
        TextView tvBlankRect = graph_entry.findViewById(R.id.bar_graph_blank_rect);

        // set colour
        if(tracker.getCountToMaxLevel() == 0){
            tvColourRect.setBackgroundResource(levelRects[0]);
        }else{
            int i = (tracker.getLevel() >= 8) ? 8 : tracker.getLevel() + 1;
            tvColourRect.setBackgroundResource(levelRects[i]);
        }

        tvName.setText(tracker.getTitle());

        //set weights of rects
        LayoutParams param_colour = new LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                progress
        );
        LayoutParams param_blank = new LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                (1f - progress)
        );
        tvColourRect.setLayoutParams(param_colour);
        tvBlankRect.setLayoutParams(param_blank);
        return graph_entry;
    }


}
