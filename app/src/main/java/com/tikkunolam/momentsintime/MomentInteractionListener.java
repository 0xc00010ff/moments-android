package com.tikkunolam.momentsintime;

public interface MomentInteractionListener {
    /**
     * interface for the Moments to notify an Activity of clicks within it
     */

    // when a play button is clicked in a MomentCard
    void onVideoSelect(Moment moment);

    // when an entire Moment is clicked from MyMoments
    void onMyMomentCardClick(Moment moment);

    // when the share TextView is clicked in a MyMoments MomentCard
    void onMyShareClick(Moment moment);

    // when the share TextView is clicked in a Community MomentCard
    void onCommunityShareClick(Moment moment);

    // when the dots are clicked in a MyMoments MomentCard
    void onMyDotsClick(Moment moment);

    // when the dots are clicked in a Community MomentsCard
    void onCommunityDotsClick(Moment moment);

    // when the moment_prompt is clicked
    void onMomentPromptClick();

    // when the welcome message is dismissed
    void onWelcomeDismiss();

    // when the state_constraintLayout is clicked on a FAILED upload Moment
    void onFailedStateClick(Moment moment);
}
