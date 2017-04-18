package com.tikkunolam.momentsintime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;

public class VideoViewActivity extends AppCompatActivity {

    // the moment that was passed in
    Moment moment;

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

        // take the Moment out of the extras bundle
        moment = getIntent().getExtras().getParcelable(videoExtra);

        // set up the Moment
        setUpVideoView(moment);

        // start the moment
        videoView.start();




    }

    private void setUpVideoView(Moment moment) {

        // get the moment path
        String url = moment.getVideoUrl();

        // set the VideoView's path
        videoView.setVideoPath(url);

        // set up the MediaController
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);

        videoView.setMediaController(mediaController);

    }

}
