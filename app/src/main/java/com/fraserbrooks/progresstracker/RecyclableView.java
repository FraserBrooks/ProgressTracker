package com.fraserbrooks.progresstracker;

public interface RecyclableView<D>{

    void initWith(D data);

    String getIdForAdapter();

    interface Root<D> extends RecyclableView<D>{

        void showDragAndDrop(boolean show);

    }

}


