package com.fraserbrooks.progresstracker;

public interface Expandable {

    boolean isExpanded();

    void expandView();

    void shrinkView();

    interface Root extends Expandable{

        void addExpansionCallback(ExpandCollapseCallback callback);

    }

    interface ExpandCollapseCallback{

        int EXPANDED = 11;
        int COLLAPSED = 12;

        void onStateChange(String id, int state);

    }

}
