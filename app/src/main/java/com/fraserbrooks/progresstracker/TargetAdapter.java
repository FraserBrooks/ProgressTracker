package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fraser on 10/01/2018.
 */

public class TargetAdapter extends ArrayAdapter<Target> {


    private DataWrapper dataWrapper;
    private ViewGroup.LayoutParams defaultParams;

    public TargetAdapter(@NonNull Context context) {
        super(context, 0);
        this.dataWrapper = new DataWrapper();
        this.defaultParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void refreshItems(){
        clear();
        setItems(dataWrapper.readTargets(getContext()));
    }

    public void setItems(ArrayList<Target> items) {
        clear();
        addAll(items);
        notifyDataSetChanged();
    }



    public ArrayList<Target> getItems(){
        ArrayList<Target> ls = new ArrayList<>();
        for(int i=0 ; i < getCount() ; i++){
            ls.add(getItem(i));
        }
        return ls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.target_item,
                    parent, false);
        }else{
            convertView.setLayoutParams(defaultParams);
        }


        final Target target = getItem(position);

        inflateTarget(convertView, target);
        return convertView;
    }

    private void inflateTarget(View convertView, Target target) {

        String title = target.getTargetTitle();
        String label = target.getSecondaryLabel();
        String lowerLabel = target.getLowerLabel();
        String percentageString = target.getPercentageString();
        float progress = target.getTargetProgress();

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
