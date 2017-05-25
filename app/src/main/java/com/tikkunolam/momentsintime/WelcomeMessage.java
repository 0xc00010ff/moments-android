package com.tikkunolam.momentsintime;

import android.content.Context;

public class WelcomeMessage {
    // a class to hold the data displayed in the welcome message header of the CommunityFragment on first use

    // the Strings holding the info to be displayed
    String mWelcomeMessageTitle, mWelcomMessageContent, mWelcomeMessageMomentPrompt;

    public WelcomeMessage(Context context) {

        // fetch the Strings from resources
        mWelcomeMessageTitle = context.getString(R.string.welcome_message_title);
        mWelcomMessageContent = context.getString(R.string.welcome_message_content);
        mWelcomeMessageMomentPrompt = context.getString(R.string.welcome_message_moment_prompt);

    }

    public String getTitle() {

        return mWelcomeMessageTitle;

    }

    public String getContent() {

        return mWelcomMessageContent;

    }

    public String getPrompt() {

        return mWelcomeMessageMomentPrompt;

    }

}
