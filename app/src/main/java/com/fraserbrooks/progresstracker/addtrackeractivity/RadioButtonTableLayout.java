package com.fraserbrooks.progresstracker.addtrackeractivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.fraserbrooks.progresstracker.R;

/**
 * Created by Fraser on 02/01/2018.
 */

public class RadioButtonTableLayout extends TableLayout implements View.OnClickListener {

    private static final String TAG = "com.fraserbrooks.progresstracker.addTrackerActivity.RadioButtonTableLayout";
    private RadioButton clickedButton;

    public RadioButtonTableLayout(Context context) {
        super(context);
    }

    public RadioButtonTableLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if(clickedButton != null){
            clickedButton.setChecked(false);
        }
        rb.setChecked(true);
        clickedButton = rb;
        if(clickedButton.getText().equals("Custom")){
            View input_layout = getRootView().findViewById(R.id.custom_max_count_layout);
            if (input_layout.getVisibility() == View.GONE){
                input_layout.setVisibility(View.VISIBLE);
            } else {
                input_layout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }


    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i=0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if ( v instanceof RadioButton ) {
                v.setOnClickListener(this);
            }
        }
    }

    public int getCheckedRadioButtonId() {
        if ( clickedButton != null ) {
            return clickedButton.getId();
        }

        return -1;
    }

}
