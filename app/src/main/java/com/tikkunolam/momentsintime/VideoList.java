package com.tikkunolam.momentsintime;


import android.content.Context;

import java.util.ArrayList;

public class VideoList {

    /**
     * INSTANCE VARIABLES
     */

    private ArrayList<Video> videos;
    private VimeoNetworker vimeoNetworker;

    /**
     * CONSTRUCTORS
     */

    public VideoList(Context applicationContext) {
        // takes a context argument only to pass to networker so it can access resources

        videos = new ArrayList<>();
        vimeoNetworker = new VimeoNetworker(applicationContext);
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

        videos = vimeoNetworker.getCommunityVideos();


    }


}
