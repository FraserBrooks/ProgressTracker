package com.fraserbrooks.progresstracker.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fraserbrooks.progresstracker.Expandable;
import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.RecyclableView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchInterceptorItemRootView<Data>
        extends FrameLayout implements RecyclableView.Root<Data>, Expandable.Root {

    @SuppressWarnings("unused")
    private static final String TAG = "TIRecyclerListItemRoot";

    FrameLayout mItemFrame, mDragAndDropButton;
    RecyclableView<Data> mListItemView;
    ExpandCollapseCallback mExpandCallback;
    int mListItemSize, mExpandedItemSize;

    public TouchInterceptorItemRootView(@NonNull Context context) {
        this(context, null);
    }

    public TouchInterceptorItemRootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchInterceptorItemRootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert(inflater != null);

        inflater.inflate(R.layout.recycler_touch_interceptor_list_item, this, true);


        mDragAndDropButton = findViewById(R.id.recycler_touch_interceptor_item_drag_and_drop_button);
        mItemFrame = findViewById(R.id.recycler_touch_interceptor_item_frame);

        // Start with button hidden
        mDragAndDropButton.setVisibility(View.GONE);

        mListItemSize = getResources().getDimensionPixelSize(R.dimen.normal_item_height);
        mExpandedItemSize = getResources().getDimensionPixelSize(R.dimen.expanded_item_height);

        mItemFrame.setOnClickListener((view) -> listItemClicked());

    }

    public void listItemClicked(){
        if(mListItemView instanceof Expandable){
            if(((Expandable) mListItemView).isExpanded()){
                ((Expandable) mListItemView).shrinkView();
                setItemFrameHeight(mListItemSize);
                mExpandCallback.onStateChange(mListItemView.getIdForAdapter(),
                        ExpandCollapseCallback.COLLAPSED);
            }else {
                ((Expandable) mListItemView).expandView();
                setItemFrameHeight(mExpandedItemSize);
                mExpandCallback.onStateChange(mListItemView.getIdForAdapter(),
                        ExpandCollapseCallback.EXPANDED);
            }
        }
    }

    private void setItemFrameHeight(int h){
        ViewGroup.LayoutParams lp = mItemFrame.getLayoutParams();
        lp.height = h;
        mItemFrame.setLayoutParams(lp);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showDragAndDrop(boolean show){
        if (show) {
            mDragAndDropButton.setVisibility(View.VISIBLE);
            mItemFrame.setOnTouchListener((view, motionEvent) -> true);
            if (mListItemView instanceof Expandable) ((Expandable) mListItemView).shrinkView();

        } else {
            mDragAndDropButton.setVisibility(View.GONE);
            mItemFrame.setOnTouchListener((view, motionEvent) -> false);
        }
    }

     public <V extends View & RecyclableView<Data>> void setItemView(V view){
        mItemFrame.removeAllViews();
        mItemFrame.addView(view);
        mListItemView = view;
    }

    @Override
    public void initWith(Data data) {
        if(mListItemView != null) mListItemView.initWith(data);
    }

    @Override
    public String getIdForAdapter() {
        if(mListItemView != null) return mListItemView.getIdForAdapter();
        else return null;
    }

    @Override
    public boolean isExpanded() {
        return (mListItemView instanceof Expandable && ((Expandable) mListItemView).isExpanded());
    }

    @Override
    public void expandView() {
        if(mListItemView instanceof Expandable) {
            setItemFrameHeight(mExpandedItemSize);
            ((Expandable) mListItemView).expandView();
        }
    }

    @Override
    public void shrinkView() {
        if(mListItemView instanceof Expandable) {
            setItemFrameHeight(mListItemSize);
            ((Expandable) mListItemView).shrinkView();
        }
    }

    @Override
    public void addExpansionCallback(ExpandCollapseCallback callback) {
        mExpandCallback = callback;
    }
}
