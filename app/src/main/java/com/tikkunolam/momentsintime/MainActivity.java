package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity
    implements FragmentInteractionListener{

    // tag for logging purposes
    private final String TAG = "MainActivity";

    // integer identifiers for Activity results
    final int NEW_MOMENT = 1;

    // strings for intent extra arguments
    String mPrimaryKeyExtra;
    String mVimeoVideoUriExtra;

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

    // the callback method that will be called when Videos are selected
    public void onMomentSelect(Moment moment) {
        // open a new Activity to view the Moment

        // create the Intent
        Intent videoIntent = new Intent(getBaseContext(), VideoViewActivity.class);

        // add the Parcelable Moment to it
        videoIntent.putExtra(mVimeoVideoUriExtra, moment.getVideoUri());

        // open the activity
        startActivity(videoIntent);

    }

    // the callback method that will be called when the FloatingActionButton or NoMomentPrompt in MyMomentsFragment is clicked
    public void onNewMomentClick() {
        // open the MakeAMomentActivity

        // create the Intent
        Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

        // open the activity
        startActivityForResult(makeAMomentIntent, NEW_MOMENT);


    }

    // the callback method that will be called when the MomentPrompt is clicked in the CommunityFragment RecyclerView
    public void onMomentPromptClick() {

        // do whatever to share
        Log.d(TAG, "MOMENT PROMPT CLICKED");

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
