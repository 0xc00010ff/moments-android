package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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
    ConstraintLayout mConstraintLayout;
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
        mConstraintLayout = (ConstraintLayout) view.findViewById(R.id.interviewing_constraintLayout);
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

    public void removeRole() {
        // constrain the interviewee name to the bottom of the ConstraintLayout, for when role is made GONE

        // remove the role view
        mIntervieweeRoleTextView.setVisibility(View.GONE);

        // make a new ConstraintSet and copy the constraints of the original
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mConstraintLayout);

        // connect the name view to the bottom
        constraintSet.connect(R.id.interviewing_card_name_textView, ConstraintSet.BOTTOM, R.id.interviewing_constraintLayout, ConstraintSet.BOTTOM);

        // apply the ConstraintSet to the ConstraintLayout
        constraintSet.applyTo(mConstraintLayout);

    }

    public void addRole() {
        // make the role visible and change the constraints to constrain interviewee to role

        // make it visible
        mIntervieweeRoleTextView.setVisibility(View.VISIBLE);

        // make a new ConstraintSet, copying the constraints of the original
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mConstraintLayout);

        // constrain the interviewee to the role
        constraintSet.connect(R.id.interviewing_card_name_textView, ConstraintSet.BOTTOM, R.id.interviewing_card_role_textView, ConstraintSet.TOP);

        // apply the ContraintSet to the ConstraintLayout
        constraintSet.applyTo(mConstraintLayout);


    }

}
