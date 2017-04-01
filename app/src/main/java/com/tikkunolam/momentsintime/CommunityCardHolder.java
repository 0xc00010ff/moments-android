package com.tikkunolam.momentsintime;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CommunityCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a holder for the views that compose the Community RecyclerView
     * it holds references to the views which will be filled dynamically
    **/

    // the ImageView for the video preview
    ImageView videoPreviewImageView;

    // the textView for the video name
    TextView videoNameTextView;

    // the TextView for the video description
    TextView videoDescriptionTextView;

    // the TextView for the share link
    TextView shareTextView;

    public CommunityCardHolder(View view) {
        //fill the holder views with those from the view argument

        super(view);

        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_preview_imageView);
        videoNameTextView = (TextView) view.findViewById(R.id.video_name_textView);
        videoDescriptionTextView = (TextView) view.findViewById(R.id.video_description_textView);
        shareTextView = (TextView) view.findViewById(R.id.share_textView);


    }

}
