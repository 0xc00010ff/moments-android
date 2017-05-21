package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class NoteCardHolder extends RecyclerView.ViewHolder {
    /**
     * this class is a holder for the note_cards that appear in the RecyclerView
     * at the bottom of the MakeAMomentActivity.
     */

    // Activity callback
    HolderInteractionListener mActivityCallback;

    // the textView for the note_card text
    TextView mNoteCardTextView;

    // the ImageView for the dots in the corner
    ImageView mDotsImageView;

    // position in the list to let the MakeAMomentActivity know which note this is
    int mPosition = -1;

    // boolean indicating whether we should set onClickListeners
    boolean mClickable = true;

    public NoteCardHolder(Context context, View view, boolean clickable) {

        // call the superclass's constructor
        super(view);

        // get the HolderInteractionListener from the context
        mActivityCallback = (HolderInteractionListener) context;

        mClickable = clickable;

        // set the textView
        mNoteCardTextView = (TextView) view.findViewById(R.id.note_card_textView);

        // set the mDotsImageView
        mDotsImageView = (ImageView) view.findViewById(R.id.note_card_dots_imageView);

        if(mClickable) {

            // set the onClick on the mDotsImageView to display the appropriate dialog
            mDotsImageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    if(mPosition != -1) {
                        // if the position is set (it should always be) tell the MakeAMomentActivity the dots were clicked

                        mActivityCallback.onNoteCardDotsClick(mPosition);

                    }

                }

            });

        }

    }

    public void setPosition(int position) {

        mPosition = position;

    }


}
