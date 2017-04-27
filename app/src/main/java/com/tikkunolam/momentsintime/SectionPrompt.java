package com.tikkunolam.momentsintime;

public class SectionPrompt {
    /**
     * this class represents one of the prompts appearing on the MakeAMomentActivity
     * just holds a String, representing the prompt of the section to be bound in the MakeAMomentAdapter
     */


    // the title of the section
    public String mSectionPrompt;


    /**
     * CONSTRUCTORS
     */


    public SectionPrompt(String sectionPrompt) {

        mSectionPrompt = sectionPrompt;

    }

    /**
     * INSTANCE METHODS
     */

    public String getSectionPrompt() {

        return mSectionPrompt;

    }






}
