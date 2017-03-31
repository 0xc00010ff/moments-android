package com.tikkunolam.momentsintime;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.R.string.ok;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;

/**
 * OKHTTP NETWORKING CLASS FOR API CALLS
 * IMPLEMENTED AS A SINGLETON -- ONLY ONE INSTANCE MAY EXIST
 */

public class VimeoNetworkingSingleton {

    /**
     * INSTANCE VARIABLES
     */

    // tag for logging
    private final String TAG = "Networking";

    // static instance
    private static VimeoNetworkingSingleton instance = null;

    // app access token for authenticating requests
    private String mAccessToken = "c3867c80fcfb0b0c177f012d841fd1c3";

    // base api address
    private final String apiAddress = "https://api.vimeo.com";

    // this is the string for both fetching and uploading. Difference is in the request type.
    private final String videoFetchUri = "/me/videos";

    // to be included in headers to let Vimeo know what version of the API we expect
    private final String apiVersion = "application/vnd.vimeo.*+json;version=3.2";

    /**
     * CONSTRUCTORS
     */

    private VimeoNetworkingSingleton() {
        // private constructor can only be called by the getInstance() method

    }

    /**
     * STATIC METHODS
     */

    public static VimeoNetworkingSingleton getInstance(){
        // returns the instance, or creates and returns one if one doesn't exist

        if(instance == null){

            instance = new VimeoNetworkingSingleton();

        }

        return instance;
    }

    /**
     * INSTANCE METHODS
     */

    public ArrayList<Video> getCommunityVideos() {
        // fetches the list of Videos for the CommunityFragment

        OkHttpClient client = new OkHttpClient();
        ArrayList<Video> videos = new ArrayList<>();
        Response response = null;

        try {
            // try to fetch the content

            // build the request
            Request request = new Request.Builder()
                    .url(apiAddress + videoFetchUri)
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();

            // make the call and receive the response
            response = client.newCall(request).execute();

            // convert the body to a String
            String responseString = response.body().string();

            // convert the String to a JSONObject
            JSONObject jsonResponse = new JSONObject(responseString);

            // pass the JSONObject to the method that creates the Video list
            videos = jsonToVideoList(jsonResponse);

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

        // return the Video list
        // might be null from an exception. check for this where it's called
        return videos;

    }

    public void uploadVideo() {
        // upload a video to Vimeo
        // will accept a uri link to the file

    }

    public ArrayList<Video> jsonToVideoList(JSONObject jsonResponse) {
        // parse the json to create an ArrayList<Video>
        // will throw out all of the crap except for what creates my Video object

        // reference so the Video list can be returned outside of the try block
        ArrayList<Video> videos = new ArrayList<Video>();

        try{
            // try to parse the JSON to create the Video list

            // get the array of JSONObjects corresponding to Videos
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            // for every JSONObject in the array, create a Video and add it to the list
            for(int i = 0; i < jsonArray.length(); i++) {

                // get the Video
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

                // get the width and height. may prove to be unnecessary
                int width = Integer.parseInt(jsonVideoObject.getString("width"));
                int height = Integer.parseInt(jsonVideoObject.getString("height"));

                // create the Video how God intended
                Video video = new Video(name, description, uri, url, pictureUrl, width, height);

                // add it to the Video list
                videos.add(video);
            }
        }
        catch(JSONException exception) {

            // log it so I'm sure where it came from
            Log.e(TAG, "JSON Exception!!!");

            throw new RuntimeException(exception);
        }

        // return the list
        // may be null. should check for that where it's called
        return videos;
    }

}
