package com.tikkunolam.momentsintime;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;

import static android.R.attr.width;

public class Moment implements Parcelable {

    /**
     * INSTANCE VARIABLES
     */

    // the name of the mMoment
    public String name;

    // the description of the mMoment
    public String description;

    // the person being interviewed. likely will change to a Person object
    public String interviewee;

    // the video uri for any further operation on the video
    public String videoUri;

    // the video playback url
    public String videoUrl;

    // the local video file uri
    public String localVideoUri;

    // the local video thumbnail
    public Bitmap localThumbnail;

    // the url of the video thumbnail
    public String pictureUrl;

    // list of notes on the Moment
    ArrayList<String> notes;


    // if the video is available (relevant after uploading)
    public boolean available;

    // the Creator field. more Parcelable functionality. used for actually creating the Moment
    public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>() {

        @Override
        public Moment createFromParcel(Parcel in) {

            return new Moment(in);

        }

        @Override
        public Moment[] newArray(int size) {

            return new Moment[size];

        }
    };

    /**
     * CONSTRUCTORS
     */

    // empty constructor for Moment creation by user
    public Moment() {

        notes = new ArrayList<>();

    }

    // the constructor for generating Moments from Vimeo results
    public Moment(String name, String description, String uri, String url, String pictureUrl) {

        this.name = name;
        this.description = description;
        this.videoUri = uri;
        this.videoUrl = url;
        this.pictureUrl = pictureUrl;

    }

    // the constructor for creation from parcel
    private Moment(Parcel in) {
        /**
         * this is the constructor that Parcelable calls to create a Moment from a Parcel
         * the order in which the variables are assigned is important
         * the order must correspond to the order set in writeToParcel()
         */

        name = in.readString();
        description = in.readString();
        interviewee = in.readString();
        videoUri = in.readString();
        videoUrl = in.readString();
        localVideoUri = in.readString();
        pictureUrl = in.readString();
        notes = in.readArrayList(String.class.getClassLoader());

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
        out.writeString(interviewee);
        out.writeString(videoUri);
        out.writeString(videoUrl);
        out.writeString(localVideoUri);
        out.writeString(pictureUrl);
        out.writeList(notes);

    }

    // some more Parcelable functionality. this method is unimportant in this context... but necessary
    @Override
    public int describeContents() {
        return 0;
    }


    // getters

    public String getName() {

        return name;

    }

    public String getDescription() {

        return description;

    }

    public String getInterviewee() {

        return interviewee;

    }


    public String getVideoUri() {

        return videoUri;

    }

    public String getVideoUrl() {

        return videoUrl;

    }

    public String getLocalVideoUri() {

        return localVideoUri;

    }

    public Bitmap getLocalThumbnail() {

        return localThumbnail;

    }

    public String getPictureUrl() {

        return pictureUrl;

    }

    public ArrayList<String> getNotes() {

        return notes;

    }

    // setters

    public void setName(String name) {

        this.name = name;

    }

    public void setDescription(String description) {

        this.description = description;

    }

    public void setInterviewee(String interviewee) {

        this.interviewee = interviewee;

    }

    public void setVideoUri(String videoUri) {

        this.videoUri = videoUri;

    }

    public void setVideoUrl(String videoUrl) {

        this.videoUrl = videoUrl;

    }

    public void setLocalVideoUri(String localVideoUri) {

        // set the localVideoUri from the argument
        this.localVideoUri = localVideoUri;

        // call the private getLocalThumbnail method
        setLocalThumbnail();

    }

    private void setLocalThumbnail() {

        localThumbnail = ThumbnailUtils.createVideoThumbnail(localVideoUri, MediaStore.Images.Thumbnails.MINI_KIND);

    }

    public void setPictureUrl(String pictureUrl) {

        this.pictureUrl = pictureUrl;

    }

    public void addNote(String note) {

        notes.add(note);

    }


}

