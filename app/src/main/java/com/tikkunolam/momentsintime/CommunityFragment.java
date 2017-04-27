package com.tikkunolam.momentsintime;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


public class CommunityFragment extends Fragment {

    // ui references
    FrameLayout mCommunityFrameLayout;
    RecyclerView mCommunityRecyclerView;
    ProgressBar mProgressBar;

    // adapter for the RecyclerView
    MomentCardAdapter mMomentCardAdapter;

    // fragment identifier for the adapter
    int mIdentifier = 1;

    // list of Moments
    MomentList mMomentList;

    // callback for the activity to handle fragment business
    FragmentInteractionListener mActivityCallback;

    // listener for mCommunityRecyclerView's scroll
    EndlessRecyclerViewScrollListener mScrollListener;

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
        mCommunityFrameLayout = (FrameLayout) entireView;

        // the the ProgressBar
        mProgressBar = (ProgressBar) mCommunityFrameLayout.findViewById(R.id.progressBar);

        // get the RecyclerView
        mCommunityRecyclerView = (RecyclerView) mCommunityFrameLayout.findViewById(R.id.community_recyclerView);

        // set the MomentList
        mMomentList = new MomentList(getActivity().getApplicationContext());

        // set up the RecyclerView
        setUpRecyclerView();

        // return the inflated view
        return entireView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // things to run after the activity's onCreate method is called

        super.onActivityCreated(savedInstanceState);

        // create and execute a new asynchronous task to fill the mMomentList
        AsyncFetchCommunityMoments asyncFetchCommunityVideos = new AsyncFetchCommunityMoments();
        asyncFetchCommunityVideos.execute(mMomentList);

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // get the reference to the activity for the callback
        mActivityCallback = (FragmentInteractionListener) context;

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
        mMomentCardAdapter = new MomentCardAdapter(getContext(), R.id.community_cardView, mMomentList, mIdentifier);

        // set the adapter on the RecyclerView
        mCommunityRecyclerView.setAdapter(mMomentCardAdapter);

        // choose and set LayoutManager based on device
        if(DeviceManager.isDeviceATablet(getActivity())) {

            // apply a GridLayoutManager to the RecyclerView, making it a grid of 3 columns
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mCommunityRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        }
        else {

            //apply a LinearLayoutManager to the RecyclerView, making it a vertical list
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mCommunityRecyclerView.setLayoutManager(linearLayoutManager);

            // make a new EndlessScrollRecyclerViewListener
            mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {

                @Override
                public void onLoadMore() {

                    AsyncFetchCommunityMoments asyncFetchCommunityMoments = new AsyncFetchCommunityMoments();
                    asyncFetchCommunityMoments.execute(mMomentList);

                }

            };

            // add the scroll listener to the RecyclerView
            mCommunityRecyclerView.addOnScrollListener(mScrollListener);



        }

        // add the RecyclerItemClickListener to the RecyclerView items.
        mCommunityRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mCommunityRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            // what's to be done when a cell is clicked
                            @Override
                            public void onItemClick(View view, int position) {
                                // tell the Activity what Moment was selected

                                // to account for having added the moment_prompt subtract 1 from position
                                // if the position is > 7 (the index of the moment_prompt)
                                if(position > 7) position--;

                                // tell the Activity
                                mActivityCallback.onMomentSelect(mMomentList.getMomentList().get(position));
                            }
                        }
                )
        );

    }


    public class AsyncFetchCommunityMoments extends AsyncTask<MomentList, Void, Void> {
        /**
         * class for making asynchronous update to the mMomentList
         * just updates the underlying list of Videos by fetching the Community Videos
         * then the adapter is notified of the change
         */

        protected void onPreExecute() {

            // show the progress bar
            mProgressBar.setVisibility(View.VISIBLE);

        }

        protected Void doInBackground(MomentList... momentList) {
            // run this task in the background


            // fetch the community videos to update the MomentList
            momentList[0].getCommunityMoments();

            //passes nothing to onPostExecute, but an argument is needed
            return null;

        }

        protected void onPostExecute(Void ignoreThis) {
            // execute on the UI thread when the background task completes
            // Void argument is necessary, but not used


            // stop the progressBar
            mProgressBar.setVisibility(View.GONE);

            // notify the adapter the MomentList has changed
            mMomentCardAdapter.clear();
            mMomentCardAdapter.updateDataSet();
            mMomentCardAdapter.notifyDataSetChanged();

        }

    }

}

