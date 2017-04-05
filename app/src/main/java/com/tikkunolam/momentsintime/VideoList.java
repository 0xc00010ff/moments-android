package com.tikkunolam.momentsintime;


import java.util.ArrayList;

public class VideoList {

    /**
     * INSTANCE VARIABLES
     */

    private ArrayList<Video> videos;
    private VimeoNetworkingSingleton vimeoNetworkingSingleton;

    /**
     * CONSTRUCTORS
     */

    public VideoList() {

        videos = new ArrayList<>();
        vimeoNetworkingSingleton = VimeoNetworkingSingleton.getInstance();

    }

    /**
     * Instance Methods
     */

    public ArrayList<Video> getVideoList() {
        // just return the video list. useless for now as all methods will return the list.

        return videos;

    }


    public void getCommunityVideos() {
        // update the videos list by fetching the Community Videos list

        videos = vimeoNetworkingSingleton.getCommunityVideos();


    }


}
