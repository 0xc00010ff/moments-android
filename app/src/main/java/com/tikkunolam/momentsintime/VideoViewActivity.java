package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;

public class VideoViewActivity extends AppCompatActivity {

    // the video that was passed in
    Video video;

    // strings for intent extra arguments
    String videoExtra = (String) getResources().getText(R.string.video_extra);

    // ui references
    VideoView videoView;

    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_view);

        // get the VideoView
        videoView = (VideoView) findViewById(R.id.videoView);

        // take the Video out of the extras bundle
        video = getIntent().getExtras().getParcelable(videoExtra);

        // set up the Video
        setUpVideoView(video);

        // start the video
        videoView.start();




    }

    private void setUpVideoView(Video video) {

        // get the video path
        String url = video.getUrl();

        // set the VideoView's path
        videoView.setVideoPath(url);

        // set up the MediaController
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);

        videoView.setMediaController(mediaController);

    }

}
