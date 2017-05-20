package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MomentCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Adapter for populating the RecyclerViews with moment_cards

    // tag for logging
    final String TAG = "Moment Card Adapter";

    Context mContext;

    // strings to fill in the state in the moment_card_with_state
    String mStateInProgress, mStateUploading, mStateFailed, mStateLive;

    // reference to the Activity for making callbacks in onClick
    MomentInteractionListener mActivityCallback;

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
            // if the Object is a Moment, determine if it has a state and return a ViewType accordingly

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

        Moment moment;

        // determine what type of layout we're going to fill
        switch(holder.getItemViewType()) {

            case MOMENT:
                // fill a momentCardHolder

                // get the Moment from the list
                moment = (Moment) mViewModelList.get(position);

                // cast the generic Holder to a MomentCardHolder
                MomentCardHolder momentCardHolder = (MomentCardHolder) holder;

                // tell the holder to fill its views with the relevant values from the Moment
                momentCardHolder.configureWithMoment(moment);

                break;

            case MOMENT_WITH_STATE:
                // fill a StateMomentCardHolder

                // get the Moment from the list
                moment = (Moment) mViewModelList.get(position);

                // cast the generic Holder to a StateMomentCardHolder
                StateMomentCardHolder stateMomentCardHolder = (StateMomentCardHolder) holder;

                // tell the holder to fill its views with values from the Moment
                stateMomentCardHolder.configureWithMoment(moment);

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

}
