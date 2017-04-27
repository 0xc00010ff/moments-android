package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.os.Build.VERSION_CODES.M;
import static okhttp3.internal.Internal.instance;

public class MakeAMomentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    /**
     * this adapter controls the display of every single view in the MakeAMomentActivity
     * the whole Activity is just a RecyclerView
     */

    Context mContext;

    // list of all things to be bound to the RecyclerView cells
    ArrayList<Object> mViewModelList;

    // integer identifiers for the view types
    final int SECTION_TITLE = 0, SECTION_PROMPT = 1, NOTE = 2;




    /**
     * CONSTRUCTORS
     */
    public MakeAMomentAdapter(Context context, ArrayList<Object> viewModels) {

        mContext = context;

        mViewModelList = viewModels;

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mViewModelList.size();

    }

    public int getItemViewType(int position) {

        if(mViewModelList.get(position) instanceof SectionTitle) {

            return SECTION_TITLE;

        }

        else if(mViewModelList.get(position) instanceof SectionPrompt) {

            return SECTION_PROMPT;

        }

        else if(mViewModelList.get(position) instanceof String) {

            return NOTE;

        }

        else {

            return -1;

        }


    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        /**
         * get the view from the ViewGroup and create and return a Holder with it
         * passes the Holder it creates to onBindViewHolder ^
         */

        // generic ViewHolder for either type of Holder
        RecyclerView.ViewHolder viewHolder = null;


        switch(viewType) {

            case SECTION_TITLE:

                // inflate a section_title_text layout
                View sectionTitleView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_title_text, parent, false);

                // fill the ViewHolder with a SectionTitleHolder
                viewHolder = new SectionTitleHolder(mContext, sectionTitleView);

                break;

            case SECTION_PROMPT:

                // inflate a section_prompt_text layout
                View sectionPromptView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_prompt_text, parent, false);

                // fill the ViewHolder with a SectionPromptHolder
                viewHolder = new SectionPromptHolder(mContext, sectionPromptView);

                break;

            case NOTE:

                // inflate a note_card layout
                View noteCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.note_card, parent, false);

                // fill the ViewHolder with a NoteCardHolder
                viewHolder = new NoteCardHolder(mContext, noteCardView);

                break;

            default:
                // unexpected value... will just return a null RecyclerView.ViewHolder

                break;

        }


        return viewHolder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // fill the holder with the values it needs

        switch(holder.getItemViewType()) {

            case SECTION_TITLE:
                // fill a SectionTitleHolder

                // cast the generic Holder to a SectionTitleHolder
                SectionTitleHolder sectionTitleHolder = (SectionTitleHolder) holder;

                // cast the generic Object at the position in mViewModelList to a SectionTitle
                SectionTitle sectionTitle = (SectionTitle) mViewModelList.get(position);

                // retrieve the title string from the SectionTitle object
                String title = sectionTitle.getSectionTitle();

                // set the SectionTitleHolder's sectionTitleTextView value
                sectionTitleHolder.sectionTitleTextView.setText(title);

                break;

            case SECTION_PROMPT:
                // fill a SectionPromptHolder

                // cast the generic Holder to a SectionPromptHolder
                SectionPromptHolder sectionPromptHolder = (SectionPromptHolder) holder;

                // cast the generic Object at the position in mViewModelList to a SectionPrompt
                SectionPrompt sectionPrompt = (SectionPrompt) mViewModelList.get(position);

                // retrieve the prompt string from the SectionPrompt object
                String prompt = sectionPrompt.getSectionPrompt();

                // set the SectionPromptHolder's sectionPromptTextView value
                sectionPromptHolder.sectionPromptTextView.setText(prompt);

                break;

            case NOTE:
                // fill a NoteCardHolder

                // cast the generic Holder to a NoteCardHolder
                NoteCardHolder noteCardHolder = (NoteCardHolder) holder;

                // cast the generic Object at the position in mViewModelList to a String
                String noteString = (String) mViewModelList.get(position);

                // set the NoteCardHolder's noteCardTextView value
                noteCardHolder.noteCardTextView.setText(noteString);

                break;

            default:
                // received a null Holder
                // nothing to do... do nothing

                break;


        }

    }

}
