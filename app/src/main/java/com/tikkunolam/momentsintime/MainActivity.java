package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    //ui references
    Toolbar mToolbar;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //inflates the activity's corresponding layout
        setContentView(R.layout.activity_main);

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
        //sets all the properties and behavior of the TabLayout

        //bind the TabLayout to the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
    }
    private class PagerAdapter extends FragmentPagerAdapter {
        //binds fragments to ViewPager for the TabLayout

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
            //returns a fragment based on TabLayout position

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
