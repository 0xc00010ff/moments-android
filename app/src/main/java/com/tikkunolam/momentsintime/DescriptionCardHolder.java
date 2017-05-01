package com.tikkunolam.momentsintime;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class DescriptionCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the views in description_card
     * to be filled with data from a DescriptionCardData object
     */

    TextView descriptionCardTitleTextView;
    TextView descriptionCardDescriptionTextView;

    public DescriptionCardHolder(View view) {

        // call the superclass's constructor
        super(view);

        // set the views
        descriptionCardTitleTextView = (TextView) view.findViewById(R.id.description_card_title_textView);

        descriptionCardDescriptionTextView = (TextView) view.findViewById(R.id.description_card_description_textView);

    }

}
