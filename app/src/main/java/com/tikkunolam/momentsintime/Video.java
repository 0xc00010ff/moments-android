package com.tikkunolam.momentsintime;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    /**
     * INSTANCE VARIABLES
     */

    // the name of the video
    public String name;

    // the description of the video
    public String description;

    // the uri for any further operation on the video
    public String uri;

    // the playback url
    public String url;

    // the url of the video thumbnail
    public String pictureUrl;

    // the width of the video
    public int width;

    // the height of the video
    public int height;

    // if the video is available (relevant after uploading)
    public boolean available;

    // the Creator field. more Parcelable functionality. used for actually creating the Video
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

        @Override
        public Video createFromParcel(Parcel in) {

            return new Video(in);

        }

        @Override
        public Video[] newArray(int size) {

            return new Video[size];

        }
    };

    /**
     * CONSTRUCTORS
     */

    // the normal constructor
    public Video(String name, String description, String uri, String url, String pictureUrl, int width, int height) {

        this.name = name;
        this.description = description;
        this.uri = uri;
        this.url = url;
        this.pictureUrl = pictureUrl;
        this.width = width;
        this.height = height;

    }

    // the constructor for creation from parcel
    private Video(Parcel in) {
        /**
         * this is the constructor that Parcelable calls to create a Video from a Parcel
         * the order in which the variables are assigned is important
         * the order must correspond to the order set in writeToParcel()
         */

        name = in.readString();
        description = in.readString();
        uri = in.readString();
        url = in.readString();
        pictureUrl = in.readString();
        width = in.readInt();
        height = in.readInt();

    }

    /**
     * METHODS
     */

    // for writing object to parcel
    public void writeToParcel(Parcel out, int flags) {
        /**
         * writes the object to a parcel
         * the order in which the variables are written is important
         * the order must be observed in the way you set variables in the private constructor ^^^
         */

        out.writeString(name);
        out.writeString(description);
        out.writeString(uri);
        out.writeString(url);
        out.writeString(pictureUrl);
        out.writeInt(width);
        out.writeInt(height);

    }

    // some more Parcelable functionality. this method is unimportant in this context... but necessary
    @Override
    public int describeContents() {
        return 0;
    }


    public String getName() {

        return name;

    }

    public String getDescription() {

        return description;

    }


    public String getUri() {

        return uri;

    }

    public String getUrl() {

        return url;

    }

    public String getPictureUrl() {

        return pictureUrl;

    }

    public int getWidth() {

        return width;

    }

    public int getHeight() {

        return height;

    }


}

