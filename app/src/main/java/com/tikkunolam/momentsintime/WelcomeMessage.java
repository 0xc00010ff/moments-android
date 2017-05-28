package com.tikkunolam.momentsintime;

import android.content.Context;

public class WelcomeMessage {
    // a class to hold the data displayed in the welcome message header of the CommunityFragment on first use

    // the Strings holding the info to be displayed
    String mWelcomeMessageTitle, mWelcomMessageContent;

    public WelcomeMessage(Context context) {

        // fetch the Strings from resources
        mWelcomeMessageTitle = context.getString(R.string.welcome_message_title);
        mWelcomMessageContent = context.getString(R.string.welcome_message_content);

    }

    public String getTitle() {

        return mWelcomeMessageTitle;

    }

    public String getContent() {

        return mWelcomMessageContent;

    }

}
