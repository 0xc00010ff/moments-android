package com.tikkunolam.momentsintime;

import android.content.Context;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.tikkunolam.momentsintime.R.string.video;

public class MakeAMomentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    /**
     * this adapter controls the display of every single view in the MakeAMomentActivity
     * the whole Activity is just a RecyclerView
     */

    Context mContext;

    // list of all things to be bound to the RecyclerView cells
    ArrayList<Object> mViewModelList;

    // integer identifiers for the view types
    final int SECTION_TITLE = 0, SECTION_PROMPT = 1, NOTE = 2, INTERVIEWING = 3, TOPIC = 4, VIDEO = 5;

    // a boolean indicating if the Holders should be clickable
    boolean mClickable = true;


    /**
     * CONSTRUCTORS
     */
    public MakeAMomentAdapter(Context context, ArrayList<Object> viewModels, boolean clickable) {

        mContext = context;

        mViewModelList = viewModels;

        mClickable = clickable;

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

        else if(mViewModelList.get(position) instanceof InterviewingCardData) {

            return INTERVIEWING;

        }

        else if(mViewModelList.get(position) instanceof TopicCardData) {

            return TOPIC;

        }

        else if(mViewModelList.get(position) instanceof VideoCardData) {

            return VIDEO;

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
                viewHolder = new SectionPromptHolder(mContext, sectionPromptView, mClickable);

                break;

            case NOTE:

                // inflate a note_card layout
                View noteCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.note_card, parent, false);

                // fill the ViewHolder with a NoteCardHolder
                viewHolder = new NoteCardHolder(mContext, noteCardView, mClickable);

                break;

            case INTERVIEWING:

                // inflate a interviewing_card layout
                View interviewingCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.interviewing_card, parent, false);

                // fill the ViewHolder with a InterviewingCardHolder
                viewHolder = new InterviewingCardHolder(mContext, interviewingCardView, mClickable);

                break;

            case TOPIC:

                // inflate a topic_card layout
                View descriptionCardView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.topic_card, parent, false);

                // fill the ViewHolder with a DescriptionCardHolder
                viewHolder = new TopicCardHolder(mContext, descriptionCardView, mClickable);

                break;

            case VIDEO:

                // inflate a video_card layout
                View videoCardLayout = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.video_card, parent, false);

                // fill the ViewHolder with a VideoCardHolder
                viewHolder = new VideoCardHolder(mContext, videoCardLayout, mClickable);

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

                // set the SectionPromptHolder's mSectionPromptTextView value
                sectionPromptHolder.setText(prompt);

                break;

            case NOTE:
                // fill a NoteCardHolder

                // cast the generic Holder to a NoteCardHolder
                NoteCardHolder noteCardHolder = (NoteCardHolder) holder;

                // cast the generic Object at the position in mViewModelList to a String
                String noteString = (String) mViewModelList.get(position);

                // set the NoteCardHolder's mNoteCardTextView value
                noteCardHolder.mNoteCardTextView.setText(noteString);

                // set the NoteCardHolder's position so it knows what to tell the MakeAMomentActivity
                noteCardHolder.setPosition(position);

                break;

            case INTERVIEWING:
                // fill an InterviewingCardHolder

                // cast the generic Holder to an InterviewingCardHolder
                InterviewingCardHolder interviewingCardHolder = (InterviewingCardHolder) holder;

                // cast the generic Object at mViewModelList(position) to an InterviewingCardData
                InterviewingCardData interviewingCardData = (InterviewingCardData) mViewModelList.get(position);

                // fill the InterviewingCardHolder's views
                interviewingCardHolder.mIntervieweeNameTextView.setText(interviewingCardData.getIntervieweeName());

                // if the user entered a role, then fill the TextView with it
                if(interviewingCardData.getIntervieweeRole() != null) {

                    // add the role
                    interviewingCardHolder.addRole();

                    // fill it
                    interviewingCardHolder.mIntervieweeRoleTextView.setText(interviewingCardData.getIntervieweeRole());

                }

                else {
                    // otherwise hide the TextView
                    interviewingCardHolder.mIntervieweeRoleTextView.setVisibility(View.GONE);

                    // and constrain the bottom of the interviewee TextView to its parent
                    interviewingCardHolder.removeRole();

                }

                // if the user added a picture, then fill the ImageView with it
                if(interviewingCardData.getIntervieweePhotoUri() != null) {

                    interviewingCardHolder.mIntervieweePhotoImageView.setVisibility(View.VISIBLE);

                    // use picasso to fill the ImageView and create a circle mask
                    Picasso.with(mContext).load(interviewingCardData.getIntervieweePhotoUri())
                            .transform(new CropCircleTransformation()).into(interviewingCardHolder.mIntervieweePhotoImageView);

                }

                // otherwise hide the ImageView
                else {

                    interviewingCardHolder.mIntervieweePhotoImageView.setVisibility(View.GONE);

                }

                break;

            case TOPIC:
                // fill a DescriptionCardHolder

                // cast the generic Holder to a DescriptionCardHolder
                TopicCardHolder topicCardHolder = (TopicCardHolder) holder;

                // cast the generic Object at mViewModelList(position) to a DescriptionCardData
                TopicCardData topicCardData = (TopicCardData) mViewModelList.get(position);

                // fill the DescriptionCardHolder's views
                topicCardHolder.mTopicCardTitleTextView.setText(topicCardData.getTitle());

                topicCardHolder.mTopicCardDescriptionTextView.setText(topicCardData.getDescription());

                break;

            case VIDEO:
                // fill a VideoCardHolder

                // cast the generic Holder to a VideoCardHolder
                VideoCardHolder videoCardHolder = (VideoCardHolder) holder;

                // cast the generic Object at mViewModelList(position) to a VideoCardData
                VideoCardData videoCardData = (VideoCardData) mViewModelList.get(position);

                // fill the VideoCardHolder's ImageView
                Glide.with(mContext).load(videoCardData.getVideoUri()).asBitmap().into(videoCardHolder.videoPreviewImageView);


            default:
                // received a null Holder
                // nothing to do... do nothing

                break;


        }

    }

}
