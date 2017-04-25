package com.tikkunolam.momentsintime;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoSectionPromptHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the video_section_prompt_text that appears
     * in the RecyclerView of MakeAMomentActivity
     */

    // ui references
    LinearLayout videoSectionPromptLinearLayout;
    TextView videoPromptTextView;
    TextView uploadPromptTextView;

    public VideoSectionPromptHolder(View view) {

        // call the superclass's constructor
        super(view);

        // initialize the Views
        videoSectionPromptLinearLayout = (LinearLayout) view;
        videoPromptTextView = (TextView) videoSectionPromptLinearLayout.findViewById(R.id.video_prompt_textView);
        uploadPromptTextView = (TextView) videoSectionPromptLinearLayout.findViewById(R.id.upload_prompt_textView);

    }

}
