package com.fraserbrooks.progresstracker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fraserbrooks.progresstracker.customviews.TouchInterceptorItemRootView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class RecyclerTouchInterceptorAdapter<Data, RecyclableDataView extends View & RecyclableView<Data> & Expandable>
        extends ListAdapter<Data, RecyclerTouchInterceptorAdapter.ViewHolder<Data>> {

    private static final String TAG = "RecyclerT.I.Adapter";

    public interface CustomViewInflater<A, B extends View & RecyclableView<A>>{
        B getView(Context context);
    }

    public interface ListChangeCallback<Data>{
        void onListChanged(List<Data> oldList, List<Data> newList);
    }

    private final CustomViewInflater<Data, RecyclableDataView> mCustomInflater;

    private final Expandable.ExpandCollapseCallback mExpansionCallback = new Expandable.ExpandCollapseCallback() {
        @Override
        public void onStateChange(String id, int state) {
            if(id == null) return;
            if(state == EXPANDED) mExpandedViews.add(id);
            else if(state == COLLAPSED) mExpandedViews.remove(id);
            else throw new InvalidParameterException();
        }
    };
    private HashSet<String> mExpandedViews;


    static class ViewHolder<T>
            extends RecyclerView.ViewHolder{

        TouchInterceptorItemRootView<T> recyclableView;

        // Used for footers/headers
        ViewHolder(View view){
            super(view);
        }

        // Used for list items
        ViewHolder(TouchInterceptorItemRootView<T> itemView) {
            super(itemView);
            recyclableView = itemView;
        }
    }


    private boolean mDragAndDropEnabled;

    private ListChangeCallback<Data> listChangeCallback;


    //headers
    private List<View> headers = new ArrayList<>();
    //footers
    private List<View> footers = new ArrayList<>();

    private static final int TYPE_HEADER = 111;
    private static final int TYPE_FOOTER = 222;
    private static final int TYPE_ITEM = 333;

    public RecyclerTouchInterceptorAdapter(@NonNull DiffUtil.ItemCallback<Data> diffCallback,
                                           @NonNull CustomViewInflater<Data, RecyclableDataView> customInflater,
                                           @NonNull ListChangeCallback<Data> listChangeCallback) {
        super(diffCallback);
        this.mCustomInflater = customInflater;
        this.listChangeCallback = listChangeCallback;
        mExpandedViews = new HashSet<>();
    }


    @Override
    public void onCurrentListChanged(List<Data> previousList, List<Data> currentList){
        super.onCurrentListChanged(previousList, currentList);
        listChangeCallback.onListChanged(previousList, currentList);
    }

    private static int x = 0;

    @NonNull
    @Override
    public RecyclerTouchInterceptorAdapter.ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        //if our position is one of our items (this comes from getItemViewType(int position) below)
        if(type == TYPE_ITEM) {

            TouchInterceptorItemRootView<Data> rootView = new TouchInterceptorItemRootView<>(viewGroup.getContext());

            // TODO : Fix whatever around here is causing the UI to stutter
            Log.d(TAG, "onCreateViewHolder: inflating view" + x);
            x += 1;

            rootView.setItemView(mCustomInflater.getView(viewGroup.getContext()));
            rootView.addExpansionCallback(mExpansionCallback);
            return new RecyclerTouchInterceptorAdapter.ViewHolder<>(rootView);
            //else we have a header/footer
        }else{
            //create a new frame layout, or inflate from a resource
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerTouchInterceptorAdapter.ViewHolder vh, int position) {
        //check what type of view our position is
        if(position < headers.size()){
            View v = headers.get(position);
            //add our view to a header view holder and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }else if(position >= headers.size() + getCurrentList().size()){
            View v = footers.get(position-getCurrentList().size()-headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        }else {
            //it's one of our items, display as required
            prepareGeneric( vh, position-headers.size());

        }
    }

    @Override
    public int getItemCount() {
        //make sure the adapter knows to look for all our items, headers, and footers
        return headers.size() + getCurrentList().size() + footers.size();
    }

    int getDataCount(){
        return getCurrentList().size();
    }

    int getHeaderCount(){
        return headers.size();
    }

    int getFooterCount(){
        return footers.size();
    }

    private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view){
        // empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();

        try{
            // remove this view from its current parent if it has one
            FrameLayout parent = (FrameLayout) view.getParent();
            if (parent != null) parent.removeView(view);
        } catch (ClassCastException e){
            // This should not happen
            Log.e(TAG, "prepareHeaderFooter: parent view not frame layout");
        }


        vh.base.addView(view);
    }

    private void prepareGeneric(RecyclerTouchInterceptorAdapter.ViewHolder<Data> vh, int position){
        //do whatever we need to for our other type
        Data data = getItem(position);
        vh.recyclableView.showDragAndDrop(mDragAndDropEnabled);
        vh.recyclableView.initWith(data);
        if(mExpandedViews.contains(vh.recyclableView.getIdForAdapter())) vh.recyclableView.expandView();
        else vh.recyclableView.shrinkView();
    }

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if(position < headers.size()){
            return TYPE_HEADER;
        }else if(position >= headers.size() + getCurrentList().size()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    //add a header to the adapter
    public void addHeader(View header){
        if(!headers.contains(header)){
            headers.add(header);
            //animate
            notifyItemInserted(headers.size() - 1);
        }
    }

    //remove a header from the adapter
    public void removeHeader(View header){
        if(headers.contains(header)){
            //animate
            notifyItemRemoved(headers.indexOf(header));
            headers.remove(header);
            if(header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
        }
    }

    //add a footer to the adapter
    public void addFooter(View footer){
        if(!footers.contains(footer)){
            footers.add(footer);
            //animate
            notifyItemInserted(headers.size()+getCurrentList().size()+footers.size()-1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer){
        if(footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size()+getCurrentList().size()+footers.indexOf(footer));
            footers.remove(footer);
            if(footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    }

    //our header/footer RecyclerView.ViewHolder is just a FrameLayout
    private class HeaderFooterViewHolder extends RecyclerTouchInterceptorAdapter.ViewHolder<Data>{
        FrameLayout base;
        HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }


    public boolean dragAndDropEnabled() {
        return mDragAndDropEnabled;
    }

    void setDragAndDropEnabled(boolean dragAndDropEnabled) {
        this.mDragAndDropEnabled = dragAndDropEnabled;
    }



}