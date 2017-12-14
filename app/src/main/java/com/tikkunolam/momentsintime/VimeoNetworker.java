package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

    // strings for JSON filter request arguments
    String mCommunityFilter, mPlayableVideoFilter, mSingleMomentFilter, mDeleteMomentFilter;

    // app access token for authenticating requests
    private String mAccessToken;

    // base api address
    private String mApiAddress;

    // this is the string for both fetching and uploading. Difference is in the request type.
    private String mVideoFetchUri;

    // the string for fetching from the main album
    private String mMainAlbumFetchUri;

    // part of the query string for specifying page number for videos fetch
    private String mPageNumberParameter;

    // part of the query string for specifying number of videos per page
    private String mPerPageParameter;

    // number of videos to request per page
    private final String mVideosPerPage = "20";

    private final int mBadRequest = 400;

    // to be included in headers to let Vimeo know what version of the API we expect
    private String mApiVersion;

    // the word "available" to match to Vimeo's status response
    private String mAvailabilityString;


    /**
     * CONSTRUCTORS
     */

    public VimeoNetworker(Context applicationContext) {
        // takes the ApplicationContext to retrieve strings from resources

        // set all the api information from the application context
        mAccessToken = applicationContext.getString(R.string.api_access_token);
        mApiAddress = applicationContext.getString(R.string.api_base_address);
        mVideoFetchUri = applicationContext.getString(R.string.video_fetch_uri);
        mMainAlbumFetchUri = applicationContext.getString(R.string.main_album_fetch);
        mPageNumberParameter = applicationContext.getString(R.string.page_number_parameter);
        mPerPageParameter = applicationContext.getString(R.string.per_page_parameter);
        mApiVersion = applicationContext.getString(R.string.api_version);

        // set the intent extras' argument names
        videoFileExtra = applicationContext.getString(R.string.video_file_extra);
        mPrimaryKeyExtra = applicationContext.getString(R.string.primary_key_extra);

        // get the JSON filter request argument Strings
        mCommunityFilter = applicationContext.getString(R.string.community_filter);
        mPlayableVideoFilter = applicationContext.getString(R.string.playable_video_filter);
        mSingleMomentFilter = applicationContext.getString(R.string.single_moment_filter);
        mDeleteMomentFilter = applicationContext.getString(R.string.delete_moment_filter);

        mAvailabilityString = applicationContext.getString(R.string.video_availability);



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
                    .url(mApiAddress + mMainAlbumFetchUri + "?" + mPageNumberParameter + pageNumberString + "&" + mPerPageParameter + mVideosPerPage + "&" + mCommunityFilter + "&sort=manual")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            if(response.isSuccessful()) {

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

        }

        catch(IOException exception) {

        }

        catch(JSONException exception) {

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
                    .url(mApiAddress + "/me" + videoUri + "?" + mPlayableVideoFilter)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            if(response.isSuccessful()) {

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

            else {

                return null;

            }

        }

        catch(IOException exception) {

            return null;

        }

        catch(JSONException exception) {

            return null;

        }

        finally {

            if(response != null) {

                response.body().close();

            }

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

                String status = jsonVideoObject.getString("status");

                // if the status is available, add it to the list
                if(status.equals(mAvailabilityString)) {

                    // get the uri and url
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
        }
        catch(JSONException exception) {

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
                    .url(mApiAddress + "/me" + videoUri + "?" + mSingleMomentFilter)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
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
            String url = jsonResponse.getString("link");
            String status = jsonResponse.getString("status");

            if(description.equals("null")) {
                // API returns "null" so make it empty instead

                description = "";

            }

            // determine if the Moment is available yet
            boolean availability = status.equals(mAvailabilityString);

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
            moment.setVideoUrl(url);
            moment.setAvailable(availability);


        }

        catch(IOException exception) {

        }

        catch(JSONException jsonException) {

        }

        finally {
            // close the response

            if(response != null) {

                response.body().close();

            }

        }

        return moment;

    }

    public boolean deleteMoment(Moment moment) {
        // deletes the Moment from Vimeo

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        boolean success = false;

        // try to delete the video
        try{

            Request request = new Request.Builder()
                    .url(mApiAddress + moment.getVideoUri() + "?" + mDeleteMomentFilter)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .delete()
                    .build();

            response = client.newCall(request).execute();

            success = true;

        }

        catch(IOException exception) {

        }

        finally {

            if(response != null) {

                response.body().close();

            }

        }

        return success;

    }

    public GetMomentsResponse searchVimeo(SearchByNameArgument searchArguments) {
        /**
         * searches Vimeo for videos with titles matching the searchString and returns them in an ArrayList<Moment>
         */

        // make a new GetMomentsResponse to return to the caller
        GetMomentsResponse getMomentsResponse = new GetMomentsResponse();

        ArrayList<Moment> moments = null;

        OkHttpClient client = new OkHttpClient();

        Response response = null;

        try {

            Request request = new Request.Builder()
                    .url(mApiAddress + "/me/videos?query=" + searchArguments.getSearchString() + "&" + mCommunityFilter + "&per_page=25" + "&page=" + searchArguments.getPageNumber())
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .addHeader("Accept", mApiVersion)
                    .build();

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

            // get a list of Moments
            moments = jsonToMomentList(jsonResponse);

        }

        catch(IOException exception) {

        }

        catch(JSONException exception) {

        }

        finally {

            if(response != null) {

                // close the response
                response.body().close();

            }

        }

        // set the response's Moment list with the Moments we've just fetched
        getMomentsResponse.setMomentList(moments);

        return getMomentsResponse;

    }

}
