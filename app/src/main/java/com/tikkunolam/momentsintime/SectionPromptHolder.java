package com.tikkunolam.momentsintime;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SectionPromptHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the section_prompt_text that appears
     * in the RecyclerView of the MakeAMomentActivity
     */

    // ui references
    FrameLayout sectionPromptFrameLayout;
    TextView sectionPromptTextView;

    public SectionPromptHolder(View view) {

        // call the superclass's constructor
        super(view);

        // initialize the Views
        sectionPromptFrameLayout = (FrameLayout) view;
        sectionPromptTextView = (TextView) sectionPromptFrameLayout.findViewById(R.id.section_prompt_textView);

    }

}
