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
    RecyclerAdapter mRecyclerAdapter;
    // ArrayList of Video objects
    ArrayList<Video> mVideoList = new ArrayList<>();

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
        asyncFetchCommunityVideos.execute();

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // get the reference to the activity for the callback
        activityCallback = (OnCommunityInteractionListener) context;

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
        mRecyclerAdapter = new RecyclerAdapter(getContext(), R.id.community_cardView, mVideoList);

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
                                activityCallback.onVideoSelect(mVideoList.get(position));
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


    public class RecyclerAdapter extends RecyclerView.Adapter<CommunityCardHolder> {
        // Adapter for populating the RecyclerView on phones

        Context mContext;

        // the View filling the RecyclerView cells
        int mResource;

        // the list of Videos
        ArrayList<Video> mVideoList;


        public RecyclerAdapter(Context context, int resource, ArrayList<Video> videos) {

            mContext = context;
            mResource = resource;
            mVideoList = videos;

        }

        public int getItemCount() {
            // return the number of items to fill the RecyclerView

            return mVideoList.size();

        }

        public void onBindViewHolder(CommunityCardHolder holder, int position) {
            // fill the views contained in the holder with their intended values

            // get the video corresponding to the list position
            Video video = mVideoList.get(position);

            // set the text in the videoNameTextView from the video
            holder.videoNameTextView.setText(video.getName());

            // if there is a description set it, otherwise delete the view
            //if the view is deleted the constraints for the shareTextView
            String description = video.getDescription();

            if(!description.equals("")) {

                holder.videoDescriptionTextView.setText(description);

            }
            else {

                holder.videoDescriptionTextView.setVisibility(View.GONE);

            }

            // use Picasso to fill the videoPreviewImageView from the video's picture url
            Picasso.with(mContext).load(video.getPictureUrl()).into(holder.videoPreviewImageView);

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

    public class AsyncFetchCommunityVideos extends AsyncTask<Void, Void, ArrayList<Video>> {
        /**
         * class for making asynchronous network calls
         * makes calls through the VimeoNetworkingSingleton
         * fills the mVideoList
         */

        protected ArrayList<Video> doInBackground(Void... ignoreThis) {
            // run this task in the background
            // the Void argument is required by the class but has no use in this context

            // get networking instance
            VimeoNetworkingSingleton networking = VimeoNetworkingSingleton.getInstance();

            // fetch the community videos
            ArrayList<Video> fetchedVideos = networking.getCommunityVideos();

            return fetchedVideos;

        }

        protected void onPostExecute(ArrayList<Video> fetchedVideos) {
            // execute on the UI thread when the background task completes

            if(fetchedVideos != null) {
                // update the RecyclerView's contents

                // clear the Video list
                mVideoList.clear();

                // fill the Video list with the new data
                mVideoList.addAll(fetchedVideos);

                // notify the adapter to update the RecyclerView's contents
                mRecyclerAdapter.notifyDataSetChanged();

            }

            else {
                // the networker couldn't fetch the videos
                // maybe do something about it

                Log.d(TAG, "didn't fetch the videos");
            }

        }

    }

    public interface OnCommunityInteractionListener {

        void onVideoSelect(Video video);

    }

}

