package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

public class CardAdapter extends RecyclerView.Adapter<CommunityCardHolder> {
    // Adapter for populating the RecyclerView on phones

    Context mContext;

    // the View filling the RecyclerView cells
    int mResource;

    // the list of Videos
    VideoList mVideoList;


    public CardAdapter(Context context, int resource, VideoList videoList) {

        mContext = context;
        mResource = resource;
        mVideoList = videoList;

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mVideoList.getVideoList().size();

    }

    public void onBindViewHolder(CommunityCardHolder holder, int position) {
        // fill the views contained in the holder with their intended values

        // get the video corresponding to the list position
        Video video = mVideoList.getVideoList().get(position);

        // use Picasso to fill the videoPreviewImageView from the video's picture url
        // fill this before the rest so the loading doesn't look silly
        Picasso.with(mContext).load(video.getPictureUrl()).into(holder.videoPreviewImageView);

        // set the text in the videoNameTextView from the video
        holder.videoNameTextView.setText(video.getName());

        // if there is a description set it, otherwise delete the view
        // if the view is deleted the constraints for the shareTextView
        String description = video.getDescription();

        if(!description.equals("")) {

            holder.videoDescriptionTextView.setText(description);

        }
        else {

            holder.videoDescriptionTextView.setVisibility(View.GONE);

        }

    }

    public CommunityCardHolder onCreateViewHolder(ViewGroup parent, int viewType){
        /**
         * get the view from the ViewGroup and create and return a Holder with it
         * passes the Holder it creates to onBindViewHolder ^
         */

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.community_card, parent, false);

        return new CommunityCardHolder(itemView);

    }

}
