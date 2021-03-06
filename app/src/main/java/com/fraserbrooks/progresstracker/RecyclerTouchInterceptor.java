package com.fraserbrooks.progresstracker;
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

@SuppressWarnings("unused")
public class RecyclerTouchInterceptor extends RecyclerListView {

    private static final int INVALID_POSITION = -1;
    private final String TAG = "TouchInterceptor";

    private ImageView mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    /**
     * At which position is the item currently being dragged. Note that this
     * takes in to account header items.
     */
    private int mDragPos;
    /**
     * At which position was the item being dragged originally
     */
    private int mSrcDragPos;

    private int mTopDragPos;
    private int mBottomDragPos;

    private float mDragPointX; // at what x offset inside the item did the user grab it
    private float mDragPointY; // at what y offset inside the item did the user grab it
    private float mXOffset; // the difference between screen coordinates and coordinates in this view
    private float mYOffset; // the difference between screen coordinates and coordinates in this view
    private DragListener mDragListener;
    private DropListener mDropListener;
    private RemoveListener mRemoveListener;
    private float mUpperBound;
    private float mLowerBound;
    private int mHeight;
    private GestureDetector mGestureDetector;
    private static final int FLING = 0;
    private static final int SLIDE = 1;
    private static final int TRASH = 2;
    private int mRemoveMode = -1;
    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightWhileDragging;
    private int mDragAndDropButtonWidth;
    private Drawable mTrashcan;

    private boolean mDragEnabled = true;

