package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

public class MakeAMomentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    /**
     * this adapter controls the display of every single view in the MakeAMomentActivity
     * the whole Activity is just a RecyclerView
     */

    Context mContext;

    // the View filling the RecyclerView cells
    int mResource;

    // the Moment being created
    Moment mMoment;

    // an ArrayList filled with Strings that identify the Holder to use to
    ArrayList<String> mIdentifierList;

    // integers to identify the Object type in the arraylist
    final int INTERVIEWING_TEXT = 0;
    final int INTERVIEWING_PROMPT = 1;
    final int DESCRIPTION_TEXT = 2;
    final int DESCRIPTION_PROMPT = 3;
    final int VIDEO_TEXT = 4;
    final int VIDEO_PROMPT = 5;
    final int NOTES_TEXT = 6;
    final int NOTES_PROMPT = 7;
    final int NOTE = 8;

    /**
     * CONSTRUCTORS
     */
    public MakeAMomentAdapter(Context context, Moment moment) {

        mContext = context;

        mMoment = moment;

        // fill the mIdentifierList with placeholders and notes
        mIdentifierList = new ArrayList<String>();
        updateDataSet();

    }

    public int getItemCount() {
        // return the number of items to fill the RecyclerView

        return mIdentifierList.size();

    }

    public int getItemViewType(int position) {
        // returns and integer identifier for the type of Holder to use

        // if the position is greater than 7 it's a note
        if(position > 7) {

            return NOTE;

        }

        // otherwise the position matches up to the identifier. just return position
        else {

            return position;

        }


    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        /**
         * get the view from the ViewGroup and create and return a Holder with it
         * passes the Holder it creates to onBindViewHolder ^
         */

        // generic ViewHolder for either type of Holder
        RecyclerView.ViewHolder viewHolder = null;

        // a view to inflate the section_title_text into
        View sectionTitleView;

        // a view to inflate the section_prompt_text into
        View sectionPromptText;

        switch(viewType) {

            case INTERVIEWING_TEXT:
                // this is the first cell. just return a SectionTitleHolder

                // inflate a section_title_text
                sectionTitleView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_title_text, parent, false);

                // set the viewHolder as a SectionTitleHolder
                viewHolder = new SectionTitleHolder(sectionTitleView);


                break;

            case INTERVIEWING_PROMPT:
                // second cell. decide whether to return an SectionPromptHolder or a FilledIntervieweeHolder

                if(mMoment.getInterviewee() == null) {
                    // set the Holder to a SectionPromptHolder

                    // inflate the section_prompt_text
                    sectionPromptText = LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.section_prompt_text, parent, false);

                    // set the ViewHolder to a SectionPromptHolder
                    viewHolder = new SectionPromptHolder(sectionPromptText);

                }

                else {
                    // inflate the view for a filled_interviewee and return a FilledIntervieweeHolder


                }


                break;

            case DESCRIPTION_TEXT:
                // third cell. just return a SectionTitleHolder

                // inflate a section_title_text
                sectionTitleView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_title_text, parent, false);

                // set the viewHolder as a SectionTitleHolder
                viewHolder = new SectionTitleHolder(sectionTitleView);


                break;

            case DESCRIPTION_PROMPT:
                // fourth cell. decide whether to return a SectionPromptHolder or a FilledDescriptionHolder

                if(mMoment.getDescription() == null) {
                    // set the Holder to a SectionPromptHolder

                    // inflate the section_prompt_text
                    sectionPromptText = LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.section_prompt_text, parent, false);

                    // set the ViewHolder to a SectionPromptHolder
                    viewHolder = new SectionPromptHolder(sectionPromptText);

                }

                else {
                    // inflate a filled_description and set the ViewHolder to a FilledDescriptionHolder


                }

                break;

            case VIDEO_TEXT:
                // fifth cell. just return a SectionTitleHolder

                // inflate a section_title_text
                sectionTitleView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_title_text, parent, false);

                // set the viewHolder as a SectionTitleHolder
                viewHolder = new SectionTitleHolder(sectionTitleView);


                break;

            case VIDEO_PROMPT:
                // sixth cell. decide whether to return a VideoSectionPromptHolder or a FilledVideoHolder

                if(mMoment.getLocalThumbnail() == null) {
                    // set the Holder to a SectionPromptHolder

                    // inflate the section_prompt_text
                    sectionPromptText = LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.video_section_prompt_text, parent, false);

                    // set the ViewHolder to a VideoSectionPromptHolder
                    viewHolder = new VideoSectionPromptHolder(sectionPromptText);

                }

                else {
                    // inflate a filled_video and set the Holder to a FilledVideoHolder


                }


                break;

            case NOTES_TEXT:
                // seventh cell. just return a SectionTitleHolder

                // inflate a section_title_text
                sectionTitleView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_title_text, parent, false);

                // set the viewHolder as a SectionTitleHolder
                viewHolder = new SectionTitleHolder(sectionTitleView);

                break;

            case NOTES_PROMPT:
                // eighth cell. just return a SectionPromptHolder


                // inflate the section_prompt_text
                sectionPromptText = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.section_prompt_text, parent, false);

                // set the ViewHolder to a SectionPromptHolder
                viewHolder = new SectionPromptHolder(sectionPromptText);



                break;

            case NOTE:
                // any cell beyond eight. return a NoteCardHolder

                // inflate the note_card
                View noteCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.note_card, parent, false);

                // set the ViewHolder to a NoteCardHolder
                viewHolder = new NoteCardHolder(noteCardView);


        }



        return viewHolder;

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // fill the holder with the values it needs

        // a SectionTitleHolder to be cast to from the holder
        SectionTitleHolder sectionTitleHolder;

        // a SectionPromptHolder to be cast to from the holder
        SectionPromptHolder sectionPromptHolder;



        switch(position) {

            case 0:
                // the Holder is a SectionTitleHolder to be filled with interviewing text
                sectionTitleHolder = (SectionTitleHolder) holder;

                //fill it
                sectionTitleHolder.sectionTitleTextView.setText(mContext.getText(R.string.interviewing));

                break;

            case 1:
                // the Holder is a SectionPromptHolder to be filled with interviewing_prompt text
                // or it's a FilledIntervieweeHolder to be filled with Moment.interviewee info

                if(mMoment.getInterviewee() == null) {

                    // cast the holder to a SectionPromptHolder
                    sectionPromptHolder = (SectionPromptHolder) holder;

                    // set the text within it
                    sectionPromptHolder.sectionPromptTextView.setText(mContext.getText(R.string.interviewing_prompt));

                }


                break;

            case 2:
                // the Holder is a SectionTitleHolder to be filled with description text
                sectionTitleHolder = (SectionTitleHolder) holder;

                // fill it
                sectionTitleHolder.sectionTitleTextView.setText(mContext.getText(R.string.description));

                break;

            case 3:
                // the Holder is a SectionPromptHolder to be filled with description_prompt text
                // or it's a FilledDescriptionHolder to be filled with Moment.description info

                if(mMoment.getDescription() == null) {

                    // cast the holder to a SectionPromptHolder
                    sectionPromptHolder = (SectionPromptHolder) holder;

                    // set the text within it
                    sectionPromptHolder.sectionPromptTextView.setText(mContext.getText(R.string.description_prompt));

                }

                break;

            case 4:
                // the Holder is a SectionTitleHolder to be filled with video text
                sectionTitleHolder = (SectionTitleHolder) holder;

                // fill it
                sectionTitleHolder.sectionTitleTextView.setText(R.string.video);

                break;

            case 5:
                // the Holder is a VideoSectionPromptHolder.. already filled in xml file
                // or it's a FilledVideoHolder to be filled with Moment.localThumbnail Bitmap

                if(mMoment.getLocalThumbnail() == null) {

                    // cast the holder to a VideoSectionPromptHolder
                    VideoSectionPromptHolder videoSectionPromptHolder = (VideoSectionPromptHolder) holder;

                }

                break;

            case 6:
                // the Holder is a SectionTitleHolder to be filled with notes text
                sectionTitleHolder = (SectionTitleHolder) holder;

                // fill it
                sectionTitleHolder.sectionTitleTextView.setText(mContext.getText(R.string.notes));

                break;

            case 7:
                // the Holder is a SectionPromptHolder to be filled with notes text

                // cast it
                sectionPromptHolder = (SectionPromptHolder) holder;

                // fill it with notes String
                sectionPromptHolder.sectionPromptTextView.setText(mContext.getText(R.string.notes_prompt));

                break;

            default:
                // otherwise the Holder is a NoteCardHolder to be filled with notes from mIdentifierList
                // this is what the garbage String values are for... to access notes by given position

                NoteCardHolder noteCardHolder = (NoteCardHolder) holder;

                // fill the holder with the note from mIdentifierList
                noteCardHolder.noteCardTextView.setText(mIdentifierList.get(position));

                break;

        }

    }

    public void addSectionText() {
        /**
         * adds Strings to the first eight positions of the mIdentifierList
         * to make indexing easier in onBindViewHolder
         */

        for(int i = 0; i < 8; i++) {

            mIdentifierList.add(i, "not a note");

        }


    }

    public void updateDataSet() {
        // fills the mIdentifierList with the notes the Moment has. to be called before notifyDataSetChanged

        // clear out the list every time
        mIdentifierList.clear();

        // add the Strings for indexing
        addSectionText();

        // add all of the notes
        mIdentifierList.addAll(mMoment.getNotes());


    }



}
