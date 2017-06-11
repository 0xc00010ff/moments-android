package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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

    // string for shared preference argument
    String mIsFirstTimeArg;

    boolean mIsFirstTime;

    // ui references
    FrameLayout mCommunityFrameLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mCommunityRecyclerView;
    ProgressBar mProgressBar;

    // adapter for the RecyclerView
    MomentCardAdapter mMomentCardAdapter;

    // list of Moments
    MomentList mMomentList;

    // list of Moments and MomentPrompts for the RecyclerView
    ArrayList<Object> mViewModelList;

    // listener for mCommunityRecyclerView's scroll
    EndlessRecyclerViewScrollListener mScrollListener;

    final int WELCOME_MESSAGE_POSITION = 0;

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

        mIsFirstTimeArg = mContext.getString(R.string.is_first_visit);

        // inflate the layout for the fragment
        View entireView = inflater.inflate(R.layout.fragment_community, container, false);

        // from the View retrieve the FrameLayout to get the rest of the views
        mCommunityFrameLayout = (FrameLayout) entireView;

        // the the ProgressBar
        mProgressBar = (ProgressBar) mCommunityFrameLayout.findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mCommunityFrameLayout.findViewById(R.id.community_swipeRefreshLayout);

        // get the RecyclerView
        mCommunityRecyclerView = (RecyclerView) mCommunityFrameLayout.findViewById(R.id.community_recyclerView);

        // set the MomentList
        mMomentList = new MomentList(getActivity().getApplicationContext());

        // make a new mViewModelList
        mViewModelList = new ArrayList<>();

        // check if this is the user's first time in the app
        checkIfFirstTime();

        // set up the RecyclerView
        setUpRecyclerView();

        // set up swipe to refresh
        setUpSwipeToRefresh();

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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void insertWelcomeMessage() {

        if(mIsFirstTime) {

            WelcomeMessage welcomeMessage = new WelcomeMessage(mContext);
            mViewModelList.add(WELCOME_MESSAGE_POSITION, welcomeMessage);

        }

    }

    public void checkIfFirstTime() {

        // get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // get the isFirstVisit value from them
        mIsFirstTime = sharedPreferences.getBoolean(mIsFirstTimeArg, true);

        // set the value to false so it'll never insert one again
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mIsFirstTimeArg, false);
        editor.commit();

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

    private void setUpSwipeToRefresh() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mScrollListener.resetState();

                mMomentList = new MomentList(getContext());

                AsyncFetchCommunityMoments asyncFetch = new AsyncFetchCommunityMoments();

                asyncFetch.execute(mMomentList);

            }

        });


    }


    public class AsyncFetchCommunityMoments extends AsyncTask<MomentList, Void, Void> {
        /**
         * class for making asynchronous update to the mMomentList
         * just updates the underlying list of Videos by fetching the Community Videos
         * then the adapter is notified of the change
         */

        protected void onPreExecute() {

            // if this wasn't called by a swipe to refresh, show the progress bar
            if(!mSwipeRefreshLayout.isRefreshing()) {

                // show the progress bar
                mProgressBar.setVisibility(View.VISIBLE);

            }

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


            if(mSwipeRefreshLayout.isRefreshing()) {

                mSwipeRefreshLayout.setRefreshing(false);

            }

            // stop the progressBar
            mProgressBar.setVisibility(View.GONE);

            // clear the list for the sake of the endless scroll
            mViewModelList.clear();

            // add the welcome message if appropriate
            insertWelcomeMessage();

            // add all the fetched Moments to the mViewModelList
            mViewModelList.addAll(mMomentList.getMomentList());

            // insert a repeating prompt in every fifth position
            insertMomentPrompts(5, true);

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

                Log.d("FUCK", "wtf");

                for(int i = 1; i < mViewModelList.size(); i++) {

                    Log.d("FUCK", "wtf");

                    if ( i % (position) == 0) {

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

