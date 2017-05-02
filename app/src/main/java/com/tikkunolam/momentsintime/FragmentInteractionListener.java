package com.tikkunolam.momentsintime;

public interface FragmentInteractionListener {
    /**
     * interface common to the Fragments to share information with the MainActivity
     */

    // when a Moment is selected
    void onMomentSelect(Moment moment);

    // when the FloatingActionButton on the MyMomentsFragment is clicked
    void onNewMomentClick();

    // when the moment_prompt is clicked
    void onMomentPromptClick();

}
