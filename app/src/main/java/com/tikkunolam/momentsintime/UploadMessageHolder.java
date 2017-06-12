package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class UploadMessageHolder extends RecyclerView.ViewHolder{
    /**
     * the Holder for the views associated with an UploadMessage that will be displayed when a video uploads
     * will hold views from a moment_prompt and they'll be filled with data from an UploadMessage
     */

    MomentInteractionListener mActivityCallback;

    // Textview for the title of the message
    TextView mTitleTextView;

    // Textview for the content of the message
    TextView mContentTextView;

    // TextView for the prompt in the message
    TextView mPromptTextView;

    // the Moment that inspired the UploadMessage
    Moment mMoment;

    public UploadMessageHolder(Context context, View view) {

        // call the super class' constructor
        super(view);

        // set the callback from Context
        mActivityCallback = (MomentInteractionListener) context;

        // set the TextViews from the moment_prompt view
        mTitleTextView = (TextView) view.findViewById(R.id.moment_prompt_textView);
        mContentTextView = (TextView) view.findViewById(R.id.moment_prompt_cont_textView);
        mPromptTextView = (TextView) view.findViewById(R.id.ask_to_interview_textView);

        // set the OnClickListener for the prompt
        mPromptTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onMyShareClick(mMoment);

            }
        });


    }

    public void setMomentWithKey(String primaryKey) {

        mMoment = Moment.findMoment(primaryKey);

    }

}
