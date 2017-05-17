package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
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

import com.afollestad.materialdialogs.MaterialDialog;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements MomentInteractionListener {

    // tag for logging purposes
    private final String TAG = "MainActivity";

    // strings for intent extra arguments
    String mPrimaryKeyExtra;
    String mVimeoVideoUriExtra;
    String mLocalVideoUriExtra;

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
        mLocalVideoUriExtra = getString(R.string.local_video_uri_extra);

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

            videoIntent.putExtra(mLocalVideoUriExtra, moment.getLocalVideoUri());

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
    public void onMyShareClick(Moment moment) {

        // produce the dialog that presents sharing options
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.moment_share_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share on Facebook

                                break;

                            case 1:
                                // user chose to share through message

                                break;

                            case 2:
                                // user chose to cancel... auto dismiss is on so do nothing

                                break;

                        }

                    }

                })
                .show();

    }

    public void onCommunityShareClick(Moment moment) {

        // produce the dialog that presents sharing options
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.moment_share_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share on Facebook

                                break;

                            case 1:
                                // user chose to share through message

                                break;

                            case 2:
                                // user chose to cancel... auto dismiss is on so do nothing

                                break;

                        }

                    }

                })
                .show();

    }

    // the callback method that will be called when the dots are clicked in a MyMoments MomentCard
    public void onMyDotsClick(Moment moment) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.my_moments_dots_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // the user chose to delete the Moment. tell the Moment to delete itself and..
                                // ..tell the MyMomentsFragment to reload its mViewModelList

                                break;

                            case 1:
                                // the user chose to cancel. auto dismiss is on so do nothing

                        }

                    }

                })
                .show();

    }

    // the callback method that will be called when the dots are clicked in a Community MomentCard
    public void onCommunityDotsClick(Moment moment) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .items(R.array.community_moments_dots_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to share

                                break;

                            case 1:
                                // user chose to report. compose an email.

                                break;

                            case 2:
                                // user chose to cancel. auto dismiss is on so do nothing

                        }

                    }

                })
                .show();


    }


    // the callback method that will be called when the MomentPrompt is clicked in the CommunityFragment RecyclerView
    public void onMomentPromptClick() {


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
        public int getCount(){

            return mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return tab title based on item position
            return tabTitles[position];

        }
    }

}
