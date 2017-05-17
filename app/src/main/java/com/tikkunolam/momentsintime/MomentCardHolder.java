package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MomentCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a holder for the moment_cards that compose the Community RecyclerView
     * it holds references to the views which will be filled dynamically
    **/

    Context mContext;

    // activity reference through which callbacks are made
    MomentInteractionListener mActivityCallback;

    // the ImageView for the mMoment preview
    ImageView videoPreviewImageView;

    // the ImageView for the dots
    ImageView dotsImageView;

    // the textView for the mMoment name
    TextView videoNameTextView;

    // the TextView for the mMoment description
    TextView videoDescriptionTextView;

    // the TextView for the share link
    TextView shareTextView;

    public MomentCardHolder(Context context, View view) {
        //fill the holder views with those from the view argument

        super(view);

        mContext = context;

        mActivityCallback = (MainActivity) context;

        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_preview_imageView);
        videoNameTextView = (TextView) view.findViewById(R.id.video_name_textView);
        videoDescriptionTextView = (TextView) view.findViewById(R.id.video_description_textView);
        shareTextView = (TextView) view.findViewById(R.id.share_textView);
        dotsImageView = (ImageView) view.findViewById(R.id.dots_imageView);

    }

    // fills all the views with values provided by the Moment
    // code that might usually be in onBindViewHolder of MomentCardAdapter
    public void configureWithMoment(final Moment moment) {

        // use Picasso to fill the videoPreviewImageView from the mMoment's picture url
        // fill this before the rest so the loading doesn't look silly
        Picasso.with(mContext).load(moment.getPictureUrl()).into(videoPreviewImageView);

        // set the text in the videoNameTextView from the mMoment
        videoNameTextView.setText(moment.getTitle());

        // if there is a description set it, otherwise delete the view
        String description = moment.getDescription();

        if(!description.equals("")) {

            videoDescriptionTextView.setText(description);

        }
        else {

            videoDescriptionTextView.setVisibility(View.GONE);

        }

        // set the onClick for the Moments
        videoPreviewImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                mActivityCallback.onVideoSelect(moment);

            }

        });

        // set the onClick for the share
        shareTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onCommunityShareClick(moment);

            }

        });

        // set the onClick for the dots
        dotsImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onCommunityDotsClick(moment);

            }

        });


    }

}
