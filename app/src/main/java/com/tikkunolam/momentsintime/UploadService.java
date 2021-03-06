package com.tikkunolam.momentsintime;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static com.tikkunolam.momentsintime.MomentStateEnum.FAILED;
import static com.tikkunolam.momentsintime.MomentStateEnum.LIVE;
import static com.tikkunolam.momentsintime.MomentStateEnum.UPLOADING;


public class UploadService extends IntentService {
    // this service runs in the background and uploads a mMoment to Vimeo

    /**
     * INSTANCE VARIABLES
     */

    // tag for logging purposes
    private final String TAG = "UploadService";

    private final int RESPONSE_OKAY = 200;

    // string for intent extra arguments/parameters
    String mVideoFileExtra, mPrimaryKeyExtra;

    // strings for json filter argument in requests
    String mUploadTicketFilter, mUploadFilter, mCompleteUploadFilter, mUpdateMetadataFilter, mCheckAvailabilityFilter;

    // strings for SharedPreferences
    String hasFailedFlagName, mDisplayUploadMessageFlagName, mPrimaryKeyFlagName;

    // the video file to upload
    String mVideoFileString;

    // the primaryKey of the Moment from which we're uploading a video
    String mPrimaryKeyString;

    // app access token for authenticating requests
    private String mAccessToken;

    // base api address
    private String mApiAddress;

    // upload uri
    private String mVideoFetchUri;

    // add metadata uri
    private String mVideoUri;

    // switch album address
    String mVideoToAlbumAddress;

    // String to match the status of the video returned by Vimeo if it's available
    private String mVideoAvailable;

    // to be included in headers to let Vimeo know what version of the API we expect
    private String mApiVersion;

    // API endpoint for the upload ticket. query to learn more about upload
    String mUploadTicketUri;

    // Uri to make the delete call to
    String mCompleteUri;

    // secure upload URL. url to which the mMoment data is sent
    String mUploadLink;

    // the final location of the uploaded video
    String mFinalUri;

    // the Moment we're working with
    Moment mMoment;

    // to keep track of if the video has finished uploading, for handling business in onDestroy
    boolean uploaded;


    /**
     * CONSTRUCTOR
     */

    public UploadService() {

        super("UploadService");

    }

