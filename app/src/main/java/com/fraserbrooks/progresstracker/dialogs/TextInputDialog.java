package com.fraserbrooks.progresstracker.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fraserbrooks.progresstracker.R;

public class TextInputDialog extends DialogFragment{

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
    public Dialog onCreateDialog(Bundle savedInstanceState){

        assert(getActivity() != null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because it's going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.shared_ui_dialog_text_input, null);
        builder.setView(dialogView)
        // Add action buttons
            .setPositiveButton(mCallback.getPositiveButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Save clicked
                    EditText textInput = dialogView.findViewById(R.id.dialog_new_name_etv);
                    String newName = "";
                    if(textInput != null) newName = textInput.getText().toString();
                    mCallback.onPositiveClicked(newName);
                }
            })
            .setNegativeButton(mCallback.getNegativeButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Cancel clicked
                    mCallback.onNegativeClicked();
                }
            });

        return builder.create();
    }




}
