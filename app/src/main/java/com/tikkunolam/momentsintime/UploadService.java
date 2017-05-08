package com.tikkunolam.momentsintime;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
    // this service runs in the background and uploads a mMoment to Vimeo

    /**
     * INSTANCE VARIABLES
     */

    // tag for logging purposes
    private final String TAG = "UploadService";

    private final int RESPONSE_OKAY = 200;

    // string for intent extra arguments/parameters
    String mVideoFileExtra;

    // app access token for authenticating requests
    private String mAccessToken;

    // base api address
    private String mApiAddress;

    // upload uri
    private String mVideoFetchUri;

    // to be included in headers to let Vimeo know what version of the API we expect
    private String mApiVersion;

    // API endpoint for the upload ticket. query to learn more about upload
    String uploadTicketUri;

    // Uri to make the delete call to
    String completeUri;

    // secure upload URL. url to which the mMoment data is sent
    String uploadLink;

    // the final location of the uploaded video
    String finalUri;


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

        // wait for the debugger
        android.os.Debug.waitForDebugger();

        Log.d(TAG, "FUCK");

        // string for intent extra arguments/parameters
        mVideoFileExtra = getString(R.string.video_file_extra);

        // app access token for authenticating requests
        mAccessToken = getString(R.string.api_access_token);

        // base api address
        mApiAddress = getString(R.string.api_base_address);

        // upload uri
        mVideoFetchUri = getString(R.string.video_fetch_uri);

        // to be included in headers to let Vimeo know what version of the API we expect
        mApiVersion = getString(R.string.api_version);

        // the uri for the local mMoment file
        String videoFileString = intent.getStringExtra(mVideoFileExtra);


        generateUploadTicket();
        boolean successfulUpload = uploadVideo(videoFileString);

        if(successfulUpload) {

            completeUpload();

        }

        else {

            // the upload failed... notify the Moment and maybe try again

        }

    }

    protected void generateUploadTicket() {
        //generate an upload ticket
        // response contains information on how and where to upload

        OkHttpClient client = new OkHttpClient();
        Response response = null;

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
            Log.d(TAG, "generateUploadTicket: response: " + responseString);

            // create a JSONObject and extract the useful fields
            JSONObject jsonObject = new JSONObject(responseString);
            uploadTicketUri = jsonObject.getString("uri");
            completeUri = jsonObject.getString("complete_uri");
            uploadLink = jsonObject.getString("upload_link_secure");

        }

        catch(IOException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

        }

        catch(JSONException exception) {

            Log.d(TAG, "generateUploadTicket: " + exception.toString());

        }

        finally {

            response.body().close();

        }

    }

    protected boolean uploadVideo(String videoFileString) {
        //upload the mMoment

        boolean success = false;

        File file = new File(videoFileString);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        Response response = null;

        // try to convert the file to a byte array and make the request
        try{

            bytes = FileDealer.fullyReadFileToBytes(file);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(MediaType.parse("mp4"), bytes);
            Request request = new Request.Builder()
                    .url(uploadLink)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .put(requestBody)
                    .build();

            response = client.newCall(request).execute();

            int resultCode = response.code();

            if(resultCode == RESPONSE_OKAY) {

                success = true;

            }

        }

        catch(IOException exception) {

            Log.e(TAG, exception.toString());
            success = false;

        }

        finally {

            response.body().close();
            return success;

        }


    }

    protected void completeUpload() {
        // complete the upload. delete the upload ticket

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        try {
            // try to make a DELETE request to get the finalUri for the Moment

            Request request = new Request.Builder()
                    .url(mApiAddress + completeUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .delete()
                    .build();

            response = client.newCall(request).execute();


            finalUri = response.header("Location");

        }

        catch(IOException exception) {

            Log.e(TAG, exception.toString());

        }

    }

}
