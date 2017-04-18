package com.tikkunolam.momentsintime;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;



public class UploadService extends IntentService {
    // this service runs in the background and uploads a video to Vimeo

    /**
     * INSTANCE VARIABLES
     */

    // tag for logging purposes
    private final String TAG = "UploadService";

    // string for intent extra arguments/parameters
    String videoUriExtra = (String) getResources().getText(R.string.video_uri_extra);

    // app access token for authenticating requests
    private String mAccessToken = (String) getResources().getText(R.string.api_access_token);

    // base api address
    private String mApiAddress = (String) getResources().getText(R.string.api_base_address);

    // upload uri
    private String mVideoFetchUri = (String) getResources().getText(R.string.video_fetch_uri);

    // to be included in headers to let Vimeo know what version of the API we expect
    private String mApiVersion = (String) getResources().getText(R.string.api_version);

    // API endpoint for the upload ticket. query to learn more about upload
    String uploadTicketUri;

    // secure upload URL. url to which the video data is sent
    String uploadLink;


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

        // the uri for the local video file
        Uri uri = intent.getParcelableExtra(videoUriExtra);

        // create a file from the uri
        File videoFile = new File(uri.getPath());

        generateUploadTicket();
        uploadVideo(videoFile);
        checkUploadStatus();
        completeUpload();

    }

    protected void generateUploadTicket() {
        //generate an upload ticket
        // response contains information on how and where to upload

        OkHttpClient client = new OkHttpClient();

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
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            // transform the response to string for logging and JSON purposes
            String responseString = responseBody.string();
            Log.d(TAG, "generateUploadTicket: response: " + responseString);

            // create a JSONObject and extract the useful fields
            JSONObject jsonObject = new JSONObject(responseString);
            uploadTicketUri = jsonObject.getString("uri");
            uploadLink = jsonObject.getString("upload_link_secure");

        }

        catch(IOException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

        }

        catch(JSONException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

        }

    }

    protected void uploadVideo(File videoFile) {
        //upload the video

        // get the length in bytes of the file
        double fileSize = videoFile.length();

        // try to convert the file to a byte array
        byte[] bytes = new byte[(int) fileSize];
        try {

            FileInputStream inputStream = new FileInputStream(videoFile);
            inputStream.read(bytes);

        }

        catch(FileNotFoundException exception) {

            Log.d(TAG, "uploadVideo: " + exception.toString());

        }

        catch(IOException exception) {

            Log.d(TAG, "uploadVideo: " + exception.toString());

        }


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("mp4"), bytes);


    }

    protected void checkUploadStatus() {
        //check how much of the file has uploaded


    }

    protected void completeUpload() {
        // complete the upload. delete the upload ticket


    }

}
