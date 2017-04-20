package com.tikkunolam.momentsintime;


import android.content.Context;

import java.util.ArrayList;

public class MomentList {

    /**
     * INSTANCE VARIABLES
     */

    private ArrayList<Moment> mMoments;
    private VimeoNetworker mVimeoNetworker;

    /**
     * CONSTRUCTORS
     */

    public MomentList(Context applicationContext) {
        // takes a context argument only to pass to networker so it can access resources

        mMoments = new ArrayList<>();
        mVimeoNetworker = new VimeoNetworker(applicationContext);

    }

    /**
     * Instance Methods
     */

    public ArrayList<Moment> getMomentList() {
        // just return the moment list. useless for now as all methods will return the list.

        return mMoments;

    }


    public void getCommunityMoments() {
        // update the moments list by fetching the Community Videos list

        mMoments = mVimeoNetworker.getCommunityMoments();


    }

    public void getMyMoments() {
        // update the moments list


    }


}
