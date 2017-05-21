package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SectionPromptHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the section_prompt_text that appears
     * in the RecyclerView of the MakeAMomentActivity
     */

    // activity context
    Context mContext;

    // activity callback
    MakeAMomentActivity mActivityCallback;

    // ui references
    FrameLayout mSectionPromptFrameLayout;
    TextView mSectionPromptTextView;

    // Strings specified in resources as prompt strings
    // for matching purposes to determine the action of the listener applied in applyListener()
    String mInterviewingPrompt;
    String mTopicPrompt;
    String mVideoPrompt;
    String mNotesPrompt;

    // a boolean indicating if the onClicks should be set
    boolean mClickable = true;

    public SectionPromptHolder(Context context, View view, boolean clickable) {

        // call the superclass's constructor
        super(view);

        // set the context
        mContext = context;

        // set the mActivityCallback
        mActivityCallback = (MakeAMomentActivity) mContext;

        mClickable = clickable;

        // initialize the Views
        mSectionPromptFrameLayout = (FrameLayout) view;
        mSectionPromptTextView = (TextView) mSectionPromptFrameLayout.findViewById(R.id.section_prompt_textView);

        // set all the prompt Strings from resources
        mInterviewingPrompt = context.getResources().getString(R.string.interviewing_prompt);
        mTopicPrompt = context.getResources().getString(R.string.add_topic_prompt);
        mVideoPrompt = context.getResources().getString(R.string.video_prompt);
        mNotesPrompt = context.getResources().getString(R.string.notes_prompt);

    }

    public void setText(String text) {
        // called to set the text from the MakeAMomentAdapter and then apply the proper listener

        mSectionPromptTextView.setText(text);

        // apply the listener
        applyListener(text);


    }

    public void applyListener(String text) {
        // applies a listener to the textView based on the text put in it

        if(text.equals(mInterviewingPrompt)) {
            // if it's interview text apply a listener that calls the HolderInteractionListener's onInterviewingPromptClick

            if(mClickable) {

                mSectionPromptTextView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        mActivityCallback.onInterviewingPromptClick();

                    }

                });

            }

        }

        else if(text.equals(mTopicPrompt)) {
            // if it's topic text apply a listener that calls the HolderInteractionListener's onTopicPromptClick

            if(mClickable) {

                mSectionPromptTextView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        mActivityCallback.onTopicPromptClick();

                    }

                });

            }

        }

        else if(text.equals(mVideoPrompt)) {
            // if it's video text apply a listener that calls the HolderInteractionListener's onVideoPromptClick

            // set the color of the TextView to the light grey
            mSectionPromptTextView.setTextColor(mContext.getResources().getColor(R.color.textLight));

            if(mClickable) {

                // turn text into a SpannableString
                SpannableString spannableText = new SpannableString(text);

                // split the text around the word "or"
                String[] words = text.split("\\sor\\s");

                // create a ClickableSpan that specifies what to do when the video string is clicked
                ClickableSpan videoSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View textView) {

                        mActivityCallback.onVideoPromptClick();

                    }

                    @Override
                    public void updateDrawState(TextPaint drawState) {
                        // remove the funny style SpannableString adds

                        // set the color
                        drawState.setColor(mContext.getResources().getColor(R.color.actionBlue));

                        // remove the underline
                        drawState.setUnderlineText(false);
                    }

                };

                // set the ClickableSpan on the SpannableString from the beginning, to the end of the first word
                spannableText.setSpan(videoSpan, 0, words[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                // create a clickable span that specifies what to do when the upload string is clicked
                ClickableSpan uploadSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View textView) {

                        mActivityCallback.onUploadPromptClick();

                    }

                    @Override
                    public void updateDrawState(TextPaint drawState) {
                        // remove the funny style SpannableString adds

                        // set the color
                        drawState.setColor(mContext.getResources().getColor(R.color.actionBlue));

                        // remove the underline
                        drawState.setUnderlineText(false);
                    }

                };

                // set the uploadSpan on the spannableText from the beginning of the second word, to the end
                spannableText.setSpan(uploadSpan, words[0].length() + 3, spannableText.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


                mSectionPromptTextView.setText(spannableText);
                mSectionPromptTextView.setMovementMethod(LinkMovementMethod.getInstance());

            }

        }

        else if(text.equals(mNotesPrompt)) {
            // if it's notes text apply a listener that calls the HolderInteractionListener's onNotesPromptClick

            mSectionPromptTextView.setTextColor(mContext.getResources().getColor(R.color.actionBlue));

            if(mClickable) {

                mSectionPromptTextView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        mActivityCallback.onNotesPromptClick();

                    }

                });

            }

        }

    }

}
