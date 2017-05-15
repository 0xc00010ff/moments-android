package com.tikkunolam.momentsintime;

import android.app.IntentService;
import android.content.Intent;
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
        // probably send incremental progress update for a progress bar

        // strings for intent extra arguments/parameters
        mVideoFileExtra = getString(R.string.video_file_extra);
        mPrimaryKeyExtra = getString(R.string.primary_key_extra);

        // app access token for authenticating requests
        mAccessToken = getString(R.string.api_access_token);

        // base api address
        mApiAddress = getString(R.string.api_base_address);

        // upload uri
        mVideoFetchUri = getString(R.string.video_fetch_uri);

        // update metadata uri
        mVideoUri = getString(R.string.single_video_fetch_uri);

        // to be included in headers to let Vimeo know what version of the API we expect
        mApiVersion = getString(R.string.api_version);

        // the path for the local mMoment file
        mVideoFileString = intent.getStringExtra(mVideoFileExtra);

        // the primaryKey
        mPrimaryKeyString = intent.getStringExtra(mPrimaryKeyExtra);

        mMoment = Moment.findMoment(mPrimaryKeyString);

        boolean success;

        // inform Vimeo we wish to upload a video, and receive a url to do so
        success = generateUploadTicket();

        if(success) {

            // upload the video
            success = uploadVideo();

            if(success) {
                // if the upload went through, complete the upload and receive the final location of the video in mFinalUri

                success = completeUpload();

                if(success) {
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
            // update the Moment's state enum to LIVE, and set its videoUri

            // make a LIVE MomentStateEnum
            final MomentStateEnum momentStateEnum = LIVE;

            // find the Moment by primaryKey
            final Moment moment = Moment.findMoment(mPrimaryKeyString);

            // persist the state and videoUri
            moment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    moment.setEnumState(momentStateEnum);
                    moment.setVideoUri(mFinalUri);

                }

            });

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
                    .url(mApiAddress + mVideoFetchUri)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

            // execute the call and receive a response
            response = client.newCall(request).execute();
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

        catch(IOException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

        }

        catch(JSONException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

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

        // make a byte array into which the file's data will be streamed
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        Response response = null;

        // try to convert the file to a byte array and make the request
        try{

            // read the file into the byte array
            bytes = FileDealer.fullyReadFileToBytes(file);

            // make the upload request
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("mp4"), bytes);
            Request request = new Request.Builder()
                    .url(mUploadLink)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .put(requestBody)
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

            Log.e(TAG, exception.toString());

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


            // retrieve the final location of the video on Vimeo to return to the Moment
            mFinalUri = response.header("Location");

            // it worked
            success = true;

        }

        catch(IOException exception) {

            Log.e(TAG, exception.toString());

        }

        finally {

            response.body().close();
            return success;

        }

    }

    protected boolean updateMetadata() {
        // add the title and description to the video on Vimeo

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        boolean success = false;

        // try to update the metadata. return whether operation was successful
        try {

            // build the body for the PATCH request
            RequestBody patchBody = new FormBody.Builder()
                    .add("name", mMoment.getTitle())
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

            Log.e(TAG, exception.toString());

        }

        // return whether or not the request was successful
        return success;

    }

}
