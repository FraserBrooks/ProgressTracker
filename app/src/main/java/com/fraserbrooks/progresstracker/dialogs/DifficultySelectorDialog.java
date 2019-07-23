package com.fraserbrooks.progresstracker.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fraserbrooks.progresstracker.R;

public class DifficultySelectorDialog extends DialogFragment {

    private final String TAG = "DifficultyDialog";

    private DialogCallerContract mCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof Activity){
            //Verify that the host activity implements the callback
            try{
                // Instantiate the Callback so we can send events to the host
                mCallback = (DialogCallerContract) context;
            } catch (ClassCastException e){
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(context.toString() + " must implement DialogCallerContract");
            }
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        assert (getActivity() != null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") final View
                dialogView = inflater.inflate(R.layout.shared_ui_dialog_difficulty_selector, null);

        TextView counterLabelTextView = dialogView.findViewById(R.id.dialog_difficulty_selector_spinner_tv);
        String counterLabel = mCallback.getTracker().getCounterLabel();
        String counterLabelCapitalised = counterLabel.substring(0,1).toUpperCase()
                + counterLabel.substring(1);
        counterLabelTextView.setText(getString(R.string.string_to_colon, counterLabelCapitalised));


        // Set spinner and custom difficulty Edit Text
        final EditText difficultyET = dialogView.findViewById(R.id.dialog_difficulty_selector_custom_edit_text);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.dialog_difficulty_selector_spinner);
        assert (getContext() != null);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.tracker_count_strings));
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(spinnerAdapter);

        //Set spinner to

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                Log.d(TAG, "spinner selected = " + selected);

                if(selected.equals(getString(R.string.Custom))){
                    difficultyET.setVisibility(View.VISIBLE);
                }else if(selected.equals(getString(R.string.does_not_matter))) {
                    difficultyET.setVisibility(View.GONE);
                    difficultyET.setText(getString(R.string.does_not_matter_base_level));
                }else{
                    difficultyET.setVisibility(View.GONE);
                    difficultyET.setText(selected.replaceAll("\\D+",""));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(dialogView)
                // Add action buttons
            .setPositiveButton(mCallback.getPositiveButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String difficultyString = difficultyET.getText().toString();
                    mCallback.onPositiveClicked(difficultyString);
                }
            })
            .setNegativeButton(mCallback.getNegativeButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "onClick: cancel clicked");
                    mCallback.onNegativeClicked();
                }
            });

        return builder.create();
    }

}
