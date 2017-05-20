package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class TopicPromptHolder extends RecyclerView.ViewHolder{
    /**
     * a holder for the textView that appears in the first position of the RecyclerView...
     * ...in the TopicActivity
     */

    Context mContext;

    // a callback to the TopicActivity
    TopicPromptInteractionListener mActivityCallback;

    // String value to be fetched from resources to identify the clickable part of the text
    String mClickableText;

    public TextView topicPromptTextView;

    public TopicPromptHolder(Context context, View view) {

        super(view);

        mContext = context;

        mActivityCallback = (TopicPromptInteractionListener) context;

        mClickableText = context.getString(R.string.topic_prompt_highlight);

        topicPromptTextView = (TextView) view;

    }

    public void setText(String text) {
        // sets the TextView's text, styles it and applies the listener

        topicPromptTextView.setText(text);

        applyListener();

    }

    private void applyListener() {

        String text = (String) topicPromptTextView.getText();

        // determine where our string to be highlighted begins
        int beginningOfHighlight = text.indexOf(mClickableText);
        // determine where it ends
        int endOfHighlight = beginningOfHighlight + mClickableText.length();

        // set the text color of the entire TextView
        topicPromptTextView.setTextColor(mContext.getResources().getColor(R.color.text));

        // make a SpannableString
        SpannableString spannableString = new SpannableString(text);

        // make a ClickableSpan for the part that should be clickable
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View view) {

                mActivityCallback.onTopicPromptInteraction();

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

        // set the ClickableSpan on the SpannableString from beginningOfHighlight ---> endOfHighlight
        spannableString.setSpan(clickableSpan, beginningOfHighlight, endOfHighlight, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set the SpannableString to the TextView
        topicPromptTextView.setText(spannableString);
        topicPromptTextView.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public interface TopicPromptInteractionListener {
        // an interface to alert an Activity to a click on the topic_prompt

        void onTopicPromptInteraction();

    }

}
