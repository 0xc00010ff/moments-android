package com.tikkunolam.momentsintime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Case;

public class MomentCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Adapter for populating the RecyclerViews with moment_cards

    // tag for logging
    final String TAG = "Moment Card Adapter";

    Context mContext;

    // strings to fill in the state in the moment_card_with_state
    String mStateInProgress, mStateUploading, mStateFailed, mStateLive;

    // reference to the Activity for making callbacks in onClick
    FragmentInteractionListener mActivityCallback;

    // ArrayList of objects, holding Moments and MomentPrompts
    ArrayList<Object> mViewModelList;

    // integers to identify the Object in the ArrayList
    final int MOMENT = 1;
    final int PROMPT = 2;
    final int MOMENT_WITH_STATE = 3;


    public MomentCardAdapter(Context context, ArrayList<Object> viewModelList) {

        mContext = context;
        mActivityCallback = (MainActivity) context;
        mViewModelList = viewModelList;

        // get the Strings from resources
        mStateInProgress = context.getString(R.string.state_in_progress);
        mStateUploading = context.getString(R.string.state_uploading);
        mStateFailed = context.getString(R.string.state_failed);
        mStateLive = context.getString(R.string.state_live);

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mViewModelList.size();

    }

    @Override
    public int getItemViewType(int position) {
        // determine what type of object is in the current position of the list

        if(mViewModelList.get(position) instanceof Moment) {

            Moment moment = (Moment) mViewModelList.get(position);

            if(moment.getState() != null) {
                // there is a state

                return MOMENT_WITH_STATE;

            }

            else {

                return MOMENT;

            }

        }

        else if(mViewModelList.get(position) instanceof MomentPrompt) {

            return PROMPT;

        }

        // if it makes it to here it found an unidentified viewtype. will never happen. return -1
        return -1;

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        /**
         * get the view from the ViewGroup and create and return a Holder with it
         * passes the Holder it creates to onBindViewHolder ^
         */

        // generic ViewHolder for either type of Holder
        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType) {

            case PROMPT:
                // if the list calls for a prompt, inflate one and pass it to a new MomentPromptHolder

                // inflate a moment_prompt
                View promptItemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.moment_prompt, parent, false);

                // set the viewHolder as a MomentPromptHolder
                viewHolder = new MomentPromptHolder(mContext, promptItemView);

                break;

            case MOMENT:
                // if it calls for a Moment, inflate a moment_card and pass it to a new MomentCardHolder

                // inflate a moment_card
                View momentItemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.moment_card, parent, false);

                // set the viewHolder as a MomentCardHolder
                viewHolder = new MomentCardHolder(mContext, momentItemView);

                break;

            case MOMENT_WITH_STATE:
                // if it calls for a MOMENT_WITH_STATE, inflate a moment_card_with_state and pass it to a StateMomentCardHolder

                // inflate a moment_card_with_state
                View momentWithStateView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.moment_card_with_state, parent, false);

                // set the viewHolder as a StateMomentCardHolder
                viewHolder = new StateMomentCardHolder(mContext, momentWithStateView);

        }

        return viewHolder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // fill the views contained in the holder with their intended values

        switch(holder.getItemViewType()) {

            case MOMENT:
                // fill a momentCardHolder

                MomentCardHolder momentCardHolder = (MomentCardHolder) holder;

                bindCommunityMoment(momentCardHolder, position);

                break;

            case MOMENT_WITH_STATE:
                // fill a StateMomentCardHolder

                StateMomentCardHolder stateMomentCardHolder = (StateMomentCardHolder) holder;

                bindLocalMoment(stateMomentCardHolder, position);

                break;

            case PROMPT:
                // set a MomentPromptHolder

                MomentPromptHolder momentPromptHolder = (MomentPromptHolder) holder;

                // cast the generic Object to a MomentPrompt
                MomentPrompt momentPrompt = (MomentPrompt) mViewModelList.get(position);

                // fill the Holder with the MomentPrompt's values
                momentPromptHolder.moment_prompt_textView.setText(momentPrompt.getMakeAMomentString());
                momentPromptHolder.moment_prompt_cont_textView.setText(momentPrompt.getWhoString());
                momentPromptHolder.ask_to_interview_textView.setText(momentPrompt.getAskToInterviewString());


        }

    }

    private void bindLocalMoment(StateMomentCardHolder momentCardHolder, final int position) {
        // bind a local Moment to a MomentCardHolder

        Moment moment = (Moment) mViewModelList.get(position);

        MomentStateEnum stateEnum = moment.getMomentState();

        switch(stateEnum) {

            case IN_PROGRESS:
                // Moment is still being created

                // if the Moment has a localVideoUri, fill the videoPreviewImageView with a preview from it
                if(moment.getLocalVideoUri() != null) {

                    // set the preview image with Glide, using the local video uri
                    Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(momentCardHolder.videoPreviewImageView);


                }

                // if the Moment has a title, fill that field in the holder
                if(moment.getTitle() != null) {

                    momentCardHolder.videoNameTextView.setText(moment.getTitle());

                }

                // otherwise collapse that view
                else {

                    momentCardHolder.videoNameTextView.setVisibility(View.GONE);

                }

                // if the Moment has a description, fill that field in the Holder
                if(moment.getDescription() != null) {

                    momentCardHolder.videoDescriptionTextView.setText(moment.getDescription());

                }

                // otherwise collapse that view
                else {

                    momentCardHolder.videoDescriptionTextView.setVisibility(View.GONE);

                }

                momentCardHolder.momentStateTextView.setText(mStateInProgress);
                momentCardHolder.coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_yellow));

                break;

            case UPLOADING:
                // Moment is uploading currently

                // set the preview image with Glide, using the local video uri
                Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(momentCardHolder.videoPreviewImageView);

                momentCardHolder.videoNameTextView.setText(moment.getTitle());
                momentCardHolder.videoDescriptionTextView.setText(moment.getDescription());
                momentCardHolder.momentStateTextView.setText(mStateUploading);
                momentCardHolder.coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_blue));

                break;

            case FAILED:
                // Moment upload failed

                // set the preview image with Glide, using the local video uri
                Glide.with(mContext).load(moment.getLocalVideoUri()).asBitmap().into(momentCardHolder.videoPreviewImageView);

                momentCardHolder.videoNameTextView.setText(moment.getTitle());
                momentCardHolder.videoDescriptionTextView.setText(moment.getDescription());
                momentCardHolder.momentStateTextView.setText(mStateFailed);
                momentCardHolder.coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_red));

                break;

            case LIVE:
                // Moment is live on Vimeo

                momentCardHolder.coloredCircleView.setBackground(mContext.getResources().getDrawable(R.drawable.circle_green));
                AsyncArgument asyncArgument = new AsyncArgument(moment, momentCardHolder);
                AsyncMomentFetch asyncMomentFetch = new AsyncMomentFetch();
                asyncMomentFetch.execute(asyncArgument);

                break;

        }


    }

    private void bindCommunityMoment(MomentCardHolder momentCardHolder, final int position) {
        // bind a Moment, received from Vimeo to a MomentCardHolder

        Moment moment = (Moment) mViewModelList.get(position);

        // use Picasso to fill the videoPreviewImageView from the mMoment's picture url
        // fill this before the rest so the loading doesn't look silly
        Picasso.with(mContext).load(moment.getPictureUrl()).into(momentCardHolder.videoPreviewImageView);

        // set the text in the videoNameTextView from the mMoment
        momentCardHolder.videoNameTextView.setText(moment.getTitle());

        // if there is a description set it, otherwise delete the view
        String description = moment.getDescription();

        if(!description.equals("")) {

            momentCardHolder.videoDescriptionTextView.setText(description);

        }
        else {

            momentCardHolder.videoDescriptionTextView.setVisibility(View.GONE);

        }

        // set the onClick for the Moments
        momentCardHolder.videoPreviewImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                mActivityCallback.onMomentSelect((Moment) mViewModelList.get(position));

            }

        });

    }

    protected class AsyncMomentFetch extends AsyncTask<AsyncArgument, Void, AsyncArgument> {

        protected AsyncArgument doInBackground(AsyncArgument... asyncArgument) {

            VimeoNetworker vimeoNetworker = new VimeoNetworker(mContext);

            Moment newMoment = vimeoNetworker.getSingleMoment(Uri.parse(asyncArgument[0].getMoment().getVideoUri()));

            asyncArgument[0].setMoment(newMoment);

            return asyncArgument[0];

        }

        protected void onPostExecute(AsyncArgument asyncArgument) {

            StateMomentCardHolder holder = asyncArgument.getHolder();
            Moment moment = asyncArgument.getMoment();

            if(moment.getDescription().equals("")) {

                holder.videoDescriptionTextView.setVisibility(View.GONE);

            }
            holder.videoDescriptionTextView.setText(moment.getDescription());
            holder.videoNameTextView.setText(moment.getTitle());
            holder.momentStateTextView.setText(mStateLive);
            Picasso.with(mContext).load(moment.getPictureUrl()).into(holder.videoPreviewImageView);

        }

    }

    private class AsyncArgument {

        Moment moment;
        StateMomentCardHolder holder;

        public AsyncArgument(Moment moment, StateMomentCardHolder holder) {

            this.moment = moment;
            this.holder = holder;

        }

        public Moment getMoment() {

            return moment;

        }

        public StateMomentCardHolder getHolder() {

            return holder;

        }

        public void setMoment(Moment moment) {

            this.moment = moment;

        }

    }

}
