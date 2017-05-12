package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StateMomentCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a holder for the moment_cards that compose the Community RecyclerView
     * it holds references to the views which will be filled dynamically
     **/

    // activity reference through which callbacks are made
    FragmentInteractionListener mActivityCallback;

    // the TextView that indicates the state of the Moment
    TextView momentStateTextView;

    // the circle that indicates the state of the Moment
    View coloredCircleView;

    // the ImageView for the mMoment preview
    ImageView videoPreviewImageView;

    // the textView for the mMoment name
    TextView videoNameTextView;

    // the TextView for the mMoment description
    TextView videoDescriptionTextView;

    // the TextView for the share link
    TextView shareTextView;

    public StateMomentCardHolder(Context context, View view) {
        //fill the holder views with those from the view argument

        super(view);

        mActivityCallback = (MainActivity) context;

        momentStateTextView = (TextView) view.findViewById(R.id.moment_state_textView);
        coloredCircleView = view.findViewById(R.id.state_circle_view);
        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_preview_imageView);
        videoNameTextView = (TextView) view.findViewById(R.id.video_name_textView);
        videoDescriptionTextView = (TextView) view.findViewById(R.id.video_description_textView);
        shareTextView = (TextView) view.findViewById(R.id.share_textView);

        // set a listener on the videoPreviewImageView to tell the Activity what clicked it and what to do
        videoPreviewImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


            }

        });

    }

}
