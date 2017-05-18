package com.tikkunolam.momentsintime;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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

    // the state of the Moment
    private String state;

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

    // the local video file path
    private String localVideoFilePath;

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

    public String getState() {

        return state;

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

    public String getLocalVideoFilePath() {

        return localVideoFilePath;

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

    public void setState(String state) {

        this.state = state;

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

    public void setVideoUri(String videoUri) {

        this.videoUri = videoUri;

    }

    public void setVideoUrl(String videoUrl) {

        this.videoUrl = videoUrl;

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

    public void setLocalVideoFilePath(String localVideoFilePath) {

        this.localVideoFilePath = localVideoFilePath;

    }

    public void setNotes(RealmList<Note> notes) {

        this.notes = notes;

    }

    public void persistUpdates(final PersistenceExecutor executor) {
        // method for executing Realm assignments and queries

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                executor.execute();

            }
        });

    }

    public static Moment createMoment() {
        // returns a new managed Moment

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        // generate a random uuid for the primaryKey
        String primaryKey = UUID.randomUUID().toString();

        // make a new Moment with the primaryKey
        Moment moment = realm.createObject(Moment.class, primaryKey);

        realm.commitTransaction();

        return moment;

    }

    public static Moment findMoment(final String primaryKey) {
        // finds a moment from the primaryKey and returns it

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        RealmQuery<Moment> query = realm.where(Moment.class);

        // construct the query to find the Moment that matches primaryKey
        query.equalTo("primaryKey", primaryKey);

        // get the results
        RealmResults<Moment> results = query.findAll();

        // get the first Moment from results... will only be one
        Moment moment = results.first();

        realm.commitTransaction();

        return moment;

    }

    public static ArrayList<Moment> getMyMoments() {

        ArrayList<Moment> momentList = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Moment> realmResults = realm.where(Moment.class).findAll();

        momentList.addAll(realm.copyFromRealm(realmResults));

        return momentList;

    }

    public void uploadMoment(Context applicationContext) {
        // grab a VimeoNetworker to handle upload

        VimeoNetworker vimeoNetworker = new VimeoNetworker(applicationContext);

        vimeoNetworker.uploadMoment(localVideoFilePath, primaryKey, applicationContext);

    }

    public MomentStateEnum getMomentState() {
        // return the state of the Moment

        // if a state has been set, return the MomentStateEnum equivalent
        return (state != null) ? MomentStateEnum.valueOf(state) : null;

    }

    public void setEnumState(MomentStateEnum state) {
        // set the state of the Moment

        this.state = state.name();

    }

    public boolean endItAll(Context context) {
        // the Moment deletes itself from Realm and from Vimeo if it's live

        Realm realm = Realm.getDefaultInstance();

        boolean deleteFromRealm = true;


        // delete the Moment from Vimeo if it's live
        if (getMomentState() == MomentStateEnum.LIVE) {
            // delete from Vimeo
            VimeoNetworker vimeoNetworker = new VimeoNetworker(context);
            deleteFromRealm = vimeoNetworker.deleteMoment(this);

        }


        //delete the Moment from Realm

        if(deleteFromRealm) {

            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {

                    // Moment finds itself by primaryKey and deletes itself
                    RealmResults<Moment> realmResults = realm.where(Moment.class).equalTo("primaryKey", primaryKey).findAll();
                    realmResults.deleteAllFromRealm();


                }

            });

        }

        return deleteFromRealm;

    }

}