package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    TopicInteractionListener mActivityCallback;

    // list of all things to be bound to the RecyclerView cells
    ArrayList<Object> mViewModelList;

    // integer identifiers for the viewtypes
    final int PROMPT = 1, TOPIC = 2;

    // boolean indicating if the Holder should be clickable
    boolean mClickable = true;

    /**
     * CONSTRUCTORS
     */
    public TopicAdapter(Context context, ArrayList<Object> viewModels, boolean clickable) {

        mContext = context;

        mActivityCallback = (TopicInteractionListener) context;

        mViewModelList = viewModels;

        mClickable = clickable;

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mViewModelList.size();

    }

    public int getItemViewType(int position) {
        // return the type of object at the position in the mViewModelList

        if(mViewModelList.get(position) instanceof String) {

            return PROMPT;

        }

        else if(mViewModelList.get(position) instanceof TopicCardData) {

            return TOPIC;

        }

        else return -1;

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // return a ViewHolder of the correct type, depending on the viewType returned by getItemViewType

        // the generic Holder to be cast to one of the other two types
        RecyclerView.ViewHolder holder = null;

        switch(viewType) {

            case PROMPT:
                // it's a String, so it's the title at the top, so return that Holder

                // inflate a topic_prompt layout
                View topicPromptView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.topic_prompt, parent, false);

                holder = new TopicPromptHolder(mContext, topicPromptView);

                break;

            case TOPIC:
                // it's a topic so return a TopicCardHolder

                // inflate a topic_card layout
                View topicCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.topic_card, parent, false);

                holder = new TopicCardHolder(mContext, topicCardView, mClickable);

                break;

        }

        return holder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch(holder.getItemViewType()) {

            case PROMPT:
                // it's a prompt so fill a TopicPromptHolder with the String

                TopicPromptHolder topicPromptHolder = (TopicPromptHolder) holder;

                // cast the generic object at the position to a String
                String topicPrompt = (String) mViewModelList.get(position);

                // set the value in the holder with the String
                topicPromptHolder.setText(topicPrompt);

                break;

            case TOPIC:
                // it's a topic so fill the TopicCardHolder with the stuff from TopicCardData at position

                // cast the generic Holder to a TopicCardHolder
                TopicCardHolder topicCardHolder = (TopicCardHolder) holder;

                // cast the generic Object at position to the TopicCardData that it is
                TopicCardData topicCardData = (TopicCardData) mViewModelList.get(position);

                // set the two values in the Holder from the TopicCardData
                topicCardHolder.mTopicCardTitleTextView.setText(topicCardData.getTitle());
                topicCardHolder.mTopicCardDescriptionTextView.setText(topicCardData.getDescription());

                // apply a listener to inform the Activity of a click
                topicCardHolder.mTopicCardView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        mActivityCallback.onTopicClick(position);

                    }

                });

        }

    }

    interface TopicInteractionListener {
        // an interface to alert an Activity of a topic_card click

        void onTopicClick(int position);

    }

}
