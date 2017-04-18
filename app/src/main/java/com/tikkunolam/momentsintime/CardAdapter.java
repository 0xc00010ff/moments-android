package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

public class CardAdapter extends RecyclerView.Adapter<MomentCardHolder> {
    // Adapter for populating the RecyclerView on phones

    Context mContext;

    // the View filling the RecyclerView cells
    int mResource;

    // the list of Videos
    MomentList mMomentList;


    public CardAdapter(Context context, int resource, MomentList momentList) {

        mContext = context;
        mResource = resource;
        mMomentList = momentList;

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mMomentList.getMomentList().size();

    }

    public void onBindViewHolder(MomentCardHolder holder, int position) {
        // fill the views contained in the holder with their intended values

        // get the moment corresponding to the list position
        Moment moment = mMomentList.getMomentList().get(position);

        // use Picasso to fill the videoPreviewImageView from the moment's picture url
        // fill this before the rest so the loading doesn't look silly
        Picasso.with(mContext).load(moment.getPictureUrl()).into(holder.videoPreviewImageView);

        // set the text in the videoNameTextView from the moment
        holder.videoNameTextView.setText(moment.getName());

        // if there is a description set it, otherwise delete the view
        // if the view is deleted the constraints for the shareTextView
        String description = moment.getDescription();

        if(!description.equals("")) {

            holder.videoDescriptionTextView.setText(description);

        }
        else {

            holder.videoDescriptionTextView.setVisibility(View.GONE);

        }

    }

    public MomentCardHolder onCreateViewHolder(ViewGroup parent, int viewType){
        /**
         * get the view from the ViewGroup and create and return a Holder with it
         * passes the Holder it creates to onBindViewHolder ^
         */

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.moment_card, parent, false);

        return new MomentCardHolder(itemView);

    }

}
