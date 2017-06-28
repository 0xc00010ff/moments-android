package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.Theme;

import java.io.File;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements MomentInteractionListener {

    // tag for logging purposes
    private final String TAG = "MainActivity";

    Context mContext = this;

    // strings for intent extra arguments
    String mPrimaryKeyExtra;
    String mVimeoVideoUriExtra;
    String mLocalVideoFileExtra;

    // string for the terms and conditions shared preference argument
    String mHasAgreedToTermsFlag;

    // the Fragments in the ViewPager
    communityMomentsInterface communityFragment;
    myMomentInterface myMomentsFragment;

    // integers for request codes from startActivityForResult
    final int MAKE_A_MOMENT_REQUEST_CODE = 1;
    final int TERMS_AND_CONDITIONS_REQUEST_CODE = 2;

    // ui references
    Toolbar mToolbar;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    TabLayout mTabLayout;
    MenuItem mStarOfDavidMenuItem;

    // tab position of the MyMomentsFragment
    final int MY_MOMENTS_POSITION = 1;

    // regular expression to use to grab a Moment's subject
    Pattern pattern = Pattern.compile("([A-Za-z\\s\\.&\\-\\(\\)']+)\\s-\\s([A-Za-z\\s?]+)");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //inflate the activity's corresponding layout
        setContentView(R.layout.activity_main);

        // get the string resources for the outgoing intent extras
        mPrimaryKeyExtra = getResources().getString(R.string.primary_key_extra);
        mVimeoVideoUriExtra = getString(R.string.vimeo_video_uri_extra);
        mLocalVideoFileExtra = getString(R.string.local_video_file_extra);

        // get the shared preference argument
        mHasAgreedToTermsFlag = getString(R.string.has_agreed_to_terms_and_conditions);

        // check if the person has agreed to terms and conditions
        checkForTermsAndConditions();

        //set the ActionBar to the toolbar we created
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //set up the ViewPager for the TabLayout
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(mPagerAdapter);

        //set up the TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabLayout();
    }

    private void checkForTermsAndConditions() {

        // get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get the hasAgreed value from them
        Boolean hasAgreed = sharedPreferences.getBoolean(mHasAgreedToTermsFlag, false);

        // if they haven't agreed, open the TermsAndConditionsActivity
        if(!hasAgreed) {

            Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);

            startActivityForResult(termsAndConditionsIntent, TERMS_AND_CONDITIONS_REQUEST_CODE);

        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

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

                                        MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
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

    private void setupTabLayout() {
        //set all the properties and behavior of the TabLayout

        //bind the TabLayout to the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * CALLBACKS TO BE CALLED BY THE MOMENT
     */

    // the callback method that will be called when Videos are selected
    public void onVideoSelect(Moment moment) {
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

                videoIntent.putExtra(mVimeoVideoUriExtra, moment.getVideoUri());

            }

            // otherwise attach the local video uri to it
            else {

                videoIntent.putExtra(mLocalVideoFileExtra, moment.getLocalVideoFilePath());

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

    // the callback method that will be called when a MomentCard is clicked from MyMoments
    public void onMyMomentCardClick(Moment moment) {
        // start a MakeAMomentActivity for result. Pass it the primary key of the Moment

        Intent makeAMomentIntent = new Intent(this, MakeAMomentActivity.class);
        makeAMomentIntent.putExtra(mPrimaryKeyExtra, moment.getPrimaryKey());
        startActivityForResult(makeAMomentIntent, MAKE_A_MOMENT_REQUEST_CODE);

    }

    // the callback method that will be called when the share button is clicked in a
    public void onMyShareClick(final Moment moment) {

        final Context context = this;

        // if the Moment is LIVE
        if(moment.getMomentState() == MomentStateEnum.LIVE) {

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

        // if the Moment is PRIVATE
        else if(moment.getMomentState() == MomentStateEnum.PRIVATE) {

            // express a send Intent
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            // fetch the Moment's local video file
            File videoFile = new File(moment.getLocalVideoFilePath());

            Uri videoUri;

            // if this phone is running Nougat
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                // we have to use a FileProvider to get a content Uri
                videoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", videoFile);

            }

            else {

                // otherwise we can just get the file Uri
                videoUri = Uri.fromFile(videoFile);

            }


            // add the text to the Intent
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sms_message));

            // put the video data on the Intent
            sendIntent.putExtra(Intent.EXTRA_STREAM, videoUri);

            // sending all types
            sendIntent.setType("*/*");

            // grant the receiving Activity permission to access the content Uri
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // start the Activity
            startActivity(sendIntent);

        }

    }

    public void onCommunityShareClick(final Moment moment) {

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

    // the callback method that will be called when the dots are clicked in a MyMoments MomentCard
    public void onMyDotsClick(final Moment moment) {

        final Context context = this;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.my_moments_dots_dialog_array)
                .itemsColor(getResources().getColor(R.color.red))
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // the user chose to delete the Moment. tell the Moment to delete itself and..
                                // ..tell the MyMomentsFragment to reload its mViewModelList

                                if(moment.getMomentState() != MomentStateEnum.UPLOADING) {

                                    AsyncDeleteMoment asyncDeleteMoment = new AsyncDeleteMoment();
                                    asyncDeleteMoment.execute(moment);

                                }

                                else {
                                    // tell the user not to do that

                                    dialog.hide();

                                    MaterialDialog dontDialog = new MaterialDialog.Builder(context)
                                            .title(getString(R.string.cant_delete_title))
                                            .titleGravity(GravityEnum.CENTER)
                                            .content(getString(R.string.cant_delete_content))
                                            .contentGravity(GravityEnum.CENTER)
                                            .positiveText(getString(R.string.cant_delete_ok))
                                            .itemsGravity(GravityEnum.CENTER)
                                            .positiveColor(getResources().getColor(R.color.actionBlue))
                                            .theme(Theme.LIGHT)
                                            .show();


                                }


                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.textLight))
                .show();

    }

    // the callback method that will be called when the dots are clicked in a Community MomentCard
    public void onCommunityDotsClick(final Moment moment) {

        final Context context = this;

        // user chose to search for more videos by this person

        String title = moment.getTitle();

        Matcher matcher = pattern.matcher(title);

        matcher.find();

        final String matchedString = matcher.group(1);

        String[] communityDotsArray = getResources().getStringArray(R.array.community_moments_dots_dialog_array);

        String formattedString = String.format(communityDotsArray[1], matchedString);

        communityDotsArray[1] = formattedString;

        String[] communityDotsArraySansSearch = getResources().getStringArray(R.array.community_moments_dots_dialog_array_sans_search);


        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(moment.getTitle().contains("-") ? communityDotsArray: communityDotsArraySansSearch)
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

                            case 1:
                                // user chose to search for more videos by this person

                                String title = moment.getTitle();

                                Intent searchResultIntent = new Intent(mContext, SearchResultActivity.class);

                                searchResultIntent.putExtra(getString(R.string.search_extra), matchedString);

                                startActivity(searchResultIntent);


                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.textLight))
                .show();


    }

    public void onFailedStateClick(Moment moment) {
        // when the stateConstraintLayout is clicked in a Moment with FAILED upload state

        // get the Moment
        final Moment managedMoment = Moment.findMoment(moment.getPrimaryKey());

        // create an UPLOADING state
        final MomentStateEnum uploadingState = MomentStateEnum.UPLOADING;

        // persist that state to the Moment
        managedMoment.persistUpdates(new PersistenceExecutor() {

            @Override
            public void execute() {

                managedMoment.setEnumState(uploadingState);

            }

        });

        // kick off the uploadService
        managedMoment.uploadMoment(this.getApplicationContext());

        // notify the Fragment to refresh its view list to reflect the UPLOADING Moment
        myMomentsFragment.refreshListFromActivity();


    }


    // the callback method that will be called when the MomentPrompt is clicked in the CommunityFragment RecyclerView
    public void onMomentPromptClick() {

        // get the MyMomentsFragment
        TabLayout.Tab myMomentsTab = mTabLayout.getTabAt(MY_MOMENTS_POSITION);

        // select it
        myMomentsTab.select();

        myMomentsFragment.openMakeAMomentActivity();

    }

    public void onWelcomeDismiss() {

        // get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // set the value to false so it'll never insert one again
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.is_first_visit), false);
        editor.commit();

        communityFragment.dismissWelcomeMessage();

    }

    private class AsyncDeleteMoment extends AsyncTask<Moment, Void, Boolean> {

        protected Boolean doInBackground(Moment... moments) {

            // doInBackground requires variadic arguments, but there will only ever be one argument...
            // ... so grab the Moment from moment[0]
            Moment moment = moments[0];

            Boolean deleted = false;

            deleted = moment.endItAll(mContext);

            return deleted;

        }

        protected void onPostExecute(Boolean deleted) {

            if(deleted) {
                // it was deleted successfully. tell the fragment to reload its mViewModelList

                // wait for half a second to let Realm catch up
                // this is BAD™ but Realm only allows callbacks for general changes to objects, not delete specifically
                // so without some funny business, this is the easiest way
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    public void run() {

                        // refresh the list to reflect the delete
                        myMomentsFragment.refreshListFromActivity();

                    }

                }, 500);

            }

        }

    }

    private class PagerAdapter extends FragmentPagerAdapter {
        //bind fragments to ViewPager for the TabLayout

        private Context mContext;
        
        //number of tabs
        private int mNumOfTabs = 2;

        //titles for the tabs
        private String[] tabTitles = new String[] {
                getResources().getString(R.string.community), getResources().getString(R.string.my_moments)};

        //constructor
        private PagerAdapter(FragmentManager fragmentManager, Context context) {

            super(fragmentManager);
            mContext = context;

        }

        @Override
        public Fragment getItem(int position) {
            //return a fragment based on TabLayout position

            switch(position) {
                case 0:
                    //return the fragment for the "Community" tab
                    return CommunityFragment.newInstance();
                case 1:
                    //return the fragment for the "My Moments" tab
                    return MyMomentsFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

            // save the appropriate reference depending on position
            switch (position) {

                case 0:

                    communityFragment = (CommunityFragment) createdFragment;

                    break;

                case 1:

                    myMomentsFragment = (MyMomentsFragment) createdFragment;

                    break;

            }

            return createdFragment;

        }

        @Override
        public int getCount(){

            return mNumOfTabs;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return tab title based on item position
            return tabTitles[position];

        }
    }

    /**
     * INTERFACES TO THE FRAGMENTS
     */
    public interface myMomentInterface {

        void refreshListFromActivity();

        void openMakeAMomentActivity();

    }

    public interface communityMomentsInterface {

        void dismissWelcomeMessage();

    }

}
