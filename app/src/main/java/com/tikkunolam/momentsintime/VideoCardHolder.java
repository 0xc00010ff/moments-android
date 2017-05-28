package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class VideoCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a Holder for the views that make up a video_card
     * to be filled with data from a VideoCardData object
     * and expanded in the RecyclerView of MakeAMomentActivity
     */

    ImageView videoPreviewImageView;
    FrameLayout dotsContainer;
    ImageView dotsImageView;
    ImageView playButtonImageView;

    // a boolean indicating if the onClicks should be added
    boolean clickable = true;

    HolderInteractionListener mActivityCallback;

    public VideoCardHolder(Context context, View view, boolean clickable) {

        // call the superclass's constructor
        super(view);

        // cast the context to a HolderInteractionListener
        mActivityCallback = (HolderInteractionListener) context;

        this.clickable = clickable;

        // fill the views
        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_card_preview_imageView);
        dotsContainer = (FrameLayout) view.findViewById(R.id.video_card_dots_container);
        dotsImageView = (ImageView) view.findViewById(R.id.video_card_dots_imageView);
        playButtonImageView = (ImageView) view.findViewById(R.id.video_card_play_imageView);

        // add a listener to the dots
        if(clickable) {

            dotsContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    mActivityCallback.onVideoDotsClick();

                }

            });

        }

        // add a listener to the play button
        playButtonImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                mActivityCallback.onPlayButtonClick();

            }

        });

    }


}
