package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.tikkunolam.momentsintime.R.string.notes;

public class NoteCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // adapter for populating note_cards in the MakeAMomentActivity

    /**
     * INSTANCE VARIABLES
     */

    Context mContext;

    // the View filling the RecyclerView cells
    int mResource;

    // the list of notes
    ArrayList<String> mNoteList;

    /**
     * CONSTRUCTORS
     */

    public NoteCardAdapter(Context context, int resource, ArrayList<String> noteList) {

        mContext = context;

        mResource = resource;

        mNoteList = noteList;

    }

    /**
     * METHODS
     */

    public int getItemCount() {

        return mNoteList.size();

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // builds a ViewHolder from the retrieved view. passes it to onBindViewHolder


        // inflate a note_card
        View noteItemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.note_card, parent, false);

        // create a Holder for the note_card
        ViewHolder viewHolder = new NoteCardHolder(noteItemView);

        // return it to be filled out
        return viewHolder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // fill a NoteCardHolder

        // cast the Holder argument to a NoteCardHolder
        NoteCardHolder noteCardHolder = (NoteCardHolder) holder;

        // get the note corresponding to the position
        String note = mNoteList.get(position);

        // set the Holder's TextView with the note's text
        noteCardHolder.noteCardTextView.setText(note);

    }




}
