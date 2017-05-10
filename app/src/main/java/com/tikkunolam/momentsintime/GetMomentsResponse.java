package com.tikkunolam.momentsintime;

import java.util.ArrayList;

public class GetMomentsResponse {
    /**
     * Class to hold the two relevant values to be returned from a fetch from Vimeo
     */

    // the list of Moments to return to the MomentList
    private ArrayList<Moment> mMomentList;

    // a boolean indicating whether there is a next page to load
    private boolean mNextPageExists;

    public GetMomentsResponse() {

    }

    public ArrayList<Moment> getMomentList() {

        return mMomentList;

    }

    public boolean nextPageExists() {

        return mNextPageExists;

    }

    public void setMomentList(ArrayList<Moment> moments) {

        mMomentList = moments;

    }

    public void setNextPageExists(boolean nextPageExists) {

        mNextPageExists = nextPageExists;

    }


}
