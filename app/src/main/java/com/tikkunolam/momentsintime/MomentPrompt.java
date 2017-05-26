package com.tikkunolam.momentsintime;


import android.content.Context;

public class MomentPrompt {
    /**
     * this class just exists to be instantiated as an identifier for the MomentCardAdapter
     * the fields will always be the same and this exists to mark locations in the RecyclerView
     * where there will be a moment_prompt, and to subsequently fill a MomentPromptHolder
     */

    String mMakeAMomentString;

    String mWhoString;

    String mAskToInterviewString;

    public MomentPrompt(Context applicationContext) {

        mMakeAMomentString = applicationContext.getString(R.string.moment_prompt);
        mWhoString = applicationContext.getString(R.string.moment_prompt_cont);
        mAskToInterviewString = applicationContext.getString(R.string.moment_prompt_link);

    }

    public String getMakeAMomentString() {

        return mMakeAMomentString;

    }

    public String getWhoString() {

        return mWhoString;

    }

    public String getAskToInterviewString() {

        return mAskToInterviewString;

    }

}
