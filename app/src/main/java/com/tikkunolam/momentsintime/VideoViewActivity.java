package com.tikkunolam.momentsintime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;

public class VideoViewActivity extends AppCompatActivity {

    // the mMoment that was passed in
    Moment mMoment;

    // strings for intent extra arguments
    String mPrimaryKeyExtra;

    // ui references
    VideoView mVideoView;

    MediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_view);

        // fetch the string for Intent Extra argument from resources
        mPrimaryKeyExtra = (String) getResources().getText(R.string.primary_key_extra);

        // get the VideoView
        mVideoView = (VideoView) findViewById(R.id.videoView);

        // get the primaryKey by which the mMoment will be identified in Realm
        String primaryKey = getIntent().getStringExtra(mPrimaryKeyExtra);

        // fetch the Moment from Realm

        // set up the video
        setUpVideoView();

        // start the video
        mVideoView.start();




    }

    private void setUpVideoView( ) {

        if(mMoment.getLocalVideoUri() != null) {
            // there's a local video so set up the MediaController with that

            // set the video path
            mVideoView.setVideoURI(Uri.parse(mMoment.getLocalVideoUri()));

            // set up the MediaController
            mMediaController = new MediaController(this);
            mMediaController.setMediaPlayer(mVideoView);

            // set the MediaController on the VideoView
            mVideoView.setMediaController(mMediaController);

        }

        else {
            // there is a link to a Vimeo video

            // get the Moment path
            String uri = mMoment.getVideoUri();

            // call the async task to set the VideoView's path and set the MediaController
            AsyncFetchVideo asyncFetchVideo = new AsyncFetchVideo(this);
            asyncFetchVideo.execute(uri);

        }

    }

    public class AsyncFetchVideo extends AsyncTask<String, Void, String> {
        /**
         * class for asynchronously fetching a link to an mp4 from Vimeo
         */

        Context mContext;

        /**
         * CONSTRUCTOR
         */

        public AsyncFetchVideo(Context context) {

            mContext = context;

        }

        protected String doInBackground(String... videoUri) {

            // grab a networker
            VimeoNetworker vimeoNetworker = new VimeoNetworker(getApplicationContext());

            // have it fetch the video link
            String videoUrl = vimeoNetworker.getPlayableVideo(videoUri[0]);

            return videoUrl;

        }

        protected void onPostExecute(String videoUrl) {

            // set the video path
            mVideoView.setVideoPath(videoUrl);

            // set up the MediaController
            mMediaController = new MediaController(mContext);
            mMediaController.setMediaPlayer(mVideoView);

            // set the MediaController on the VideoView
            mVideoView.setMediaController(mMediaController);

        }



    }

}
