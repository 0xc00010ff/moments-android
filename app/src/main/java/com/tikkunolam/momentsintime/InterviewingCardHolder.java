package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class InterviewingCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the interviewing_cards
     * it holds references to the views to be filled with information from InterviewingCardData
     */

    ImageView mIntervieweePhotoImageView;
    TextView mIntervieweeNameTextView;
    TextView mIntervieweeRoleTextView;

    /**
     * CONSTRUCTORS
     */

    public InterviewingCardHolder(View view) {

        // call the superclass's constructor
        super(view);

        // set all the views
        mIntervieweePhotoImageView = (ImageView) view.findViewById(R.id.interviewing_card_imageView);
        mIntervieweeNameTextView = (TextView) view.findViewById(R.id.interviewing_card_name_textView);
        mIntervieweeRoleTextView = (TextView) view.findViewById(R.id.interviewing_card_role_textView);

    }
}
