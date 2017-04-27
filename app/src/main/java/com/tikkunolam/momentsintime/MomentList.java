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

    /**
     * CONSTRUCTORS
     */

    public MomentList(Context applicationContext) {
        // takes a context argument only to pass to networker so it can access resources

        mMoments = new ArrayList<>();
        mVimeoNetworker = new VimeoNetworker(applicationContext);

        mCurrentPage = 0;

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

        // fetch the current page of videos
        mMoments.addAll(mVimeoNetworker.getCommunityMoments(mCurrentPage + 1));

        // increment mCurrentPage
        mCurrentPage++;


    }

    public void getMyMoments() {
        // update the moments list


    }

    public int getCurrentPage() {

        return mCurrentPage;

    }


}