    /**
     * METHODS
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        // do the upload work

        // the video has not finished uploading yet
        uploaded = false;

        // strings for intent extra arguments/parameters
        mVideoFileExtra = getString(R.string.video_file_extra);
        mPrimaryKeyExtra = getString(R.string.primary_key_extra);

        // strings for JSON filter arguments
        mUploadTicketFilter = getString(R.string.upload_ticket_filter);
        mUploadFilter = getString(R.string.upload_filter);
        mCompleteUploadFilter = getString(R.string.complete_upload_filter);
        mUpdateMetadataFilter = getString(R.string.update_metadata_filter);
        mCheckAvailabilityFilter = getString(R.string.check_availability_filter);

        // strings for SharedPreferences
        hasFailedFlagName = getString(R.string.has_failed_flag);
        mDisplayUploadMessageFlagName = getString(R.string.display_upload_message);
        mPrimaryKeyFlagName = getString(R.string.moment_primary_key);

        // app access token for authenticating requests
        mAccessToken = getString(R.string.api_access_token);

        // base api address
        mApiAddress = getString(R.string.api_base_address);

        // upload uri
        mVideoFetchUri = getString(R.string.video_fetch_uri);

        // video to album address
        mVideoToAlbumAddress = getString(R.string.video_to_album_address);

        // update metadata uri
        mVideoUri = getString(R.string.single_video_fetch_uri);

        // to be included in headers to let Vimeo know what version of the API we expect
        mApiVersion = getString(R.string.api_version);

        // matches the available status returned by Vimeo
        mVideoAvailable = getString(R.string.video_availability);

        // the path for the local mMoment file
        mVideoFileString = intent.getStringExtra(mVideoFileExtra);

        // the primaryKey
        mPrimaryKeyString = intent.getStringExtra(mPrimaryKeyExtra);

        mMoment = Moment.findMoment(mPrimaryKeyString);

        boolean success;

        // indicate to any Activities that may be looking, the upload has not finished
        indicateUploadNotFinished();

        // inform Vimeo we wish to upload a video, and receive a url to do so
        success = generateUploadTicket();

        if(success) {

            // upload the video
            success = uploadVideo();

            if(success) {
                // if the upload went through, complete the upload and receive the final location of the video in mFinalUri

                success = completeUpload();

                if(success) {

                    // tell the world that the upload has finished
                    indicateUploadFinished();

                    // update the Moment's state to LIVE

                    // make a LIVE MomentStateEnum
                    final MomentStateEnum momentStateEnum = LIVE;

                    // find the Moment by primaryKey
                    final Moment moment = Moment.findMoment(mPrimaryKeyString);

                    // persist the state and videoUri
                    moment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            moment.setEnumState(momentStateEnum);

                        }

                    });

                    // if the upload completion went through and we received the final Uri...
                    // ... update the video's title and description on Vimeo

                    success = updateMetadata();


                }

            }

        }

        else {
            // one of the tasks failed... so the entire upload failed

            success = false;

        }


        // if the upload was successful
        if(success) {
            // update the video's uri, then wait to notify the fragment until vimeo return an available status

            // find the Moment by primaryKey
            final Moment moment = Moment.findMoment(mPrimaryKeyString);

            // persist the videoUri
            moment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    moment.setVideoUri(mFinalUri);

                }

            });

            // move the video to the pending album
            moveToAlbum();

            // wait until the video is available to tell the fragment to update its view
            waitForAvailability();

        }

        else {
            // update the Moment's state enum to FAILED

            final MomentStateEnum momentStateEnum = FAILED;

            // find the Moment by primaryKey
            final Moment moment = Moment.findMoment(mPrimaryKeyString);

            // persist the Moment state
            moment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    moment.setEnumState(momentStateEnum);

                }

            });

        }

        // set the UploadFinishedMessage's success value
        UploadFinishedMessage uploadFinishedMessage = new UploadFinishedMessage();
        uploadFinishedMessage.setIsSuccess(success);

        // post the message
        EventBus.getDefault().post(uploadFinishedMessage);


        // service will now kill itself

    }

    protected boolean generateUploadTicket() {
        //generate an upload ticket
        // response contains information on how and where to upload

        OkHttpClient client = new OkHttpClient();

        Response response = null;

        // success of the operation. set to true if it goes off without a hitch
        boolean success = false;

        // try to request an upload ticket
        try {

            // build the body of the POST request
            RequestBody body = new FormBody.Builder()
                    .add("type", "streaming")
                    .build();

            // build the request
            Request request = new Request.Builder()
                    .url(mApiAddress + mVideoFetchUri + "?" + mUploadTicketFilter)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

            // execute the call and receive a response
            response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                ResponseBody responseBody = response.body();

                // transform the response to string for logging and JSON purposes
                String responseString = responseBody.string();

                // create a JSONObject and extract the useful fields
                JSONObject jsonObject = new JSONObject(responseString);
                mUploadTicketUri = jsonObject.getString("uri");
                mCompleteUri = jsonObject.getString("complete_uri");
                mUploadLink = jsonObject.getString("upload_link_secure");

                // operation was successful
                success = true;

            }

        }

        catch(IOException exception) {

        }

        catch(JSONException exception) {

        }

        finally {

            // close the connection
            response.body().close();

            // return whether it was successful
            return success;

        }

    }

    protected boolean uploadVideo() {
        //upload the mMoment

        boolean success = false;

        // create a file from the videoFileString
        File file = new File(mVideoFileString);

        Response response = null;

        // try to make the request
        try{

            // make the upload request
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(mUploadLink)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .put(RequestBody.create(MediaType.parse("mp4"), file))
                    .build();

            // get the response
            response = client.newCall(request).execute();

            // determine if the upload was successful and return that information to the service
            int resultCode = response.code();

            if(resultCode == RESPONSE_OKAY) {

                success = true;

            }

        }

        catch(IOException exception) {

        }

        finally {

            response.body().close();

            return success;

        }


    }

    protected boolean completeUpload() {
        // complete the upload. delete the upload ticket

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        boolean success = false;

        try {
            // try to make a DELETE request to get the mFinalUri for the Moment

            Request request = new Request.Builder()
                    .url(mApiAddress + mCompleteUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .delete()
                    .build();

            // field the response
            response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                // retrieve the final location of the video on Vimeo to return to the Moment
                mFinalUri = response.header("Location");

                // it worked
                success = true;

                // signify that the upload has completed
                uploaded = true;

            }

        }

        catch(IOException exception) {

        }

        finally {

            response.body().close();
            return success;

        }

    }

    protected boolean updateMetadata() {
        // add the title and description to the video on Vimeo

        OkHttpClient client = new OkHttpClient();
        Response response;
        boolean success = false;

        // try to update the metadata. return whether operation was successful
        try {

            // build the body for the PATCH request
            RequestBody patchBody = new FormBody.Builder()
                    .add("name", mMoment.getCanonicalTitle())
                    .add("description", mMoment.getDescription())
                    .build();

            // build the request with patchBody
            Request request = new Request.Builder()
                    .url(mApiAddress + mFinalUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .patch(patchBody)
                    .build();

            // execute the request
            response = client.newCall(request).execute();

            // get the response code to determine if the request went through properly
            int responseCode = response.code();

            if(responseCode == RESPONSE_OKAY) {

                success = true;

            }

        }

        catch(IOException exception) {

        }

        // return whether or not the request was successful
        return success;

    }

    public void waitForAvailability() {
        /**
         * after a Moment has been uploaded to Vimeo, it may not actually be available for playback
         * it will not return a thumbnail, title, description, or be able to play
         * so this method just requests the video once a minute, checks if it's available, and when it is... exits
         */

