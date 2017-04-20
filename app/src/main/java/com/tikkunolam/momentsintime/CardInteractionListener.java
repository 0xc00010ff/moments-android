package com.tikkunolam.momentsintime;

public interface CardInteractionListener {
    /**
     * interface common to the Fragments to share information with the Activity
     */

    // when a Moment is selected
    void onMomentSelect(Moment moment);

}
