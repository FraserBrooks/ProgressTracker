package com.fraserbrooks.progresstracker.settings;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fraserbrooks.progresstracker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsScreenFragment extends Fragment {

    private final String TAG = "Main>SettingsScreenFrag";



    public SettingsScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup fragmentScreen = (ViewGroup) inflater.inflate(R.layout.frag_targets,
                        container, false);


        return fragmentScreen;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }


}
