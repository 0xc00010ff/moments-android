package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class SearchResultActivity extends AppCompatActivity implements MomentInteractionListener{

    // ui references
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    AVLoadingIndicatorView mProgressBar;
    MenuItem mStarOfDavidMenuItem;

    // the String to be passed in, with which to search Vimeo
    String mSearchString;

    MomentList mMomentList;

    // list of objects to be displayed in the RecyclerView (mostly Moments)
    ArrayList<Object> mViewModelList = new ArrayList<>();

    // the Adapter for the moment_cards
    MomentCardAdapter mMomentCardAdapter;

    // listener for RecyclerView's scroll
    EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // get the search string that was passed in
        mSearchString = getIntent().getStringExtra(getString(R.string.search_extra)).trim();

        // get the ui elements
        mToolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_result_recyclerView);
        mProgressBar = (AVLoadingIndicatorView) findViewById(R.id.search_result_progressBar);

        //set the ActionBar to the toolbar we created
        mToolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mSearchString);

        mMomentList = new MomentList(this);

        // set up the RecyclerView
        setupRecyclerView();

        // fetch the Moments
        AsyncSearchForMoments asyncSearchForMoments = new AsyncSearchForMoments();
        asyncSearchForMoments.execute(mSearchString);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // get the Star of David MenuItem
        mStarOfDavidMenuItem = menu.findItem(R.id.star_of_david);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch(menuItem.getItemId()) {

            case R.id.star_of_david:
                // the star of david was clicked

                // show the feedback dialog
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(getString(R.string.feedback_dialog_title))
                        .content(getString(R.string.feedback_dialog_content))
                        .items(R.array.feedback_dialog_items)
                        .itemsColor(getResources().getColor(R.color.actionBlue))
                        .itemsCallback(new MaterialDialog.ListCallback() {

                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                switch(position) {

                                    case 0:
                                        // they chose to rate the app. show the next dialog

                                        MaterialDialog materialDialog = new MaterialDialog.Builder(getBaseContext())
                                                .title(getString(R.string.app_review_dialog_title))
                                                .content(getString(R.string.app_review_dialog_content))
                                                .positiveText(getString(R.string.app_review_dialog_positive_text))
                                                .positiveColor(getResources().getColor(R.color.actionBlue))
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {

                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        // the user chose to leave a review. go to the play store

                                                        // make the Uri for our app in the store
                                                        Uri marketUri = Uri.parse(getString(R.string.marketplace_address) + getPackageName());

                                                        // make  new Intent
                                                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);

                                                        // set its data with the Uri
                                                        marketIntent.setData(marketUri);

                                                        // start the Activity
                                                        startActivity(marketIntent);

                                                    }

                                                })
                                                .negativeText(getString(R.string.app_review_dialog_negative_text))
                                                .negativeColor(getResources().getColor(R.color.textLight))
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {

                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        // the user chose not to leave a review. do nothing. auto dismiss.


                                                    }

                                                })
                                                .show();


                                        break;

                                    case 1:
                                        // they chose to report a problem. send an email

                                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                        emailIntent.setType("text/html");
                                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.email_recipient)});
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_report_subject));
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_report_content));

                                        startActivity(emailIntent);

                                        break;

                                }

                            }

                        })
                        .positiveText(getString(R.string.feedback_dialog_positive))
                        .positiveColor(getResources().getColor(R.color.textLight))
                        .show();



                break;

        }

        return true;

    }

    private void setupRecyclerView() {
        // sets up the RecyclerView with the MomentCardAdapter and a LayoutManager

        mMomentCardAdapter = new MomentCardAdapter(this, mViewModelList);

        mRecyclerView.setAdapter(mMomentCardAdapter);

        // choose and set LayoutManager based on device
        if(DeviceManager.isDeviceATablet(this)) {

            // apply a GridLayoutManager to the RecyclerView, making it a grid of 3 columns
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

            // make a new EndlessScrollRecyclerViewListener
            mScrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

                @Override
                public void onLoadMore() {

                    AsyncSearchForMoments asyncSearch = new AsyncSearchForMoments();

                    asyncSearch.execute(mSearchString);

                }

            };

            // add the scroll listener to the RecyclerView
            mRecyclerView.addOnScrollListener(mScrollListener);

        }
        else {

            //apply a LinearLayoutManager to the RecyclerView, making it a vertical list
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            mRecyclerView.setLayoutManager(linearLayoutManager);

            // make a new EndlessScrollRecyclerViewListener
            mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {

                @Override
                public void onLoadMore() {

                    AsyncSearchForMoments asyncSearch = new AsyncSearchForMoments();

                    asyncSearch.execute(mSearchString);

                }

            };

            // add the scroll listener to the RecyclerView
            mRecyclerView.addOnScrollListener(mScrollListener);

        }

    }

    private class AsyncSearchForMoments extends AsyncTask<String, Void, ArrayList<Moment>> {
        /**
         * class for running background calls to Vimeo to search for videos based on a search string
         */


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            // show the progress bar
            mProgressBar.show();
            mProgressBar.setVisibility(View.VISIBLE);

        }

        protected ArrayList<Moment> doInBackground(String... searchStrings) {

            // variadic arguments are required when extending AsyncTask. We won't use them so just grab the first one.
            String searchString = searchStrings[0];

            // search for Moments with the search String
            mMomentList.getMomentsByName(searchString);

            // get the Moments from the MomentList
            ArrayList<Moment> moments = mMomentList.getMomentList();

            return moments;

        }

        protected void onPostExecute(ArrayList<Moment> moments) {

            // hide the progress bar
            mProgressBar.hide();
            mProgressBar.setVisibility(View.INVISIBLE);

            // clear the list
            mViewModelList.clear();

            // add all the Moments we were returned to the list
            mViewModelList.addAll(moments);

            // notify the Adapter to update the screen with its new data
            mMomentCardAdapter.notifyDataSetChanged();

        }


    }

    /**
     * MomentInteractionListener implementations for callbacks from Moments
     */

    @Override
    public void onCommunityDotsClick(final Moment moment) {
        // the dots for a community Moment were clicked

        final Context context = this;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.community_moments_dots_dialog_array_sans_search)
                .itemsColor(getResources().getColor(R.color.actionBlue))
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to report. compose an email.

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("text/html");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.email_recipient)});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_content) + " " + moment.getVideoUrl());

                                startActivity(emailIntent);

                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.textLight))
                .show();

    }

    @Override
    public void onCommunityShareClick(final Moment moment) {
        // the share button on a community Moment was clicked. share the Moment.

        // make an intent
        Intent sendIntent = new Intent();

        // express that the Intent is to send data
        sendIntent.setAction(Intent.ACTION_SEND);

        // attach some text to it
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sms_message) + " " + moment.getVideoUrl());

        // set the type as text
        sendIntent.setType("text/plain");

        // start the Activity
        startActivity(sendIntent);

    }

    @Override
    public void onVideoSelect(final Moment moment) {
        // the play button on any Moment was clicked

        // open a new Activity to view the Moment

        // a boolean that determines whether or not we're allowed to open the VideoViewActivity
        // if there is a Live Moment that hasn't finished processing on Vimeo, watchable will be false.
        boolean watchable = true;

        // if the Moment is Live
        if(moment.getMomentState() == MomentStateEnum.LIVE) {
            // it may not be available from Vimeo. If it isn't, we can't open the VideoViewActivity
            // or it will try to play a video that isn't processed and crashed

            // if it's not available
            if(!moment.isAvailable()) {

                // set the boolean to false
                watchable = false;

            }


        }

        // if the Moment is Private
        if(moment.getMomentState() == MomentStateEnum.PRIVATE) {

            // if the Moment doesn't have a video
            if(moment.getLocalVideoFilePath() == null) {

                // it isn't watchable
                watchable = false;

            }

        }

        if(watchable) {

            // create the Intent
            Intent videoIntent = new Intent(getBaseContext(), VideoViewActivity.class);

            // if there is a Vimeo video uri then attach that to the Intent
            if(moment.getVideoUri() != null) {

                videoIntent.putExtra(getString(R.string.vimeo_video_uri_extra), moment.getVideoUri());

            }

            // otherwise attach the local video uri to it
            else {

                videoIntent.putExtra(getString(R.string.local_video_file_extra), moment.getLocalVideoFilePath());

            }

            // open the activity
            startActivity(videoIntent);

        }

        else {
            // the Moment is Live and unavailable... tell the user that

            if(moment.getMomentState() == MomentStateEnum.LIVE) {

                // show the dialog explaining
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(getString(R.string.video_processing_dialog_title))
                        .content(R.string.video_processing_dialog_content)
                        .positiveText(R.string.video_processing_dialog_prompt)
                        .positiveColor(getResources().getColor(R.color.actionBlue))
                        .show();


            }

        }


    }

    @Override
    public void onFailedStateClick(Moment moment) {
        // a failed local Moment was clicked

    }

    @Override
    public void onMomentPromptClick() {
        // a moment_prompt was clicked


    }

    @Override
    public void onMyDotsClick(Moment moment) {
        // the dots in a local Moment were clicked

    }

    @Override
    public void onMyMomentCardClick(Moment moment) {
        // a local Moment was clicked

    }

    @Override
    public void onMyShareClick(Moment moment) {
        // the share button of a local Moment was clicked

    }

}
