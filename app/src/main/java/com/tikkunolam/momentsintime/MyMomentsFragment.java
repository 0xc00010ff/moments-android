package com.tikkunolam.momentsintime;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class MyMomentsFragment extends Fragment{

    // tag for logging purposes
    private final String TAG = "My Moments Fragment";

    // Strings for use as extra argument identifiers
    String mPrimaryKeyExtra;

    // integer identifiers for Intent requestCodes
    final int MAKE_A_MOMENT_REQUEST_CODE = 1;

    // list of Moments
    private MomentList mMomentList;

    // list of Moments and Prompts to fill the RecyclerView
    ArrayList<Object> mViewModelList;

    // callback for the activity to handle fragment business
    FragmentInteractionListener mActivityCallback;

    // MomentCardAdapter for the RecyclerView
    MomentCardAdapter mMomentCardAdapter;

    // ui references
    RelativeLayout mMyMomentsRelativeLayout;
    LinearLayout mNoMomentsLinearLayout;
    RecyclerView mMyMomentsRecyclerView;
    TextView mMakeAMomentTextView;
    FloatingActionButton mFloatingActionButton;
    ProgressBar mProgressBar;


    public MyMomentsFragment() {

        // required empty public constructor

    }

    public static MyMomentsFragment newInstance() {

        MyMomentsFragment fragment = new MyMomentsFragment();

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the fragment's view
        View entireView = inflater.inflate(R.layout.fragment_my_moments, container, false);

        // get the RelativeLayout to retrieve the child views
        mMyMomentsRelativeLayout = (RelativeLayout) entireView;

        // get the progressBar
        mProgressBar = (ProgressBar) mMyMomentsRelativeLayout.findViewById(R.id.progressBar);

        // get the view to show when there are no moments. it's set to hidden for now
        mNoMomentsLinearLayout = (LinearLayout) mMyMomentsRelativeLayout.findViewById(R.id.no_moments_linearLayout);

        // get the Make a Moment prompt and set an OnClickListener on it
        mMakeAMomentTextView = (TextView) mNoMomentsLinearLayout.findViewById(R.id.make_a_moment_textView);
        mMakeAMomentTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // load a MakeAMomentActivity for result
                onNewMomentClick();

            }

        });

        // get the FloatingActionButton and set an OnClickListener on it
        mFloatingActionButton = (FloatingActionButton) mMyMomentsRelativeLayout.findViewById(R.id.floatingActionButton);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // when the fab is clicked load a MakeAMomentActivity for result

                onNewMomentClick();

            }

        });

        // get the RecyclerView
        mMyMomentsRecyclerView = (RecyclerView) mMyMomentsRelativeLayout.findViewById(R.id.my_moments_recyclerView);

        // create a new MomentList
        mMomentList = new MomentList(getActivity().getApplicationContext());

        // create a new mViewModelList
        mViewModelList = new ArrayList<>();

        // set up the RecyclerView
        setUpRecyclerView();

        // inflate the layout for this fragment and return it
        return entireView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // fetch the mPrimaryKeyExtra
        mPrimaryKeyExtra = getString(R.string.primary_key_extra);

        // create a new asynchronous task to fill the mMomentList
        AsyncFetchMyMoments asyncFetchMyMoments = new AsyncFetchMyMoments();
        asyncFetchMyMoments.execute(mMomentList);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // get the reference to the activity for the callback
        mActivityCallback = (FragmentInteractionListener) context;

    }

    @Override
    public void onStart() {

        super.onStart();

        // register this fragment for EventBus message delivery
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {

        super.onStop();

        // unregister this fragment for EventBus message delivery
        EventBus.getDefault().unregister(this);

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
        mMyMomentsRecyclerView.setAdapter(mMomentCardAdapter);

        // choose and set LayoutManager based on device
        if(DeviceManager.isDeviceATablet(getActivity())) {

            // apply a GridLayoutManager to the RecyclerView, making it a grid of 3 columns
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mMyMomentsRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        }
        else {

            //apply a LinearLayoutManager to the RecyclerView, making it a vertical list
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mMyMomentsRecyclerView.setLayoutManager(linearLayoutManager);

        }

        // add the RecyclerItemClickListener to the RecyclerView items.
        mMyMomentsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mMyMomentsRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            // what's to be done when a cell is clicked
                            @Override
                            public void onItemClick(View view, int position) {
                                // tell the Activity what Moment was selected
                                mActivityCallback.onMomentSelect(mMomentList.getMomentList().get(position));
                            }
                        }
                )
        );

    }

    private void onNewMomentClick() {

        // make a new Intent with the MakeAMomentActivity
        Intent makeAMomentIntent = new Intent(getActivity(), MakeAMomentActivity.class);

        // start it
        this.startActivityForResult(makeAMomentIntent, MAKE_A_MOMENT_REQUEST_CODE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called when returning from a StartActivityForResult

        if(resultCode == RESULT_OK) {

            if(requestCode == MAKE_A_MOMENT_REQUEST_CODE) {
                // the user made a Moment. it was saved to Realm fetch it and display it

                // get the primaryKey for the Moment created by the MakeAMomentActivity
                String primaryKey = data.getStringExtra(mPrimaryKeyExtra);

                // find the Moment in Realm and add it to the MomentList
                Moment.findMoment(primaryKey);
            }

        }


    }

    /**
     * EVENTBUS CALLBACKS
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadFinishedMessage event) {
        // an UploadService finished.. do whatever to handle that
        // update the Moment by primaryKey and reload the RecyclerView

    }

    public class AsyncFetchMyMoments extends AsyncTask<MomentList, Void, Void> {
        /**
         * class for making asynchronous update to the mMomentList
         * just updates the underlying list of Videos by fetching the Community Videos
         * then the adapter is notified of the change
         */

        protected void onPreExecute() {

            // show the mProgressBar while fetching Moments
            mProgressBar.setVisibility(View.VISIBLE);

        }

        protected Void doInBackground(MomentList... momentList) {
            // run this task in the background


            // fetch the community videos to update the MomentList
            momentList[0].getMyMoments();

            //passes nothing to onPostExecute, but an argument is needed
            return null;

        }

        protected void onPostExecute(Void ignoreThis) {
            // execute on the UI thread when the background task completes
            // Void argument is necessary, but not used


            // stop the mProgressBar
            mProgressBar.setVisibility(View.GONE);

            mViewModelList.clear();

            mViewModelList.addAll(mMomentList.getMomentList());

            // notify the adapter the MomentList has changed
            mMomentCardAdapter.notifyDataSetChanged();

            // if there are still no Moments, display the no_moments layout
            if(mViewModelList.size() < 1) {

                // make it visible
                mNoMomentsLinearLayout.setVisibility(View.VISIBLE);

            }

        }

    }

}
