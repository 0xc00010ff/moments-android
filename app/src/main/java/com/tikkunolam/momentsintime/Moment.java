package com.tikkunolam.momentsintime;

import android.net.Uri;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Moment extends RealmObject {
    /**
     * this class is a model for the main tool within the app
     */


    /**
     * INSTANCE VARIABLES
     */

    @PrimaryKey
    private String primaryKey;

    // the title of the Moment
    private String title;

    // the description of the Moment
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
    RealmList<Note> notes;


    // if the video is available (relevant after uploading)
    private boolean available;

    /**
     * CONSTRUCTORS
     */

    // empty constructor for Moment creation by user
    public Moment() {


    }


    /**
     * METHODS
     */


    // getters

    public String getPrimaryKey() {

        return primaryKey;

    }

    public String getTitle() {

        return title;

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

    public String getIntervieweePhotoUri() {

        return intervieweePhotoUri;

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

    public String getPictureUrl() {

        return pictureUrl;

    }

    public RealmList<Note> getNotes() {

        return notes;

    }

    public boolean isAvailable() {

        return available;

    }

    // setters

    public void setPrimaryKey(String primaryKey) {

        this.primaryKey = primaryKey;

    }

    public void setTitle(String title) {

        this.title = title;

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

    public void setAvailable(boolean available) {

        this.available = available;

    }

    public void setIntervieweePhotoUri(String intervieweePhotoUri) {

        this.intervieweePhotoUri = intervieweePhotoUri;

    }

    public void setLocalVideoUri(String localVideoUri) {

        this.localVideoUri = localVideoUri;

    }

    public void setNotes(RealmList<Note> notes) {

        this.notes = notes;

    }

}

