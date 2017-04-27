package com.tikkunolam.momentsintime;

public class SectionTitle {
    /**
     * this class represents one of the titles appearing on the MakeAMomentActivity
     * just holds a String, representing the title of the section to be bound in the MakeAMomentAdapter
     */


    // the title of the section
    public String mSectionTitle;


    /**
     * CONSTRUCTORS
     */


    public SectionTitle(String sectionTitle) {

        mSectionTitle = sectionTitle;

    }

    /**
     * INSTANCE METHODS
     */

    public String getSectionTitle() {

        return mSectionTitle;

    }



}
