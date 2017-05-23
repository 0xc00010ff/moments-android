package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements MomentInteractionListener {

    // tag for logging purposes
    private final String TAG = "MainActivity";

    Context mContext = this;

    // strings for intent extra arguments
    String mPrimaryKeyExtra;
    String mVimeoVideoUriExtra;
    String mLocalVideoFileExtra;

    // the Fragments in the ViewPager
    Fragment communityFragment;
    myMomentInterface myMomentsFragment;

    // integers for request codes from startActivityForResult
    final int MAKE_A_MOMENT_REQUEST_CODE = 1;

    // ui references
    Toolbar mToolbar;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //inflate the activity's corresponding layout
        setContentView(R.layout.activity_main);

        // get the string resources for the outgoing intent extras
        mPrimaryKeyExtra = getResources().getString(R.string.primary_key_extra);
        mVimeoVideoUriExtra = getString(R.string.vimeo_video_uri_extra);
        mLocalVideoFileExtra = getString(R.string.local_video_file_extra);

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

        // produce the dialog that presents sharing options
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.moment_share_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share on Facebook

                                MaterialDialog anotherDialog = new MaterialDialog.Builder(context)
                                        .title(getString(R.string.in_development_title))
                                        .content(getString(R.string.in_development_content))
                                        .positiveText(getString(R.string.in_development_ok))
                                        .positiveColor(getResources().getColor(R.color.actionBlue))
                                        .show();

                                break;

                            case 1:
                                // user chose to share through message

                                // make an Intent for sending an sms
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"));

                                // add a message to it
                                sendIntent.putExtra("sms_body", getString(R.string.sms_message) + moment.getVideoUrl());

                                // start the Activity
                                startActivity(sendIntent);

                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.red))
                .show();

    }

    public void onCommunityShareClick(final Moment moment) {

        final Context context = this;

        // produce the dialog that presents sharing options
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.moment_share_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share on Facebook

                                // show the comming soon dialog
                                MaterialDialog anotherDialog = new MaterialDialog.Builder(context)
                                        .title(getString(R.string.in_development_title))
                                        .content(getString(R.string.in_development_content))
                                        .positiveText(getString(R.string.in_development_ok))
                                        .positiveColor(getResources().getColor(R.color.actionBlue))
                                        .show();

                                break;

                            case 1:
                                // user chose to share through message

                                // make an Intent for sending an sms
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"));

                                // add a message to it
                                sendIntent.putExtra("sms_body", getString(R.string.sms_message) + moment.getVideoUrl());

                                // start the Activity
                                startActivity(sendIntent);

                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.actionBlue))
                .show();

    }

    // the callback method that will be called when the dots are clicked in a MyMoments MomentCard
    public void onMyDotsClick(final Moment moment) {

        final Context context = this;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.my_moments_dots_dialog_array)
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
                                            .positiveColor(getResources().getColor(R.color.colorPrimary))
                                            .theme(Theme.LIGHT)
                                            .show();


                                }


                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.actionBlue))
                .show();

    }

    // the callback method that will be called when the dots are clicked in a Community MomentCard
    public void onCommunityDotsClick(final Moment moment) {

        final Context context = this;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.community_moments_dots_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share

                                MaterialDialog newDialog = new MaterialDialog.Builder(context)
                                        .items(R.array.moment_share_dialog_array)
                                        .itemsCallback(new MaterialDialog.ListCallback() {

                                            @Override
                                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                                switch(position) {

                                                    case 0:
                                                        // user chose facebook

                                                        // show the comming soon dialog
                                                        MaterialDialog anotherDialog = new MaterialDialog.Builder(context)
                                                                .title(getString(R.string.in_development_title))
                                                                .content(getString(R.string.in_development_content))
                                                                .positiveText(getString(R.string.in_development_ok))
                                                                .positiveColor(getResources().getColor(R.color.actionBlue))
                                                                .show();

                                                        break;
                                                    case 1:
                                                        // user chose Message

                                                        // make an Intent for sending an sms
                                                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                                        sendIntent.setData(Uri.parse("sms:"));

                                                        // add a message to it
                                                        sendIntent.putExtra("sms_body", getString(R.string.sms_message) + moment.getVideoUrl());

                                                        // start the Activity
                                                        startActivity(sendIntent);
                                                }

                                            }

                                        })
                                        .show();

                                break;

                            case 1:
                                // user chose to report. compose an email.

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("text/html");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email_recipient));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_content) + " " + moment.getVideoUrl());

                                startActivity(emailIntent);

                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.actionBlue))
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

                myMomentsFragment.refreshListFromActivity();

            }

            else {
                // it wasn't deleted successfully

                // display a dialog saying something went wrong

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

    }

}
