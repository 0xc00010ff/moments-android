package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class DescriptionCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the views in description_card
     * to be filled with data from a DescriptionCardData object
     */

    // get a callback to the Activity
    HolderInteractionListener mActivityCallback;

    CardView mDescriptionCardView;
    TextView descriptionCardTitleTextView;
    TextView descriptionCardDescriptionTextView;

    public DescriptionCardHolder(Context context, View view) {

        // call the superclass's constructor
        super(view);

        // set the HolderInteractionListener from the context
        mActivityCallback = (HolderInteractionListener) context;

        // set the views
        mDescriptionCardView = (CardView) view;

        descriptionCardTitleTextView = (TextView) view.findViewById(R.id.description_card_title_textView);

        descriptionCardDescriptionTextView = (TextView) view.findViewById(R.id.description_card_description_textView);

        // set an onClickListener on the CardView
        mDescriptionCardView.setOnClickListener(new View.OnClickListener() {

           public void onClick(View view) {

               // same callback as when an empty SectionPrompt for description is clicked
               // the DescriptionActivity determines if it was opened with a blank or filled description
               mActivityCallback.onDescriptionPromptClick();

           }

        });

    }

}
