package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MomentCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Adapter for populating the RecyclerViews with moment_cards

    Context mContext;

    // reference to the Activity for making callbacks in onClick
    FragmentInteractionListener mActivityCallback;


    // ArrayList of objects, holding Moments and MomentPrompts
    ArrayList<Object> mViewModelList;


    // integers to identify the Object in the ArrayList
    final int MOMENT = 1;
    final int PROMPT = 2;


    public MomentCardAdapter(Context context, ArrayList<Object> viewModelList) {

        mContext = context;
        mActivityCallback = (MainActivity) context;
        mViewModelList = viewModelList;

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mViewModelList.size();

    }

    @Override
    public int getItemViewType(int position) {
        // determine what type of object is in the current position of the list

        if(mViewModelList.get(position) instanceof Moment) {

            return MOMENT;

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

        }

        return viewHolder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // fill the views contained in the holder with their intended values

        switch(holder.getItemViewType()) {

            case MOMENT:
                // fill a momentCardHolder

                MomentCardHolder momentCardHolder = (MomentCardHolder) holder;

                // get the mMoment corresponding to the list position
                Moment moment = (Moment) mViewModelList.get(position);

                // use Picasso to fill the videoPreviewImageView from the mMoment's picture url
                // fill this before the rest so the loading doesn't look silly
                Picasso.with(mContext).load(moment.getPictureUrl()).into(momentCardHolder.videoPreviewImageView);

                // set the text in the videoNameTextView from the mMoment
                momentCardHolder.videoNameTextView.setText(moment.getName());

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

    public void clear() {

        mViewModelList.clear();

    }

}
