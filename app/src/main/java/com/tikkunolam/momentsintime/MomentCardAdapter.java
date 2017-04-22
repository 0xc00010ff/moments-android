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

    // the View filling the RecyclerView cells
    int mResource;

    // integer used to identify the calling fragment
    int mFragmentIdentifier;

    // the original list of Moments
    MomentList mMomentList;

    // the list that may have prompts inserted
    ArrayList<Object> mDynamicList = new ArrayList<>();

    // integers to identify the Object in the ArrayList
    final int MOMENT = 1;
    final int PROMPT = 2;


    public MomentCardAdapter(Context context, int resource, MomentList momentList, int fragmentIdentifier) {

        mContext = context;
        mResource = resource;
        mMomentList = momentList;
        mFragmentIdentifier = fragmentIdentifier;

        // fill the dynamic list with all the Moments
        mDynamicList.addAll(momentList.getMomentList());

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mDynamicList.size();

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // fill the views contained in the holder with their intended values

        switch(holder.getItemViewType()) {

            case MOMENT:
                // fill a momentCardHolder

                MomentCardHolder momentCardHolder = (MomentCardHolder) holder;

                // get the mMoment corresponding to the list position
                Moment moment = (Moment) mDynamicList.get(position);

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

                break;

            case PROMPT:
                // set a MomentPromptHolder

                MomentPromptHolder momentPromptHolder = (MomentPromptHolder) holder;

                // the text is set in the xml so no need to fill in values


        }

    }

    @Override
    public int getItemViewType(int position) {
        // determine what type of object is in the current position of the list

        if(mDynamicList.get(position) instanceof Moment) {

            return MOMENT;

        }

        else if(mDynamicList.get(position) instanceof String) {

            return PROMPT;

        }

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
                viewHolder = new MomentPromptHolder(promptItemView);

                break;

            case MOMENT:
                // if it calls for a Moment, inflate a moment_card and pass it to a new MomentCardHolder

                // inflate a moment_card
                View momentItemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.moment_card, parent, false);

                // set the viewHolder as a MomentCardHolder
                viewHolder = new MomentCardHolder(momentItemView);

                break;

        }

        return viewHolder;

    }

    public void updateDataSet() {
        /**
         * since notifyDataSetChanged is final, this method is to notify the adapter
         * that the data has been updated and it should fill the mDynamicList with
         * the new data
         */

        // fill the mDynamicList with the new Moments
        mDynamicList.addAll(mMomentList.getMomentList());

        // if the calling fragment is CommunityFragment add the Moment Prompts
        if(mFragmentIdentifier == 1) {

            addMomentPrompts();

        }

    }

    public void addMomentPrompts() {
        // adds Strings to the dynamic list to notify the adapter to fill the RecyclerView with moment_prompts

        // add a String at the seventh position, if possible
        if(mDynamicList.size() > 7) {

            mDynamicList.add(7, "PROMPT");

        }

    }

}
