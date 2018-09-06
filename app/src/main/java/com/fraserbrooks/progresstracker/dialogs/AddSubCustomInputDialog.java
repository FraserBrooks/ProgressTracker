package com.fraserbrooks.progresstracker.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fraserbrooks.progresstracker.R;

public class AddSubCustomInputDialog extends DialogFragment {

    private final String TAG = "CustomTimeInputDialog";

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        assert (getActivity() != null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.shared_ui_dialog_add_sub_time, null);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_add_time_title);
        titleTextView.setText(mCallback.getTracker().getTitle());

        final EditText minutesInput = dialogView.findViewById(R.id.minutes_etv);
        final EditText hoursInput = dialogView.findViewById(R.id.hour_etv);

        if(!mCallback.getTracker().isTimeTracker()){
            minutesInput.setVisibility(View.GONE);
            hoursInput.setHint(mCallback.getTracker().getCounterLabel());
        }

        Button addButton = dialogView.findViewById(R.id.add_button);
        Button subButton = dialogView.findViewById(R.id.sub_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toAdd;
                if(mCallback.getTracker().isTimeTracker()){
                    toAdd = getInput(hoursInput, minutesInput);
                }else{
                    toAdd = getInput(hoursInput);
                }
                if(toAdd != 0){
                    mCallback.returnInt(toAdd);
                    AddSubCustomInputDialog.this.dismiss();
                }else{
                    Toast.makeText(AddSubCustomInputDialog.this.getContext(),
                            AddSubCustomInputDialog.this.getString(R.string.must_enter_a_number),
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toSub;
                if(mCallback.getTracker().isTimeTracker()){
                    toSub = getInput(hoursInput, minutesInput);
                }else{
                    toSub = getInput(hoursInput);
                }
                if(toSub != 0){
                    mCallback.returnInt(-toSub);
                    AddSubCustomInputDialog.this.dismiss();
                }else{
                    Toast.makeText(AddSubCustomInputDialog.this.getContext(),
                            AddSubCustomInputDialog.this.getString(R.string.must_enter_a_number),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        builder.setView(dialogView)
                // Add action button
                .setNegativeButton(mCallback.getNegativeButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel Clicked
                        mCallback.onNegativeClicked();
                    }
                });

        return builder.create();
    }

    private int getInput(EditText input){
        assert (input != null);

        // number of hours/sessions/paintings/'whatever the label for
        // this tracker is' (hours for time trackers)
        String inputValue = input.getText().toString();

        if(inputValue.isEmpty()) return 0;
        else{
            try {
                return Integer.parseInt(inputValue);
            }catch (NumberFormatException e){
                Log.e(TAG, "getInput: NumberFormatException: mainInput ");
                return 0;
            }
        }

    }

    private int getInput(EditText hoursInput, EditText minutesInput){
        assert (hoursInput != null);
        assert (minutesInput != null);

        // number of hours
        String hoursInputValue = hoursInput.getText().toString();

        // number of minutes to add ('empty string'/hidden  if not time tracker)
        String minutesInputValue = minutesInput.getText().toString();

        if(hoursInputValue.isEmpty() && minutesInputValue.isEmpty()) return 0;

        int minutes = 0;

        if(!hoursInputValue.isEmpty()){
            try{
                minutes += Integer.parseInt(hoursInputValue) * 60;
            } catch (NumberFormatException e){
                // This should be prevented by editText settings in XML
                Log.e(TAG, "getInput: NumberFormatException: hoursInput");
            }
        }
        if(!minutesInputValue.isEmpty()){
            try {
                minutes += Integer.parseInt(minutesInputValue);
            } catch (NumberFormatException e){
                // This should be prevented by editText settings in XML
                Log.e(TAG, "getInput: NumberFormatException: minutesInput");
            }
        }
        return minutes;
    }

}
