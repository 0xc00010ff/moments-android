package com.tikkunolam.momentsintime;

public class SearchByNameArgument {
    /**
     * A class for providing the argument from a MomentList to VimeoNetworker's searchVimeo method
     */

    // the page number to pass to VimeoNetworker's searchVimeo method
    private int mPageNumber;

    // the search string to pass to VimeoNetworker's searchVimeo method
    private String mSearchString;

    public SearchByNameArgument(int pageNumber, String searchString) {

        mPageNumber = pageNumber;

        mSearchString = searchString;

    }

    public int getPageNumber() {

        return mPageNumber;

    }

    public String getSearchString() {

        return mSearchString;

    }

    public void setPageNumber(int pageNumber) {

        mPageNumber = pageNumber;

    }

    public void setSearchString(String searchString) {

        mSearchString = searchString;

    }

}
