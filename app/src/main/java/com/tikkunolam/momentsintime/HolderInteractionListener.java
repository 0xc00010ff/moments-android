package com.tikkunolam.momentsintime;

public interface HolderInteractionListener {
    /**
     * interface common to the ViewHolders used in MakeAMomentActivity
     * to implement callbacks to the Activity
     */

    // callback for when interviewing_prompt is clicked
    void onInterviewingPromptClick();

    // callback for when topic_prompt is clicked
    void onTopicPromptClick();

    //callback for when video_prompt is clicked
    void onVideoPromptClick();

    // callback for when upload_prompt is clicked
    void onUploadPromptClick();

    // callback for when notes_prompt is clicked
    void onNotesPromptClick();

    // callback for when the video_card dots are clicked
    void onVideoDotsClick();

    // callback for when the video_card play button is clicked
    void onPlayButtonClick();

    // callback for when the note_card dots are clicked
    void onNoteCardDotsClick(int position);

}
