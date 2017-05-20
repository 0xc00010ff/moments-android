package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class InterviewingCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the interviewing_cards
     * it holds references to the views to be filled with information from InterviewingCardData
     */

    HolderInteractionListener mActivityCallback;

    CardView mInterviewingCardView;
    ImageView mIntervieweePhotoImageView;
    TextView mIntervieweeNameTextView;
    TextView mIntervieweeRoleTextView;

    // boolean indicating whether the onClickListeners should be set
    boolean mClickable = true;

    /**
     * CONSTRUCTORS
     */

    public InterviewingCardHolder(Context context, View view, boolean clickable) {

        // call the superclass's constructor
        super(view);

        // set the HolderInteractionListener with the context
        mActivityCallback = (HolderInteractionListener) context;

        mClickable = clickable;

        // set all the views
        mInterviewingCardView = (CardView) view;
        mIntervieweePhotoImageView = (ImageView) view.findViewById(R.id.interviewing_card_imageView);
        mIntervieweeNameTextView = (TextView) view.findViewById(R.id.interviewing_card_name_textView);
        mIntervieweeRoleTextView = (TextView) view.findViewById(R.id.interviewing_card_role_textView);

        if(mClickable) {

            // set an onclicklistener on the CardView
            mInterviewingCardView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    // tell the Activity that the card was clicked

                    // use the same callback as when a SectionPrompt for interviewing is clicked
                    // the InterviewingActivity determines whether the user is re-entering with data
                    mActivityCallback.onInterviewingPromptClick();

                }

            });

        }

    }
}
