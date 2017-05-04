package com.tikkunolam.momentsintime;

public class DescriptionCardData {
    /**
     * this class holds the Moment fields related to the description
     * for insertion into a DescriptionCardHolder which fills a description_card
     * expanded in MakeAMomentActivity's RecyclerView
     */

    String mTitle;

    String mDescription;

    public DescriptionCardData(String title, String description) {

        mTitle = title;

        mDescription = description;

    }

    public String getTitle() {

        return mTitle;

    }

    public void setTitle(String title) {

        mTitle = title;

    }

    public String getDescription() {

        return mDescription;

    }

    public void setDescription(String description) {

        mDescription = description;

    }

}