    public RecyclerTouchInterceptor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Resources res = getResources();
        mItemHeightNormal = res.getDimensionPixelSize(R.dimen.normal_item_height);
        mItemHeightWhileDragging = res.getDimensionPixelSize(R.dimen.normal_item_drag_height);
        mDragAndDropButtonWidth = res.getDimensionPixelSize(R.dimen.icon_height);
        this.setHasFixedSize(true);
    }

    public void setDragEnabled(boolean enabled){
        boolean old = mDragEnabled;
        mDragEnabled = enabled;
        if(getAdapter() != null){
            getAdapter().setDragAndDropEnabled(enabled);
            if(mDragEnabled != old) getAdapter().notifyDataSetChanged();
        }
    }


    public boolean dragEnabled(){return mDragEnabled;}

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if(!mDragEnabled) return super.onInterceptTouchEvent(ev);

        if (mRemoveListener != null && mGestureDetector == null) {
            if (mRemoveMode == FLING) {
                mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(
                            MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (mDragView != null) {
                            if (velocityX > 1000) {
                                Rect r = mTempRect;
                                mDragView.getDrawingRect(r);
                                if (e2.getX() > r.right * 2 / 3) {
                                    // fast fling right with release near the right edge of the
                                    // screen
                                    stopDragging();
                                    mRemoveListener.remove(mSrcDragPos);
                                    unExpandViews(true);
                                }
                            }
                            // flinging while dragging should have no effect
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        if (mDragListener != null || mDropListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = ev.getX();
                    float y = ev.getY();
                    View childView = findChildViewUnder(x, y);
                    if (childView == null) {
                        break;
                    }
                    int itemnum = getChildAdapterPosition(childView);

                    // Don't drag headers or footers
                    if(itemnum < getHeaderViewsCount() || itemnum > (getCount() - 1) - getFooterViewsCount()){
                        break;
                    }


                    ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());


                    mDragPointX = x - item.getLeft();
                    mDragPointY = y - item.getTop();

                    mTopDragPos = itemnum;
                    mBottomDragPos = itemnum;

                    mXOffset = ev.getRawX() - x;
                    mYOffset = ev.getRawY() - y;
                    // The left side of the item is the grabber for dragging the item
                    if (x < mDragAndDropButtonWidth) {
                        item.setDrawingCacheEnabled(true);
                        // Create a copy of the drawing cache so that it does not get recycled
                        // by the framework when the list tries to clean up memory
                        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
                        item.setDrawingCacheEnabled(false);

                        startDragging(bitmap, x, y);
                        mDragPos = itemnum;
                        mSrcDragPos = mDragPos;
                        mHeight = getHeight();
                        int touchSlop = mTouchSlop;
                        mUpperBound = Math.min(y - touchSlop, mHeight / 3);
                        mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
                        return false;
                    }
                    stopDragging();
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
     * pointToPosition() doesn't consider invisible views, but we
     * need to, so implement a slightly different version.
     */
    private int myPointToPosition(int x, int y) {
        if (y < 0) {
            // when dragging off the top of the screen, calculate position
            // by going back from a visible item
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos > 0) {
                return pos - 1;
            }
        }

        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(float y) {

        float itemTopY = y - mDragPointY;
        float itemBottomY = itemTopY + mItemHeightNormal - mItemHeightNormal/4;

        int newTop = myPointToPosition(0, (int) itemTopY);
        int newBottom = myPointToPosition(0,(int) itemBottomY);

        if (newTop == -1) newTop = mTopDragPos;
        if(newBottom == -1) newBottom = mBottomDragPos;

        int pos;

        if(newTop < mTopDragPos){
            pos =  newTop - 1;
        } else if(newBottom > mBottomDragPos){
            pos = newBottom;
        } else  pos = myPointToPosition(0, (int)(itemTopY + itemBottomY)/2);

        mTopDragPos = newTop  -1;
        mBottomDragPos = newBottom;

        if (itemTopY < 0) {
            // this shouldn't happen anymore now that myPointToPosition deals
            // with this situation
            pos = 0;
        }
        return pos;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 3) {
            mUpperBound = mHeight / 3;
        }
        if (y <= mHeight * 2 / 3) {
            mLowerBound = mHeight * 2 / 3;
        }
    }

    /*
     * Restore size and visibility for all list items
     */
    private void unExpandViews(boolean deletion) {
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                if (deletion) {
                    // HACK force update of mItemCount
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                    // end hack
                }
                try {
                    //layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                }
                if (v == null) {
                    return;
                }
            }
            if(v.getId() == R.id.recycler_touch_interceptor_list_item){
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                v.setLayoutParams(params);
                v.setVisibility(View.VISIBLE);
            }

        }
    }

    /* Adjust visibility and size to make it appear as though
     * an item is being dragged around and other items are making
     * room for it:
     * If dropping the item would result in it still being in the
     * same place, then make the dragged list item's size normal,
     * but make the item invisible.
     * Otherwise, if the dragged list item is still on screen, make
     * it as small as possible and expand the item below the insert
     * point.
     * If the dragged item is not on screen, only expand the item
     * below the current insert point.
     */
    private void doExpansion() {
        int childnum = mDragPos - getFirstVisiblePosition();

        int numheaders = getHeaderViewsCount();

        View viewBeingDragged = getChildAt(mSrcDragPos - getFirstVisiblePosition());

        // getChildAt(i) returns the ith element on the screen
        for (int i = 0;; i++) {
            View vv = getChildAt(i);
            if (vv == null) {
                break;
            }

            if(getPositionForView(vv) < numheaders){
                Log.d(TAG, "doExpansion: headerView@ " + i);
                continue;
            }else{
                Log.d(TAG, "doExpansion: i = " + i);
            }

            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int visibility = View.VISIBLE;
            if (mDragPos < numheaders && i - getFirstVisiblePosition() == numheaders) {
                // dragging on top of the header item, so no need to expand anything
                if (vv.equals(viewBeingDragged)) {
                    Log.d(TAG, "doExpansion: over headers i = " + i );
                    visibility = View.INVISIBLE;
                }
            } else if (vv.equals(viewBeingDragged)) {
                // processing the item that is being dragged
                Log.d(TAG, "doExpansion: processing item being dragged i = " + i);
                if (mDragPos == mSrcDragPos || getPositionForView(vv) == getCount() - 1) {
                    // hovering over the original location
                    visibility = View.INVISIBLE;
                } else {
                    // not hovering over it
                    // Ideally the item would be completely gone, but neither
                    // setting its size to 0 nor settings visibility to GONE
                    // has the desired effect.
                    height = 1;
                }
            } else if (i == childnum) {
                Log.d(TAG, "doExpansion: processing shadow child i = " + i);
                // childnum is the view we are hovering over
                if (mDragPos >= numheaders && mDragPos < (getCount())
                        // Don't expand footers
                        - getFooterViewsCount()) {
                    height = mItemHeightWhileDragging;
                }
            }
            ViewGroup.LayoutParams params = vv.getLayoutParams();
            params.height = height;
            vv.setLayoutParams(params);
            vv.setVisibility(visibility);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        if ((mDragListener != null || mDropListener != null) && mDragView != null) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Rect r = mTempRect;
                    mDragView.getDrawingRect(r);
                    stopDragging();
                    if (mRemoveMode == SLIDE && ev.getX() > r.right * 3 / 4) {
                        if (mRemoveListener != null) {
                            mRemoveListener.remove(mSrcDragPos);
                        }
                        unExpandViews(true);
                    } else {
                        if (mDropListener != null && mDragPos >= 0 && mDragPos < getCount()) {
                            mDropListener.drop(mSrcDragPos, mDragPos);
                        }
                        unExpandViews(false);
                    }
                    break;

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();

                    dragView(x, y);
                    int itemnum = getItemForPosition(y);
                    if (itemnum >= 0) {
                        if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
                            if (mDragListener != null) {
                                mDragListener.drag(mDragPos, itemnum);
                            }
                            mDragPos = itemnum;
                            doExpansion();
                        }
                        int speed = 0;
                        adjustScrollBounds(y);
                        if (y > mLowerBound) {
                            // scroll the list up a bit
                            if (getLastVisiblePosition() < getCount() - 1) {
                                speed = y > (mHeight + mLowerBound) / 2 ? 50 : 25;
                            } else {
                                speed = 8;
                            }
                        } else if (y < mUpperBound) {
                            // scroll the list down a bit
                            speed = y < mUpperBound / 2 ? -50 : -25;
                            if (getFirstVisiblePosition() == 0
                                    && getChildAt(0).getTop() >= getPaddingTop()) {
                                // if we're already at the top, don't try to scroll, because
                                // it causes the framework to do some extra drawing that messes
                                // up our animation
                                speed = 0;
                            }
                        }
                        if (speed != 0) {
                            smoothScrollBy(speed, 15);
                        }
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void startDragging(Bitmap bm, float x, float y) {
        stopDragging();


        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        mWindowParams.x = (int) (x - mDragPointX + mXOffset);
        mWindowParams.y = (int )(y - mDragPointY + mYOffset);

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);


        v.setPadding(0, 0, 0, 0);
        v.setImageBitmap(bm);

        Log.d(TAG, "startDragging: Bitmap changed");
        mDragBitmap = bm;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    private void dragView(float x, float y) {
        if (mRemoveMode == SLIDE) {
            float alpha = 1.0f;
            int width = mDragView.getWidth();
            if (x > width / 2) {
                alpha =  (width - x) / (width / 2);
            }
            mWindowParams.alpha = alpha;
        }

        if (mRemoveMode == FLING || mRemoveMode == TRASH) {
            mWindowParams.x = (int)(x - mDragPointX + mXOffset);
        } else {
            mWindowParams.x = 0;
        }
        mWindowParams.y = (int) (y - mDragPointY + mYOffset);
        mWindowManager.updateViewLayout(mDragView, mWindowParams);

        if (mTrashcan != null) {
            int width = mDragView.getWidth();
            if (y > getHeight() * 3 / 4) {
                mTrashcan.setLevel(2);
            } else if (width > 0 && x > width / 4) {
                mTrashcan.setLevel(1);
            } else {
                mTrashcan.setLevel(0);
            }
        }
    }

    private void stopDragging() {
        if (mDragView != null) {
            mDragView.setVisibility(GONE);
            WindowManager wm =
                    (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
        if (mTrashcan != null) {
            mTrashcan.setLevel(0);
        }
    }

    public void setTrashcan(Drawable trash) {
        mTrashcan = trash;
        mRemoveMode = TRASH;
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    public interface DragListener { void drag(int from, int to); }
    public interface DropListener { void drop(int from, int to); }
    public interface RemoveListener { void remove(int which); }
}