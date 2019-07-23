package com.fraserbrooks.progresstracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.security.InvalidParameterException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerListView  extends RecyclerView {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerTouchInterceptorAdapter mAdapter;

    public RecyclerListView(@NonNull Context context) {
        super(context);
        init();
    }

    public RecyclerListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setAdapter(Adapter adapter){
        if(!(adapter instanceof RecyclerTouchInterceptorAdapter)){
            throw new InvalidParameterException();
        }
        mAdapter = (RecyclerTouchInterceptorAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public RecyclerTouchInterceptorAdapter getAdapter(){
        return mAdapter;
    }

    public void init(){
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        this.setLayoutManager(mLinearLayoutManager);
    }

    public int getHeaderViewsCount(){
        return mAdapter.getHeaderCount();
    }

    public int getFooterViewsCount(){
        return mAdapter.getFooterCount();
    }

    public int getCount(){
        return mAdapter.getDataCount();
    }

    public int getFirstVisiblePosition(){
        return mLinearLayoutManager.findFirstVisibleItemPosition();
    }

    public int getLastVisiblePosition(){
        return mLinearLayoutManager.findLastVisibleItemPosition();
    }

    public int getPositionForView(View v){
        return mLinearLayoutManager.getPosition(v);
    }

    public void setSelectionFromTop(int position, int y){
        mLinearLayoutManager.scrollToPositionWithOffset(position, y);
    }








}
