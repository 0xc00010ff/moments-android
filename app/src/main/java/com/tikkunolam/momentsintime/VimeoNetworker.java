package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tikkunolam.momentsintime.R.string.local_video_uri_extra;
import static com.tikkunolam.momentsintime.R.string.video;
import static com.tikkunolam.momentsintime.R.string.video_file_extra;

/**
 * OKHTTP NETWORKING CLASS FOR API CALLS
 */

public class VimeoNetworker {

    /**
     * INSTANCE VARIABLES
     */

    // tag for logging
    private final String TAG = "Networking";

    // strings for intent extra arguments/parameters
    String videoFileExtra, mPrimaryKeyExtra;

    // app access token for authenticating requests
    private String mAccessToken;

    // base api address
    private String mApiAddress;

    // this is the string for both fetching and uploading. Difference is in the request type.
    private String mVideoFetchUri;

    // part of the query string for specifying page number for videos fetch
    private String mPageNumberParameter;

    // part of the query string for specifying number of videos per page
    private String mPerPageParameter;

    // number of videos to request per page
    private final String mVideosPerPage = "20";

    private final int mBadRequest = 400;

    // to be included in headers to let Vimeo know what version of the API we expect
    private String mApiVersion;


    /**
     * CONSTRUCTORS
     */

    public VimeoNetworker(Context applicationContext) {
        // takes the ApplicationContext to retrieve strings from resources

        // set all the api information from the application context
        mAccessToken = applicationContext.getString(R.string.api_access_token);
        mApiAddress = applicationContext.getString(R.string.api_base_address);
        mVideoFetchUri = applicationContext.getString(R.string.video_fetch_uri);
        mPageNumberParameter = applicationContext.getString(R.string.page_number_parameter);
        mPerPageParameter = applicationContext.getString(R.string.per_page_parameter);
        mApiVersion = applicationContext.getString(R.string.api_version);

        // set the intent extras' argument names
        videoFileExtra = applicationContext.getString(R.string.video_file_extra);
        mPrimaryKeyExtra = applicationContext.getString(R.string.primary_key_extra);



    }

    /**
     * INSTANCE METHODS
     */

    public GetMomentsResponse getCommunityMoments(int pageNumber) {
        // fetches the list of Videos for the CommunityFragment

        GetMomentsResponse getMomentsResponse = new GetMomentsResponse();

        OkHttpClient client = new OkHttpClient();
        ArrayList<Moment> moments = new ArrayList<>();
        Response response = null;

        String pageNumberString = String.valueOf(pageNumber);

        try {
            // try to fetch the content

            // build the request
            Request request = new Request.Builder()
                    .url(mApiAddress + mVideoFetchUri + "?" + mPageNumberParameter + pageNumberString + "&" + mPerPageParameter + mVideosPerPage)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            // convert the body to a String
            String responseString = response.body().string();

            // convert the String to a JSONObject
            JSONObject jsonResponse = new JSONObject(responseString);

            // get the JSONObject related to pagination from the response
            JSONObject paginationObject = jsonResponse.getJSONObject("paging");

            // get the String signifying if there is a next page
            String isNextPage = paginationObject.getString("next");

            if(isNextPage.equals("null")) {

                getMomentsResponse.setNextPageExists(false);

            }

            else {

                getMomentsResponse.setNextPageExists(true);

            }

            // pass the JSONObject to the method that creates the Moment list
            moments = jsonToMomentList(jsonResponse);


        }

        catch(IOException exception) {

            // log it so I'm sure where it came from
            Log.e(TAG, "IO EXCEPTION!!!!!!");

            throw new RuntimeException(exception);

        }

        catch(JSONException exception) {

            // log it so I'm sure where it came from
            Log.e(TAG, "JSON EXCEPTION!!!!!");

            throw new RuntimeException(exception);

        }

        finally {
            // finally close the response body

            if(response != null) {

                response.body().close();

            }

        }

        // add the moments list to the GetMomentsResponse

        getMomentsResponse.setMomentList(moments);

        return getMomentsResponse;

    }

