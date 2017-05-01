package com.tikkunolam.momentsintime;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoCardData {
    /**
     * this class holds the data that will be used to fill a VideoCardHolder
     * which will fill a video_card in the RecyclerView of MakeAMomentActivity
     */

    // the uri of the image on disk
    Bitmap mVideoPreviewBitmap;

    public VideoCardData(Bitmap videoPreviewBitmap) {

        mVideoPreviewBitmap = videoPreviewBitmap;

    }

    public Bitmap getVideoPreviewBitmap() {

        return mVideoPreviewBitmap;

    }

    public void setVideoPreviewBitmap(Bitmap videoPreviewBitmap) {

        mVideoPreviewBitmap = videoPreviewBitmap;

    }

}
