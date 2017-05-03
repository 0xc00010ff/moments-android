package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class VideoCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the views that make up a video_card
     * to be filled with data from a VideoCardData object
     * and expanded in the RecyclerView of MakeAMomentActivity
     */

    ImageView videoPreviewImageView;
    ImageView dotsImageView;

    HolderInteractionListener mActivityCallback;

    public VideoCardHolder(Context context, View view) {

        // call the superclass's constructor
        super(view);

        // cast the context to a HolderInteractionListener
        mActivityCallback = (HolderInteractionListener) context;

        // fill the views
        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_card_preview_imageView);
        dotsImageView = (ImageView) view.findViewById(R.id.video_card_dots_imageView);

        // add a listener to the dots
        dotsImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                mActivityCallback.onVideoDotsClick();

            }

        });

    }


}
