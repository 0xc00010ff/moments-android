package com.tikkunolam.momentsintime;

public class TopicCardData {

    String mTitle;
    String mDescription;

    public TopicCardData(String title, String description) {

        mTitle = title;
        mDescription = description;

    }

    public void setTitle(String titleText) {

        mTitle = titleText;

    }

    public void setDescription(String descriptionText) {

        mDescription = descriptionText;

    }

    public String getTitle() {

        return mTitle;

    }

    public String getDescription() {

        return mDescription;

    }

}
