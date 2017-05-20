package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class TopicActivity extends AppCompatActivity implements TopicAdapter.TopicInteractionListener, TopicPromptHolder.TopicPromptInteractionListener{

    // String to be used as a tag in log statements
    final String TAG = "TopicActivity";

    // String to be fetched from resources and used as the title in the Toolbar
    String mActivityTitle;
    // the String to be the prompt at the top of this page
    String mTopicPrompt;
    // the Strings to be used as Intent Extras
    String mTitleExtra, mDescriptionExtra;

    // Strings to return to MakeAMomentActivity
    String mTitle, mDescription;

    // integer identifiers for request codes
    final int TOPIC_REQUEST = 1;

    // list of the objects to fill the RecyclerView
    ArrayList<Object> mViewModelList;

    TopicAdapter mTopicAdapter;

    // ui references
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        // fetch the Strings from resources
        mActivityTitle = getString(R.string.topic_toolbar_title);
        mTopicPrompt = getString(R.string.topic_prompt);
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);

        // get the Toolbar and set it, and its title
        mToolbar = (Toolbar) findViewById(R.id.topic_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // fetch the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.topic_recyclerView);

        // set up the recyclerView
        setupRecyclerView();


    }

    // sets up the mRecyclerView by populating the mViewModelList and applying the Adapter
    private void setupRecyclerView() {

        // initialize the mViewModelList
        mViewModelList = new ArrayList<>();

        // fill the mViewModelList
        fillModelList();

        // initialize the Adapter
        mTopicAdapter = new TopicAdapter(this, mViewModelList);

        // set the Adapter on the RecyclerView
        mRecyclerView.setAdapter(mTopicAdapter);

        // set a LinearLayoutManager on it
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    // fills the mViewModelList with the prompt and all the topics
    private void fillModelList() {

        // add the TopicPrompt at the beginning of the mViewModelList
        mViewModelList.add(0, mTopicPrompt);

        // fill the list with the TopicCardData
        readTopicJson();

    }

    // reads the JSON default_topics file and makes a bunch of TopicCardData and puts them in the mViewModelList
    public void readTopicJson() {

        InputStream inputStream = getResources().openRawResource(R.raw.default_topics);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        // try to read the contents of the JSON file into a String
        try {

            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            int n;

            while ((n = reader.read(buffer)) != -1) {

                writer.write(buffer, 0, n);

            }

        }

        catch(IOException exception) {

            Log.e(TAG, exception.toString());

        }

        // close the InputStream
        finally {

            try {

                if(inputStream != null) {

                    inputStream.close();

                }

            }

            catch(IOException exception) {

                Log.e(TAG, exception.toString());

            }

        }

        // write the contents of the Writer to the String
        String jsonString = writer.toString();

        // try to make a JSONObject from the String and loop through its contents to make TopicCardData
        try {

            // convert the String to a JSONObject
            JSONObject topicsObject = new JSONObject(jsonString);

            // fetch the topics array from the JSONObject
            JSONArray topicArray = topicsObject.getJSONArray("topics");

            // loop through the topicsArray, making TopicCardData from its contents and inserting them into mViewModelList
            for(int i = 0; i < topicArray.length(); i++) {

                // get the JSONObject
                JSONObject topicObject = topicArray.getJSONObject(i);

                // craft a TopicCardData object out of it

                // get an iterator of the keys in the object. there will only be one... the topic
                Iterator<String> topicName = topicObject.keys();

                // get the topic
                String topic = topicName.next();

                // get the description
                String description = topicObject.getString(topic);

                // make a TopicCardData with the topic and description
                TopicCardData topicCardData = new TopicCardData(topic, description);

                // add it to the mViewModelList
                mViewModelList.add(topicCardData);

            }

        }

        catch(JSONException exception) {

            Log.e(TAG, exception.toString());

        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called upon returning from another Activity called with an implicit Intent

        // if the operation we asked for was completed successfully
        if(resultCode == RESULT_OK) {

            // if the Activity we're returning from is one indicated by TOPIC_REQUEST request code
            if(requestCode == TOPIC_REQUEST) {
                // fill the mTitle and mDescription from the result

                // get the title
                mTitle = data.getStringExtra(mTitleExtra);

                // get the description
                mDescription = data.getStringExtra(mDescriptionExtra);

                // return to the MakeAMomentActivity, title and description in tow
                Intent makeAMomentIntent = new Intent(this, MakeAMomentActivity.class);

                // attach the title and description
                makeAMomentIntent.putExtra(mTitleExtra, mTitle);
                makeAMomentIntent.putExtra(mDescriptionExtra, mDescription);

                // notify that the transaction was successful
                setResult(RESULT_OK, makeAMomentIntent);

                // return to the calling Activity
                finish();



            }

        }

    }

    public void onTopicClick(int position) {
        // a topic was clicked

        // get the TopicCardData
        TopicCardData topicCardData = (TopicCardData) mViewModelList.get(position);

        mTitle = topicCardData.getTitle();

        mDescription = topicCardData.getDescription();

        // return to the MakeAMomentActivity, title and description in tow
        Intent makeAMomentIntent = new Intent(this, MakeAMomentActivity.class);

        // attach the title and description
        makeAMomentIntent.putExtra(mTitleExtra, mTitle);
        makeAMomentIntent.putExtra(mDescriptionExtra, mDescription);

        // notify that the transaction was successful
        setResult(RESULT_OK, makeAMomentIntent);

        // return to the calling Activity
        finish();


    }

    public void onTopicPromptInteraction() {
        // the prompt at the top was clicked

        // open the CreateTopicActivity
        Intent createTopicIntent = new Intent(this, CreateTopicActivity.class);

        startActivityForResult(createTopicIntent, TOPIC_REQUEST);


    }

}
