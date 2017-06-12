package com.tikkunolam.momentsintime;

import android.content.Context;

public class UploadMessage {
    /**
     * this class holds the data to be displayed in the views held by an UploadMessageHolder
     * its purpose is to distinguish itself in the MomentCardAdapter for proper data binding
     */

    // Strings for the data to hydrate the views
    String mTitleString;
    String mContentString;
    String mPromptString;

    // the primary key of the Moment that inspired the UploadMessage, so a share can eventually be carried out with it
    String mPrimaryKeyString;

    public UploadMessage(Context context, String primaryKey) {

        mTitleString = context.getString(R.string.upload_message_title);
        mContentString = context.getString(R.string.upload_message_content);
        mPromptString = context.getString(R.string.upload_message_prompt);

        mPrimaryKeyString = primaryKey;

    }

    public String getTitle() {

        return mTitleString;

    }

    public String getContent() {

        return mContentString;

    }

    public String getPrompt() {

        return mPromptString;

    }

    public String getPrimaryKey() {

        return mPrimaryKeyString;

    }

}
