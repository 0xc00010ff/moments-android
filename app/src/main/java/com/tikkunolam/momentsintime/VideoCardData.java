package com.tikkunolam.momentsintime;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoCardData {
    /**
     * this class holds the data that will be used to fill a VideoCardHolder
     * which will fill a video_card in the RecyclerView of MakeAMomentActivity
     */

    // the uri of the image on disk
    Uri mVideoUri;

    public VideoCardData(Uri videoUri) {

        mVideoUri = videoUri;

    }

    public Uri getVideoUri() {

        return mVideoUri;

    }

    public void setVideoUri(Uri videoUri) {

        mVideoUri = videoUri;

    }

}
