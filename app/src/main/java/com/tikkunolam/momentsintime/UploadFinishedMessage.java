package com.tikkunolam.momentsintime;

public class UploadFinishedMessage {
    // a class for holding a message to be passed from UploadService when it's complete
    // informs whether the upload was a success and passes the Moment primaryKey and videoUri

    /**
     * VARIABLES
     */

    private boolean mIsSuccess;

    /**
     * CONSTRUCTORS
     */

    public UploadFinishedMessage() {


    }

    /**
     * METHODS
     */

    // getters
    public boolean isSuccess() {

        return mIsSuccess;

    }

    // setters

    public void setIsSuccess(boolean isSuccess) {

        mIsSuccess = isSuccess;

    }

}
