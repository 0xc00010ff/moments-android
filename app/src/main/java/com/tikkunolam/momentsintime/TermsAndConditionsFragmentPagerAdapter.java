package com.tikkunolam.momentsintime;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

public class TermsAndConditionsFragmentPagerAdapter extends FragmentPagerAdapter{

    public TermsAndConditionsFragmentPagerAdapter(FragmentManager fragmentManager){

        super(fragmentManager);

    }
    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:

                return TermsAndConditionsFragment.newInstance();

            case 1:

                return PrivacyPolicyFragment.newInstance();

        }

        return null;

    }

    @Override
    public int getCount() {

        return 2;

    }
}
