package com.tikkunolam.momentsintime;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import io.realm.RealmResults;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;


public class MyMomentsFragment extends Fragment implements MainActivity.myMomentInterface{

    // tag for logging purposes
    private final String TAG = "My Moments Fragment";

    // Strings for use as extra argument identifiers
    String mPrimaryKeyExtra;

    // integer identifiers for Intent requestCodes
    final int MAKE_A_MOMENT_REQUEST_CODE = 1;

    // Strings for use with SharedPreferences
    String mHasFailedFlagName, mDisplayUploadMessageFlagName, mPrimaryKeyFlagName;

    // list of Moments
    private MomentList mMomentList;

    // list of Moments and Prompts to fill the RecyclerView
    ArrayList<Object> mViewModelList;

    // MomentCardAdapter for the RecyclerView
    MomentCardAdapter mMomentCardAdapter;

    // ui references
    RelativeLayout mMyMomentsRelativeLayout;
    LinearLayout mNoMomentsLinearLayout;
    RecyclerView mMyMomentsRecyclerView;
    TextView mMakeAMomentTextView;
    FloatingActionButton mFloatingActionButton;
    ProgressBar mProgressBar;
    PulsatorLayout mFabPulse;

    // position at which to insert an UploadMessage
    final int mUploadMessagePosition = 0;


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

        // get the name of the sharedPreferences
        mHasFailedFlagName = getString(R.string.has_failed_flag);
        mDisplayUploadMessageFlagName = getString(R.string.display_upload_message);
        mPrimaryKeyFlagName = getString(R.string.moment_primary_key);

        // get the RelativeLayout to retrieve the child views
        mMyMomentsRelativeLayout = (RelativeLayout) entireView;

        // get the progressBar
        mProgressBar = (ProgressBar) mMyMomentsRelativeLayout.findViewById(R.id.progressBar);

        // get the PulsatorLayout for the FAB pulse
        mFabPulse = (PulsatorLayout) mMyMomentsRelativeLayout.findViewById(R.id.fab_pulse);

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

        // fill the mViewModelList with local Moments
        fetchMoments();

        // switch any UPLOADING Moments that may have failed to FAILED if indicated by sharedPreference
        switchToFailed();

        // inflate the layout for this fragment and return it
        return entireView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // fetch the mPrimaryKeyExtra
        mPrimaryKeyExtra = getString(R.string.primary_key_extra);

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

    @Override
    public void onResume() {

        // call the superclass's constructor
        super.onResume();

        // refresh the mViewModelList with Moments
        fetchMoments();

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

    }

    private void fetchMoments() {
        // fetches all the Moments and updates the screen with them

        mMomentList.getMyMoments();

        // clear the contents of the screen
        mViewModelList.clear();

        // check to see if an UploadMessage should be added
        addUploadMessage();

        // add all the fetched Moments to the mViewModelList
        mViewModelList.addAll(mMomentList.getMomentList());

        // tell the Adapter to update itself
        mMomentCardAdapter.notifyDataSetChanged();

        // if there are no Moments, show the noMoments prompt
        if(mViewModelList.size() == 0) {

            mNoMomentsLinearLayout.setVisibility(View.VISIBLE);

        }

        // otherwise hide it
        else {

            mNoMomentsLinearLayout.setVisibility(View.GONE);

        }


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

    // check if the flag was set by the service indicating an upload never finished. if so switch any UPLOADING Moments to FAILED
    public void switchToFailed() {

        // get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // get the shouldSwitch value from them
        Boolean shouldSwitch = sharedPreferences.getBoolean(mHasFailedFlagName, false);

        // reset it to false in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mHasFailedFlagName, false);
        editor.commit();

        // if a the UploadService started an upload and never finished it
        if(shouldSwitch) {
            // find all the Moments with UPLOADING state and switch them to FAILED

            mMomentList.getMyMoments();

            ArrayList<Moment> list = mMomentList.getMomentList();

            for(Moment moment: list) {
                // for every Moment in the MomentList, find the Managed Moment by key, and if it's uploading, change it to failed

                // get the primaryKey
                String primaryKey = moment.getPrimaryKey();

                // get the managed Moment
                final Moment managedMoment = Moment.findMoment(primaryKey);

                if(managedMoment.getMomentState() == MomentStateEnum.UPLOADING) {

                    managedMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            managedMoment.setEnumState(MomentStateEnum.FAILED);

                        }

                    });

                }

            }

        }


    }

    private void addUploadMessage() {
        // determine if an UploadMessage should be added, and add it if so

        // get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // get the shouldAddMessage value from them
        Boolean shouldAddMessage = sharedPreferences.getBoolean(mDisplayUploadMessageFlagName, false);

        // reset it to false in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mDisplayUploadMessageFlagName, false);
        editor.commit();

        // if we should add an UploadMessage
        if(shouldAddMessage) {
            // add one to the view list

            // get the PrimaryKey of the Moment that inspired the UploadMessage
            String primaryKey = sharedPreferences.getString(mPrimaryKeyFlagName, "");

            // make an UploadMessage
            UploadMessage uploadMessage = new UploadMessage(getContext(), primaryKey);

            // insert it at the beginning of the view list
            mViewModelList.add(mUploadMessagePosition, uploadMessage);

        }

    }

    /**
     * EVENTBUS CALLBACKS
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadFinishedMessage event) {
        // an UploadService finished.. reload the views

        fetchMoments();

    }

    /**
     * METHODS TO BE CALLED FROM THE PARENT ACTIVITY
     */

    public void refreshListFromActivity() {
        // called from the MainActivity when it has changed some underlying data and the Fragment needs to refresh

        fetchMoments();

    }

    public void openMakeAMomentActivity() {
        // called from the MainActivity when it wants to open MakeAMomentActivity from the Moment prompt

        // make the FAB pulse
        mFabPulse.start();

        // wait .75 seconds to open the activity to make it clear to the user what happened
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {

                // stop the pulse
                mFabPulse.stop();

                // open MakeAMomentActivity
                onNewMomentClick();

            }

        }, 750);

    }

}
