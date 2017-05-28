package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class WelcomeMessageHolder extends RecyclerView.ViewHolder{

    // reference to the MainActivity for callbacks
    MomentInteractionListener mActivityCallback;

    // the TextViews to display the data from a WelcomeMessage
    TextView mWelcomeMessageTitleTextView, mWelcomeMessageContentTextView;

    public WelcomeMessageHolder(Context context, View view) {

        // call the default constructor
        super(view);

        mActivityCallback = (MomentInteractionListener) context;

        mWelcomeMessageTitleTextView = (TextView) view.findViewById(R.id.welcome_message_title_textView);

        mWelcomeMessageContentTextView = (TextView) view.findViewById(R.id.welcome_message_content_textView);

    }

}
