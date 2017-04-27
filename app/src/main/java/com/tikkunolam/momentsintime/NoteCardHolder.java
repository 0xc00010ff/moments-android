package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class NoteCardHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the note_cards that appear in the RecyclerView
     * at the bottom of the MakeAMomentActivity.
     */

    // Activity Context
    Context mContext;

    // the textView for the note_card text
    TextView noteCardTextView;

    public NoteCardHolder(Context context, View view) {

        // call the superclass's constructor
        super(view);

        // set the Context
        mContext = context;

        // set the textView
        noteCardTextView = (TextView) view.findViewById(R.id.note_card_textView);

    }


}
