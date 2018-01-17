package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fraser on 30/12/2017.
 */

public class BarGraph extends LinearLayout {

    private DataWrapper dataWrapper;

    public BarGraph(Context context) {
        super(context);
        this.dataWrapper = new DataWrapper();
        this.setOrientation(LinearLayout.VERTICAL);

        populate(dataWrapper.readTrackers(getContext()));
    }

    public void refresh(){
        this.removeAllViews();
        populate(dataWrapper.readTrackers(getContext()));
    }

    public void refresh(ArrayList<Tracker> ls){
        this.removeAllViews();
        populate(ls);
    }

    private void populate(ArrayList<Tracker> trackers) {
        for (Tracker tracker : trackers) {
            float progress = tracker.getProgressPercentage();

            View graph_entry = LayoutInflater.from(getContext()).inflate(R.layout.bar_graph_item, this, false);

            // Data Population
            TextView tvName = graph_entry.findViewById(R.id.bar_graph_text);
            TextView tvColourRect = graph_entry.findViewById(R.id.bar_graph_colour_rect);
            TextView tvBlankRect = graph_entry.findViewById(R.id.bar_graph_blank_rect);

            // set colour
            tracker.setColourAndIcon(tvColourRect);
            tvName.setText(tracker.getName());

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

            this.addView(graph_entry);
        }
    }


}
