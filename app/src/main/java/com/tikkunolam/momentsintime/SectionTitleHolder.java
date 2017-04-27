package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SectionTitleHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the section_title_text that appears in the
     * RecyclerView of the MakeAMomentActivity
     */

    // Activity Context
    Context mContext;

    // Activity callback
    MakeAMomentActivity mActivityCallback;

    // ui references
    FrameLayout sectionTitleFrameLayout;
    TextView sectionTitleTextView;

    public SectionTitleHolder(Context context, View view) {

        // call the superclass's constructor
        super(view);

        // set the Context
        mContext = context;

        // initialize the views
        sectionTitleFrameLayout = (FrameLayout) view;
        sectionTitleTextView = (TextView) sectionTitleFrameLayout.findViewById(R.id.section_title_textView);

    }

}
