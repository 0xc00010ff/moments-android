package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TopicCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the views in topic_card
     * to be filled with data from a DescriptionCardData object
     */

    // get a callback to the Activity
    HolderInteractionListener mActivityCallback = null;

    CardView mTopicCardView;
    TextView mTopicCardTitleTextView;
    TextView mTopicCardDescriptionTextView;

    public TopicCardHolder(Context context, View view) {

        // call the superclass's constructor
        super(view);

        // if the MakeAMomentActivity owns this Holder, then set the mActivityCallback accordingly
        if(context instanceof MakeAMomentActivity) {

            // set the HolderInteractionListener from the context
            mActivityCallback = (HolderInteractionListener) context;

        }

        // set the views
        mTopicCardView = (CardView) view;

        mTopicCardTitleTextView = (TextView) view.findViewById(R.id.topic_card_title_textView);

        mTopicCardDescriptionTextView = (TextView) view.findViewById(R.id.topic_card_description_textView);

        // set an onClickListener on the CardView

        // if the mActivityCallback isn't null, then MakeAMomentActivity owns this Holder, and the following onClick is appropriate
        if(mActivityCallback != null) {

            mTopicCardView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    // alert the Activity a Topic was chosen
                    mActivityCallback.onTopicPromptClick();

                }

            });

        }

    }

}