    public String getPlayableVideo(String videoUri) {
        // takes the uri from a mMoment and fetches the video playback url

        OkHttpClient client = new OkHttpClient();
        Response response = null;

        try {

            // build the request
            Request request = new Request.Builder()
                    .url(mApiAddress + "/me" + videoUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            // convert the body to a String
            String responseString = response.body().string();

            // convert the String to a JSONObject
            JSONObject jsonResponse = new JSONObject(responseString);

            // get the video data
            JSONArray jsonArray = jsonResponse.getJSONArray("files");

            // fetch the highest quality video
            JSONObject jsonVideo = fetchHighestQualityVideo(jsonArray);

            String videoUrl = jsonVideo.getString("link");

            return videoUrl;

        }

        catch(IOException exception) {

            Log.e(TAG, "getPlayableVideo" + exception.toString());

            return null;

        }

        catch(JSONException exception) {

            Log.e(TAG, "getPlayableVideo" + exception.toString());

            return null;

        }

        finally {

            response.body().close();

        }
    }

    public void uploadMoment(String videoFile, String momentPrimaryKey, Context context) {
        // upload a mMoment to Vimeo using the UploadService in the background

        // make an intent with UploadService
        Intent uploadIntent = new Intent(context, UploadService.class);

        // add the Moment's video file
        uploadIntent.putExtra(videoFileExtra, videoFile);

        // add the Moment's primaryKey
        uploadIntent.putExtra(mPrimaryKeyExtra, momentPrimaryKey);

        // start the service
        context.startService(uploadIntent);

    }

    public ArrayList<Moment> jsonToMomentList(JSONObject jsonResponse) {
        // parse the json to create an ArrayList<Moment>
        // will throw out all of the crap except for what creates my Moment object

        // reference so the Moment list can be returned outside of the try block
        ArrayList<Moment> moments = new ArrayList<Moment>();

        try{
            // try to parse the JSON to create the Moment list

            // get the array of JSONObjects corresponding to Videos
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            // for every JSONObject in the array, create a Moment and add it to the list
            for(int i = 0; i < jsonArray.length(); i++) {

                // get the Moment
                JSONObject jsonVideoObject = jsonArray.getJSONObject(i);

                // get the attributes that are one level deep
                String name = jsonVideoObject.getString("name");
                String description = jsonVideoObject.getString("description");

                if(description.equals("null")) {
                    // API returns "null" so make it empty instead

                    description = "";

                }

                String uri = jsonVideoObject.getString("uri");
                String url = jsonVideoObject.getString("link");

                // get the pictures JSONObject (a list of the same pic in different sizes)
                JSONObject picturesObject = jsonVideoObject.getJSONObject("pictures");
                JSONArray sizesArray = picturesObject.getJSONArray("sizes");

                // get the third picture. somewhere around 300 x 150
                // this may need to be more robust, in case Vimeo is inconsistent
                JSONObject picture = sizesArray.getJSONObject(4);

                // get the url
                String pictureUrl = picture.getString("link");

                // create the Moment how God intended
                Moment moment = new Moment();
                moment.setTitle(name);
                moment.setDescription(description);
                moment.setVideoUri(uri);
                moment.setVideoUrl(url);
                moment.setPictureUrl(pictureUrl);

                // add it to the Moment list
                moments.add(moment);
            }
        }
        catch(JSONException exception) {

            // log it so I'm sure where it came from
            Log.e(TAG, "JSON Exception!!!");

            throw new RuntimeException(exception);
        }

        // return the list
        // may be null. should check for that where it's called
        return moments;

    }

    public JSONObject fetchHighestQualityVideo(JSONArray jsonArray) {
        // loop through the JSONArray and find the highest quality video

        JSONObject highestQualityVideo = null;

        try {

            // maintain the index of the video with the highest quality
            int highest = 0;

            // the current highest quality value
            int currentHigh = 0;

            for(int i = 0; i < jsonArray.length(); i++) {

                // get the video object
                JSONObject video = jsonArray.getJSONObject(i);

                if(video.has("height")) {

                    // get the video's quality
                    int videoQuality = video.getInt("height");

                    // if it has the highest quality so far, update highest with its index
                    if(videoQuality >= currentHigh) {

                        currentHigh = videoQuality;
                        highest = i;

                    }

                }

            }

            // set the highestQualityVideo to be returned
            highestQualityVideo = jsonArray.getJSONObject(highest);

        }

        catch(JSONException jsonException) {

            Log.e(TAG, "fetchHighestQualityVideo " + jsonException);

        }

        // return the highest quality video
        return highestQualityVideo;


    }

    public Moment getSingleMoment(Uri videoUri) {
        // returns a single Moment

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        Moment moment = new Moment();

        try {
            // try to retrieve the Moment from Vimeo

            Request request = new Request.Builder()
                    .url(mApiAddress + "/me" + videoUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            // convert the body to a String
            String responseString = response.body().string();

            // convert the String to a JSONObject
            JSONObject jsonResponse = new JSONObject(responseString);

            // get the attributes that are one level deep
            String name = jsonResponse.getString("name");
            String description = jsonResponse.getString("description");

            if(description.equals("null")) {
                // API returns "null" so make it empty instead

                description = "";

            }

            // get the pictures JSONObject (a list of the same pic in different sizes)
            JSONObject picturesObject = jsonResponse.getJSONObject("pictures");
            JSONArray sizesArray = picturesObject.getJSONArray("sizes");

            // get the third picture. somewhere around 300 x 150
            // this may need to be more robust, in case Vimeo is inconsistent
            JSONObject picture = sizesArray.getJSONObject(4);

            // get the url
            String pictureUrl = picture.getString("link");

            moment.setTitle(name);
            moment.setDescription(description);
            moment.setPictureUrl(pictureUrl);


        }

        catch(IOException exception) {

            Log.e(TAG, exception.toString());

        }

        catch(JSONException jsonException) {

            Log.e(TAG, jsonException.toString());

        }

        return moment;

    }

}
