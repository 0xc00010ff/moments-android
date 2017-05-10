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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class CommunityFragment extends Fragment {

    Context mContext;

    // ui references
    FrameLayout mCommunityFrameLayout;
    RecyclerView mCommunityRecyclerView;
    ProgressBar mProgressBar;

    // adapter for the RecyclerView
    MomentCardAdapter mMomentCardAdapter;

    // list of Moments
    MomentList mMomentList;

    // list of Moments and MomentPrompts for the RecyclerView
    ArrayList<Object> mViewModelList;

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

        mContext = getActivity();

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

        // make a new mViewModelList
        mViewModelList = new ArrayList<>();

        // set up the RecyclerView
        setUpRecyclerView();

        // return the inflated view
        return entireView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // things to run after the activity's onCreate method is called

        super.onActivityCreated(savedInstanceState);

        // create and execute a new asynchronous task to fill the mViewModelList
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
        mMomentCardAdapter = new MomentCardAdapter(getContext(), mViewModelList);

        // set the adapter on the RecyclerView
        mCommunityRecyclerView.setAdapter(mMomentCardAdapter);

        // choose and set LayoutManager based on device
        if(DeviceManager.isDeviceATablet(getActivity())) {

            // apply a GridLayoutManager to the RecyclerView, making it a grid of 3 columns
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mCommunityRecyclerView.setLayoutManager(staggeredGridLayoutManager);

            // make a new EndlessScrollRecyclerViewListener
            mScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

                @Override
                public void onLoadMore() {

                    AsyncFetchCommunityMoments asyncFetchCommunityMoments = new AsyncFetchCommunityMoments();
                    asyncFetchCommunityMoments.execute(mMomentList);

                }

            };

            // add the scroll listener to the RecyclerView
            mCommunityRecyclerView.addOnScrollListener(mScrollListener);

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

            // clear the list for the sake of the endless scroll
            mViewModelList.clear();

            // add all the fetched Moments to the mViewModelList
            mViewModelList.addAll(mMomentList.getMomentList());

            // insert a single prompt at the seventh position
            insertMomentPrompts(7, false);

            // notify the adapter the mViewModelList has changed
            mMomentCardAdapter.notifyDataSetChanged();

        }

    }

    private void insertMomentPrompts(int position, boolean repeating) {
        // either inserts one MomentPrompt at position, or a MomentPrompt at every [position]th position in mViewModeList

        if(mViewModelList.size() >= position) {
            // if the list is large enough to insert a prompt at that position

            if(repeating) {
                // add a MomentPrompt at every [position]th position

                for(int i = 0; i > mViewModelList.size(); i++) {

                    if ((position - 1) % i == 0) {

                        MomentPrompt momentPrompt = new MomentPrompt(mContext);
                        mViewModelList.add(i, momentPrompt);

                    }

                }

            }

            else {
                // add a single MomentPrompt at position

                MomentPrompt momentPrompt = new MomentPrompt(mContext);
                mViewModelList.add(position, momentPrompt);

            }

        }

        else {
            // otherwise just put it at the end

            MomentPrompt momentPrompt = new MomentPrompt(mContext);
            mViewModelList.add(mViewModelList.size(), momentPrompt);

        }

    }

}

