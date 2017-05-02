package com.tikkunolam.momentsintime;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MomentPromptHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the moment_prompts
     * they appear in the seventh position of the RecyclerView in the CommunityFragment
     */

    /**
     * INSTANCE VARIABLES
     */

    // reference to the MainActivity for callbacks
    FragmentInteractionListener mActivityCallback;

    // the Moment prompt TextView
    TextView moment_prompt_textView;

    // the TextView for the next part of the Moment prompt
    TextView moment_prompt_cont_textView;

    // the TextView for the ask to interview part of the Moment prompt
    TextView ask_to_interview_textView;


    /**
     * CONSTRUCTORS
     */

    public MomentPromptHolder(Context context, View view) {
        // fill the Holder views

        // call the default constructor
        super(view);

        // assign the mActivityCallback from context
        mActivityCallback = (MainActivity) context;

        moment_prompt_textView = (TextView) view.findViewById(R.id.moment_prompt_textView);
        moment_prompt_cont_textView = (TextView) view.findViewById(R.id.moment_prompt_cont_textView);
        ask_to_interview_textView = (TextView) view.findViewById(R.id.ask_to_interview_textView);

        ask_to_interview_textView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                mActivityCallback.onMomentPromptClick();

            }

        });


    }


}
