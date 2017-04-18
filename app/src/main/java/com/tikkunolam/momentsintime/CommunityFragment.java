package com.tikkunolam.momentsintime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.R.attr.resource;
import static android.content.ContentValues.TAG;
import static android.media.CamcorderProfile.get;


public class CommunityFragment extends Fragment {

    // ui references
    FrameLayout communityFrameLayout;
    RecyclerView communityRecyclerView;

    // adapter for the RecyclerView
    CardAdapter mRecyclerAdapter;

    // VideoList model
    VideoList mVideoList;

    // callback for the activity to handle business when a video is clicked
    OnCommunityInteractionListener activityCallback;

    public CommunityFragment() {

        // required empty public constructor

    }


    public static CommunityFragment newInstance() {

        CommunityFragment fragment = new CommunityFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the layout for the fragment
        View entireView = inflater.inflate(R.layout.fragment_community, container, false);

        // from the View retrieve the FrameLayout to get the rest of the views
        communityFrameLayout = (FrameLayout) entireView;

        // get the RecyclerView
        communityRecyclerView = (RecyclerView) communityFrameLayout.findViewById(R.id.community_recyclerView);

        // set up the RecyclerView
        setUpRecyclerView();

        // return the inflated view
        return entireView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // things to run after the activity's onCreate method is called

        super.onActivityCreated(savedInstanceState);

        // create and execute a new asynchronous task to fill the mVideoList
        AsyncFetchCommunityVideos asyncFetchCommunityVideos = new AsyncFetchCommunityVideos();
        asyncFetchCommunityVideos.execute(mVideoList);

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // get the reference to the activity for the callback
        activityCallback = (OnCommunityInteractionListener) context;

        // set the VideoList
        mVideoList = new VideoList(getActivity().getApplicationContext());

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setUpRecyclerView(){
        /**
         * SET UP THE RECYCLERVIEW
         * set the adapter on it
         * determine if the device is a tablet and choose between two LayoutManagers
         * add whatever decorations to the RecyclerView
         * set whatever item listeners
         */

        // get the RecyclerAdapter
        mRecyclerAdapter = new CardAdapter(getContext(), R.id.community_cardView, mVideoList);

        // set the adapter on the RecyclerView
        communityRecyclerView.setAdapter(mRecyclerAdapter);

        // choose and set LayoutManager based on device
        if(isDeviceATablet()) {

            // apply a GridLayoutManager to the RecyclerView, making it a grid of 3 columns
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            communityRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        }
        else {

            //apply a LinearLayoutManager to the RecyclerView, making it a vertical list
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            communityRecyclerView.setLayoutManager(linearLayoutManager);

        }

        // add the RecyclerItemClickListener to the RecyclerView items.
        communityRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), communityRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            // what's to be done when a cell is clicked
                            @Override
                            public void onItemClick(View view, int position) {
                                // tell the Activity what Video was selected
                                activityCallback.onVideoSelect(mVideoList.getVideoList().get(position));
                            }
                        }
                )
        );

    }

    public boolean isDeviceATablet(){
        /**
         * DETERMINE IF THE DEVICE IS A TABLET
         * fetch the screen dimensions
         * convert them to dp units
         * if the smallest dimension is >= 600dp it's a tablet
         */

        // fetch the device display object and produce the metrics
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // fetch the pixel density to convert pixels to dp
        float density = getResources().getDisplayMetrics().density;

        // get the dp values
        float dpWidth = outMetrics.widthPixels / density;
        float dpHeight = outMetrics.heightPixels / density;

        // return whether it's a tablet
        return (dpWidth >= 800 || dpHeight >= 800);

    }


    public class AsyncFetchCommunityVideos extends AsyncTask<VideoList, Void, Void> {
        /**
         * class for making asynchronous update to the mVideoList
         * just updates the underlying list of Videos by fetching the Community Videos
         * then the adapter is notified of the change
         */

        protected Void doInBackground(VideoList... videoList) {
            // run this task in the background


            // fetch the community videos to update the VideoList
            videoList[0].getCommunityVideos();

            //passes nothing to onPostExecute, but an argument is needed
            return null;

        }

        protected void onPostExecute(Void ignoreThis) {
            // execute on the UI thread when the background task completes
            // Void argument is necessary, but not used


            // notify the adapter the VideoList has changed
            mRecyclerAdapter.notifyDataSetChanged();

        }

    }

    public interface OnCommunityInteractionListener {

        void onVideoSelect(Video video);

    }

}