        boolean available = false;
        OkHttpClient client = new OkHttpClient();
        Response response = null;

        try {

            while(!available) {
                // while the video isn't available, ask for it every minute

                // wait a minute
                Thread.sleep(60000);

                // request the video
                Request request = new Request.Builder()
                        .url(mApiAddress + mFinalUri + "?" + mCheckAvailabilityFilter)
                        .addHeader("Authorization", "Bearer " + mAccessToken)
                        .addHeader("Accept", mApiVersion)
                        .build();

                // receive the response
                response = client.newCall(request).execute();

                if(response.isSuccessful()) {

                    // convert it to a String
                    String responseString = response.body().string();

                    // convert it to JSON
                    JSONObject jsonObject = new JSONObject(responseString);

                    // grab the status of the video
                    String availability = jsonObject.getString("status");

                    // if the status == "available" set available to true ---> exit the loop and method
                    if(availability.equals(mVideoAvailable)) {

                        available = true;

                    }

                }

            }

        }

        catch(InterruptedException exception) {
            // the thread was interrupted. not a big deal.
            // this method will just end and the Moment will be updated to Live

        }

        catch(IOException exception) {

        }

        catch(JSONException exception) {

        }

        finally {

            response.body().close();

        }


    }

    private void moveToAlbum() {
        // to be called once the video has finished uploading
        // moves it to the upload album to wait for admin's approval

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        try {

            // OkHttp requires a request body for PUTs. We don't though, so make an empty one.
            RequestBody body = RequestBody.create(null, new byte[]{});

            // build the request
            Request request = new Request.Builder()
                    .url(mApiAddress + mVideoToAlbumAddress + mFinalUri)
                    .put(body)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

            // make the call
            response = client.newCall(request).execute();

        }

        catch(IOException exception) {

        }

        finally {

            response.body().close();

        }

    }

    private void indicateUploadNotFinished() {
        // set a sharedPreferences value indicating upload hasn't finished
        // happens before upload incase the app is killed, so the Moment can be updated to FAILED

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // indicate that upload has not finished
        editor.putBoolean(hasFailedFlagName, true);
        editor.commit();

    }

    // set the sharedPreference back, indicating the upload finished and the state is correct
    private void indicateUploadFinished() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // indicate that upload has finished
        editor.putBoolean(hasFailedFlagName, false);
        editor.putBoolean(mDisplayUploadMessageFlagName, true);
        editor.putString(mPrimaryKeyFlagName, mPrimaryKeyString);
        editor.commit();

    }

}
