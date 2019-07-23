package com.fraserbrooks.progresstracker.dialogs;

import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;

public interface DialogCallerContract {
    /* The activity that creates an instance of a dialog fragment must
     * implement this interface in order to receive event callbacks. */

    void onPositiveClicked(String result);

    void onNegativeClicked();

    String getPositiveButtonText();

    String getNegativeButtonText();

    String getTitleText();

    String getDescriptiveText();

    Tracker getTracker();

    void returnInt(int amount);

}
