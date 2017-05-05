package com.tikkunolam.momentsintime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.width;
import static android.content.ContentValues.TAG;

public class Moment implements Parcelable {

    /**
     * INSTANCE VARIABLES
     */

    // the name of the mMoment
    private String name;

    // the description of the mMoment
    private String description;

    // the person being interviewed. likely will change to a Person object
    private String interviewee;

    // the role of the person being interviewed
    private String intervieweeRole;

    // the uri of the picture of the interviewee on phone
    private String intervieweePhotoUri;

    // the video uri for any further operation on the video
    private String videoUri;

    // the video playback url
    private String videoUrl;

    // the local video file uri
    private String localVideoUri;

    // the url of the video thumbnail
    private String pictureUrl;

    // list of notes on the Moment
    ArrayList<String> notes;


    // if the video is available (relevant after uploading)
    private boolean available;

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
        intervieweeRole = in.readString();
        intervieweePhotoUri = in.readString();
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
        out.writeString(intervieweeRole);
        out.writeString(intervieweePhotoUri);
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

    public String getIntervieweeRole() {

        return intervieweeRole;

    }

    public Uri getIntervieweePhotoUri() {

        if(intervieweePhotoUri != null) {

            return Uri.parse(intervieweePhotoUri);

        }

        else {

            return null;

        }

    }


    public String getVideoUri() {

        return videoUri;

    }

    public String getVideoUrl() {

        return videoUrl;

    }

    public Uri getLocalVideoUri() {

        if(localVideoUri != null) {

            return Uri.parse(localVideoUri);

        }

        else return null;

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

    public void setIntervieweeRole(String intervieweeRole) {

        this.intervieweeRole = intervieweeRole;

    }

    public void setIntervieweePhotoUri(Uri interVieweePhotoUri) {

        this.intervieweePhotoUri = interVieweePhotoUri.toString();

    }

    public void setVideoUri(String videoUri) {

        this.videoUri = videoUri;

    }

    public void setVideoUrl(String videoUrl) {

        this.videoUrl = videoUrl;

    }

    public void setLocalVideoUri(Uri localVideoUri) {

        // set the localVideoUri from the argument
        this.localVideoUri = localVideoUri.toString();

    }


    public void setPictureUrl(String pictureUrl) {

        this.pictureUrl = pictureUrl;

    }

    public void addNote(String note) {

        notes.add(note);

    }

}

