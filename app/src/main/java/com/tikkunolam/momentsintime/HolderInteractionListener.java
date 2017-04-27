package com.tikkunolam.momentsintime;

public interface HolderInteractionListener {
    /**
     * interface common to the ViewHolders used in MakeAMomentActivity
     * to implement callbacks to the Activity
     */

    // callback for when interviewing_prompt is clicked
    public void onInterviewingPromptClick();

    // callback for when description_prompt is clicked
    public void onDescriptionPromptClick();

    //callback for when video_prompt is clicked
    public void onVideoPromptClick();

    // callback for when upload_prompt is clicked
    public void onUploadPromptClick();

    // callback for when notes_prompt is clicked
    public void onNotesPromptClick();

}
