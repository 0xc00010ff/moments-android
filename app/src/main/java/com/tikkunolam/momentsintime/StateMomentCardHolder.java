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

public class StateMomentCardHolder extends RecyclerView.ViewHolder{
    /**
     * this class is a holder for the moment_cards that compose the Community RecyclerView
     * it holds references to the views which will be filled dynamically
     **/

    // the Context
    Context mContext;

    // activity reference through which callbacks are made
    FragmentInteractionListener mActivityCallback;

    // Strings to be filled from resources and to fill the momentStateTextView
    String mStateInProgress, mStateUploading, mStateFailed, mStateLive;

    // the CardView containing everything
    CardView wholeCardView;

    // the TextView that indicates the state of the Moment
    TextView momentStateTextView;

    // the circle that indicates the state of the Moment
    View coloredCircleView;

    // the ImageView for the mMoment preview
    ImageView videoPreviewImageView;

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
        coloredCircleView = view.findViewById(R.id.state_circle_view);
        videoPreviewImageView = (ImageView) view.findViewById(R.id.video_preview_imageView);
        videoNameTextView = (TextView) view.findViewById(R.id.video_name_textView);
        videoDescriptionTextView = (TextView) view.findViewById(R.id.video_description_textView);
        shareTextView = (TextView) view.findViewById(R.id.share_textView);
        dotsImageView = (ImageView) view.findViewById(R.id.dots_imageView);


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

        // if the Moment has a localVideoUri, fill the videoPreviewImageView with a preview from it
        if(moment.getLocalVideoUri() != null) {

            // set the preview image with Glide, using the local video uri
            Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(videoPreviewImageView);

            // set an onClickListener on the play button to play the video

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

    }

    private void configureUploading(Moment moment) {
        // configures the views with an UPLOADING Moment

        // set the preview image with Glide, using the local video uri
        Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(videoPreviewImageView);

        videoNameTextView.setText(moment.getTitle());
        videoDescriptionTextView.setText(moment.getDescription());
        momentStateTextView.setText(mStateUploading);
        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_blue));

    }

    private void configureFailed(Moment moment) {
        // configures the views with a FAILED Moment

        // set the preview image with Glide, using the local video uri
        Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(videoPreviewImageView);

        videoNameTextView.setText(moment.getTitle());
        videoDescriptionTextView.setText(moment.getDescription());
        momentStateTextView.setText(mStateFailed);
        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_red));

    }

    private void configureLive(Moment moment) {
        // configures the views with a LIVE Moment

        coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_green));

        AsyncMomentFetch asyncMomentFetch = new AsyncMomentFetch();

        asyncMomentFetch.execute(moment);

    }

    protected class AsyncMomentFetch extends AsyncTask<Moment, Void, Moment> {
        // fetches a Moment off of Vimeo when it's Live and fills the views with the Moment

        protected Moment doInBackground(Moment... momentArg) {

            // take the Moment
            Moment moment = momentArg[0];

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

                }

            }

            else {

                videoDescriptionTextView.setVisibility(View.GONE);

            }

            // set the rest of the values
            videoNameTextView.setText(moment.getTitle());
            momentStateTextView.setText(mStateLive);
            Picasso.with(mContext).load(moment.getPictureUrl()).error(mContext.getResources().getDrawable(R.drawable.camera)).into(videoPreviewImageView);

        }

    }

}
