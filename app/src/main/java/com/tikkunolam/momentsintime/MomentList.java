package com.tikkunolam.momentsintime;


import android.content.Context;

import java.util.ArrayList;

public class MomentList {

    /**
     * INSTANCE VARIABLES
     */

    private ArrayList<Moment> mMoments;
    private VimeoNetworker mVimeoNetworker;

    // keeps up with the last loaded page of Vimeo videos
    private int mCurrentPage;

    // signifies if there is a next page or not
    private boolean mNextPageExists;

    /**
     * CONSTRUCTORS
     */

    public MomentList(Context applicationContext) {
        // takes a context argument only to pass to networker so it can access resources

        mMoments = new ArrayList<>();
        mVimeoNetworker = new VimeoNetworker(applicationContext);

        mCurrentPage = 0;
        mNextPageExists = true;

    }

    /**
     * Instance Methods
     */

    public ArrayList<Moment> getMomentList() {
        // just return the mMoment list. useless for now as all methods will return the list.

        return mMoments;

    }


    public void getCommunityMoments() {
        // update the moments list by fetching the Community Videos list

        // if there is a next page to fetch fetch the Moments

        if(mNextPageExists) {

            // get a GetMomentsResponse from the VimeoNetworker
            GetMomentsResponse getMomentsResponse = mVimeoNetworker.getCommunityMoments(mCurrentPage + 1);

            mNextPageExists = getMomentsResponse.nextPageExists();

            mMoments.addAll(getMomentsResponse.getMomentList());

            // increment mCurrentPage
            mCurrentPage++;

        }

    }

    public void getMyMoments() {
        // update the moments list


    }

    public int getCurrentPage() {

        return mCurrentPage;

    }


}
