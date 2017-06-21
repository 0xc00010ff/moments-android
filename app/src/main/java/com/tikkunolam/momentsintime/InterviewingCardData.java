package com.tikkunolam.momentsintime;

import android.net.Uri;

public class InterviewingCardData {

    // values that will fill an InterviewingCardHolder
    Uri mIntervieweePhotoUri;
    String mIntervieweeName;
    String mIntervieweeRelation;

    /**
     * CONSTRUCTORS
     */
    public InterviewingCardData(String name) {

        mIntervieweeName = name;

    }

    /**
     * METHODS
     */
    public Uri getIntervieweePhotoUri() {

        return mIntervieweePhotoUri;

    }

    public String getIntervieweeName() {

        return mIntervieweeName;

    }

    public String getIntervieweeRelation() {

        return mIntervieweeRelation;

    }

    public void setIntervieweePhotoUri(Uri intervieweePhotoUri) {

        mIntervieweePhotoUri = intervieweePhotoUri;

    }

    public void setIntervieweeName(String intervieweeName) {

        mIntervieweeName = intervieweeName;

    }

    public void setIntervieweeRelation(String intervieweeRelation) {

        mIntervieweeRelation = intervieweeRelation;

    }

}
