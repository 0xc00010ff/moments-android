package com.tikkunolam.momentsintime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

public class StateMomentCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a holder for the moment_cards that compose the Community RecyclerView
     * it holds references to the views which will be filled dynamically
     **/

    // the Context
    Context mContext;

    // activity reference through which callbacks are made
    MomentInteractionListener mActivityCallback;

    // Strings to be filled from resources and to fill the momentStateTextView
    String mStateInProgress, mStateUploading, mStateFailed, mStateLive;

    // the CardView containing everything
    CardView wholeCardView;

    ConstraintLayout stateConstraintLayout;

    // the TextView that indicates the state of the Moment
    TextView momentStateTextView;

    // the circle that indicates the state of the Moment
    View coloredCircleView;

    // the ImageView for the mMoment preview
    ImageView videoPreviewImageView;

    // the play button ImageView
    ImageView playButtonImageView;

    // the three dots for the delete dialog
    ImageView dotsImageView;

    // the textView for the mMoment name
    TextView videoNameTextView;

    // the TextView for the mMoment description
    TextView videoDescriptionTextView;

    // the TextView for the share link
    TextView shareTextView;

    public StateMomentCardHolder(Context context, View view) {
        //fill the holder views with those from the view argument

        super(view);

        mContext = context;

        mActivityCallback = (MainActivity) context;

        // fetch the state Strings from resources
        mStateInProgress = context.getString(R.string.state_in_progress);
        mStateUploading = context.getString(R.string.state_uploading);
        mStateFailed = context.getString(R.string.state_failed);
        mStateLive = context.getString(R.string.state_live);

        wholeCardView = (CardView) view;
        momentStateTextView = (TextView) view.findViewById(R.id.moment_state_textView);
        stateConstraintLayout = (ConstraintLayout) view.findViewById(R.id.state_constraintLayout);
        coloredCircleView = view.findViewById(R.id.state_circle_view);
        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_preview_imageView);
        videoNameTextView = (TextView) view.findViewById(R.id.video_name_textView);
        videoDescriptionTextView = (TextView) view.findViewById(R.id.video_description_textView);
        shareTextView = (TextView) view.findViewById(R.id.share_textView);
        dotsImageView = (ImageView) view.findViewById(R.id.dots_imageView);
        playButtonImageView = (ImageView) view.findViewById(R.id.play_button_imageView);


    }

    // fills all relevant views with values provided by the Moment
    public void configureWithMoment(Moment moment) {

        MomentStateEnum state = moment.getMomentState();

        switch(state) {

            case IN_PROGRESS:
                // configure the views with an IN_PROGRESS Moment

                configureInProgress(moment);

                break;

            case UPLOADING:
                // configure the views with an UPLOADING Moment

                configureUploading(moment);

                break;

            case FAILED:
                // configure the views with a FAILED Moment

                configureFailed(moment);

                break;

            case LIVE:
                // configure the views with a LIVE Moment

                configureLive(moment);

                break;

        }

    }

    private void configureInProgress(Moment moment) {
        // configures the views with an IN_PROGRESS Moment

        // hide the shareTextView
        shareTextView.setVisibility(View.INVISIBLE);

        // show the rest of the views in case they were hidden in another configure
        videoNameTextView.setVisibility(View.VISIBLE);
        videoDescriptionTextView.setVisibility(View.VISIBLE);

        // if the Moment has a localVideoUri, fill the videoPreviewImageView with a preview from it
        if(moment.getLocalVideoFilePath() != null) {

            // gets the video file path string from the Moment and makes a file with it
            File videoFile = new File(moment.getLocalVideoFilePath());

            // set the preview image with Glide, using the local video uri
            Glide.with(mContext).load(Uri.fromFile(videoFile)).asBitmap().into(videoPreviewImageView);


        }

        // if the Moment has a title, fill that field
        if(moment.getTitle() != null) {

            videoNameTextView.setText(moment.getTitle());

        }

        // otherwise collapse that view
        else {

            videoNameTextView.setVisibility(View.GONE);

        }

        // if the Moment has a description, fill that field
        if(moment.getDescription() != null) {

            videoDescriptionTextView.setText(moment.getDescription());

        }

        // otherwise collapse that view
        else {

            videoDescriptionTextView.setVisibility(View.GONE);

        }

        momentStateTextView.setText(mStateInProgress);
        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_yellow));

        // set all the onClickListeners
        setOnCardClick(moment);
        setOnVideoClick(moment);
        setOnDotsClick(moment);

    }

    private void configureUploading(Moment moment) {
        // configures the views with an UPLOADING Moment

        // hide the shareTextView
        shareTextView.setVisibility(View.INVISIBLE);

        // show the rest of the views in case they were hidden in another configure
        videoNameTextView.setVisibility(View.VISIBLE);
        videoDescriptionTextView.setVisibility(View.VISIBLE);

        // gets the video file path string from the Moment and makes a file with it
        File videoFile = new File(moment.getLocalVideoFilePath());

        // set the preview image with Glide, using the local video uri
        Glide.with(mContext).load(Uri.fromFile(videoFile)).asBitmap().into(videoPreviewImageView);

        videoNameTextView.setText(moment.getTitle());
        videoDescriptionTextView.setText(moment.getDescription());
        momentStateTextView.setText(mStateUploading);
        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_blue));

        setOnVideoClick(moment);
        setOnDotsClick(moment);

    }

    private void configureFailed(final Moment moment) {
        // configures the views with a FAILED Moment

        // hide the shareTextView
        shareTextView.setVisibility(View.INVISIBLE);

        // gets the video file path string from the Moment and makes a file with it
        File videoFile = new File(moment.getLocalVideoFilePath());

        // set the preview image with Glide, using the local video file
        Glide.with(mContext).load(Uri.fromFile(videoFile)).asBitmap().into(videoPreviewImageView);

        videoNameTextView.setText(moment.getTitle());
        videoDescriptionTextView.setText(moment.getDescription());
        momentStateTextView.setText(mStateFailed);
        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_red));

        // set the stateConstraintLayout's onClick
        stateConstraintLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onFailedStateClick(moment);

            }

        });

        setOnVideoClick(moment);
        setOnDotsClick(moment);

    }

    private void configureLive(Moment moment) {
        // configures the views with a LIVE Moment

        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_green));

        AsyncMomentFetch asyncMomentFetch = new AsyncMomentFetch();

        asyncMomentFetch.execute(moment);

        // set all the onClickListeners
        setOnCardClick(moment);
        setOnVideoClick(moment);
        setOnDotsClick(moment);
        setOnShareClick(moment);

    }

    protected class AsyncMomentFetch extends AsyncTask<Moment, Void, Moment> {
        // fetches a Moment off of Vimeo when it's Live and fills the views with the Moment

        protected Moment doInBackground(Moment... moments) {

            // doInBackground is required to accept variadic arguments, but there will only ever be one...
            // ... so take the Moment from position moments[0]
            Moment moment = moments[0];

            VimeoNetworker vimeoNetworker = new VimeoNetworker(mContext);

            // get the Moment off of Vimeo to populate the views
            Moment newMoment = vimeoNetworker.getSingleMoment(Uri.parse(moment.getVideoUri()));

            moment = newMoment;

            return moment;

        }

        protected void onPostExecute(Moment moment) {

            // if there is a description, set the description_textView's value with it
            // otherwise collapse the description_textView
            if(moment.getDescription() != null) {

                if(moment.getDescription().equals("")) {

                    videoDescriptionTextView.setVisibility(View.GONE);

                }

                else {

                    videoDescriptionTextView.setText(moment.getDescription());
                    videoDescriptionTextView.setVisibility(View.VISIBLE);

                }

            }

            else {

                videoDescriptionTextView.setVisibility(View.GONE);

            }

            // set the rest of the values
            videoNameTextView.setText(moment.getTitle());
            videoNameTextView.setVisibility(View.VISIBLE);
            momentStateTextView.setText(mStateLive);
            shareTextView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(moment.getPictureUrl()).error(mContext.getResources().getDrawable(R.drawable.camera)).into(videoPreviewImageView);

        }

    }

    /**
     * THE ONCLICKS
     */

    private void setOnCardClick(final Moment moment) {
        // sets a listener on the entire card that will call the Activity's onMyMomentCardClick method

        wholeCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onMyMomentCardClick(moment);

            }

        });

    }

    private void setOnShareClick(final Moment moment) {
        // sets a listener on the share button that will call the Activity's onMyShareClick method

        shareTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onMyShareClick(moment);

            }

        });

    }

    private void setOnVideoClick(final Moment moment) {
        // sets a listener on the play button that will call the Activity's onVideoSelect method

        playButtonImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onVideoSelect(moment);

            }

        });

    }

    private void setOnDotsClick(final Moment moment) {
        // sets a listener on the dots that will call the Activity's onMyDotsClick method

        dotsImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onMyDotsClick(moment);

            }

        });

    }

}
