package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * A listener class to be set on the RecyclerViews
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    /**
     * INSTANCE VARIABLES
     */

    // the listener providing us access to the calling fragment
    private OnItemClickListener mListener;

    // the GestureDetector
    GestureDetector mGestureDetector;


    /**
     * CONSTRUCTORS
     */

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {

        // set the listener
        mListener = listener;

        // set the gesture detector to detect single taps. if necessary other interactions can be added here
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                //let it be known that we supply functionality for a single tap interaction
                return true;

            }

        });

    }

    /**
     * METHODS
     */

    // called when any touch event occurs on the RecyclerView
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        // deal with the actual touch events

        // determine which cell within the RecyclerView was actually touched
        View childView = view.findChildViewUnder(event.getX(), event.getY());

        // check that the interaction was one that we actually supply functionality for
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(event)) {

            // if so, call the listener's corresponding method
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));

            return true;

        }

        // otherwise it's detected an event we don't support. can't do anything
        return false;

    }

    // necessary to implement OnItemTouchListener. not used here
    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    // necessary to implement OnItemTouchListener. not used here
    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){ }


    /**
     * INTERFACES
     */

    // interface to be implemented by any RecyclerView that wishes to respond to single taps
    public interface OnItemClickListener {

        public void onItemClick(View view, int position);

    }
}
