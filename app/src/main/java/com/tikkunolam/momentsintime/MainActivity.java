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
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //set the action bar to the toolbar we created
        setSupportActionBar(mToolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabLayout();
    }

    private void setupTabLayout(){
        //sets all the properties and behavior of the tab layout
        mTabLayout.setupWithViewPager(mViewPager);
        /**
         * add an OnTabSelectedListener to the TabLayout to handle the color changes and
         * other dynamic effects when tabbing about
         **/
    }
    private class PagerAdapter extends FragmentPagerAdapter{
        private int mNumOfTabs = 2;
        private String[] tabTitles = new String[] {
                getResources().getString(R.string.community), getResources().getString(R.string.my_moments)};
        private Context mContext;

        private PagerAdapter(FragmentManager fm, Context context){
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    //TODO: return community fragment
                    return CommunityFragment.newInstance();
                case 1:
                    //TODO: return my moments fragment
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
