package com.fraserbrooks.progresstracker.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CustomMaxCountDialog extends DialogFragment{

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

        return null;

    }

}
