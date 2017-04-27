package com.tikkunolam.momentsintime;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;

public class MakeAMomentActivity extends AppCompatActivity implements HolderInteractionListener{

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // ui references
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    // adapter for the RecyclerView that fills the whole Activity
    MakeAMomentAdapter mMakeAMomentAdapter;

    // a Moment to be filled by the user or loaded from disk
    Moment mMoment;

    // a list to be filled with the SectionTitle and SectionPrompt models and notes from the Moment
    ArrayList<Object> mViewModelList;

    // lists to be filled with the titles and prompts retrieved from string resources
    ArrayList<SectionTitle> mTitles;
    ArrayList<SectionPrompt> mPrompts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_moment);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);

        // get the RecyclerView. this holds everything in this activity but the toolbar
        mRecyclerView = (RecyclerView) findViewById(R.id.make_a_moment_recyclerView);

        // make a new Moment. to be filled with values from disk, or by the user
        mMoment = new Moment();

        // make a new mViewModelList
        mViewModelList = new ArrayList<Object>();

        // set up the RecyclerView
        setUpRecyclerView();

        // fake some notes to see if it works
        fakeSomeNotes();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.make_a_moment_menu, menu);

        return true;
    }

    public void setUpRecyclerView() {
        // sets up the RecyclerView with the MakeAMomentAdapter and mViewModelList

        // set up the mViewModelList
        setUpViewModelList();

        // set up the adapter
        mMakeAMomentAdapter = new MakeAMomentAdapter(this, mViewModelList);

        // set the MakeAMomentAdapter on the mRecyclerView
        mRecyclerView.setAdapter(mMakeAMomentAdapter);

        // set a LinearLayoutManager on it
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    public void setUpViewModelList() {
        // fills the mViewModelList with values

        // add the titles and prompts to the beginning of the mViewModelList
        addTitlesAndPrompts();

        // fake some notes
        fakeSomeNotes();

        // add all of the notes from the Moment
        mViewModelList.addAll(mMoment.getNotes());


    }

    public void addTitlesAndPrompts() {
        // adds titles and prompts to the beginning of the mViewModelList. used in setUpModelViewList.

        // retrieve all the titles and prompts from the string resources
        getTitlesAndPrompts();

        // add them all to the mViewModelList
        for(int i = 0; i < mTitles.size(); i++) {

            // add the title
            mViewModelList.add(mTitles.get(i));

            // add the corresponding prompts
            mViewModelList.add(mPrompts.get(i));

        }

    }

    public void getTitlesAndPrompts() {
        // retrieves all the title and prompt strings from resources. used in addTitlesAndPrompts

        // initialize the lists
        mTitles = new ArrayList<>();
        mPrompts = new ArrayList<>();

        // retrieve all the title strings and add them to mTitles as SectionTitles
        String interviewing = getBaseContext().getResources().getString(R.string.interviewing);
        mTitles.add(new SectionTitle(interviewing));
        String description = getBaseContext().getResources().getString(R.string.description);
        mTitles.add(new SectionTitle(description));
        String video = getBaseContext().getResources().getString(R.string.video);
        mTitles.add(new SectionTitle(video));
        String notes = getBaseContext().getResources().getString(R.string.notes);
        mTitles.add(new SectionTitle(notes));

        // retrieve all the prompt strings and add them to mPrompts as SectionPrompts
        String interviewingPrompt = getBaseContext().getResources().getString(R.string.interviewing_prompt);
        mPrompts.add(new SectionPrompt(interviewingPrompt));
        String descriptionPrompt = getBaseContext().getResources().getString(R.string.description_prompt);
        mPrompts.add(new SectionPrompt(descriptionPrompt));
        String videoPrompt = getBaseContext().getResources().getString(R.string.video_prompt);
        mPrompts.add(new SectionPrompt(videoPrompt));
        String notesPrompt = getBaseContext().getResources().getString(R.string.notes_prompt);
        mPrompts.add(new SectionPrompt(notesPrompt));


    }

    public void fakeSomeNotes() {

        mMoment.addNote("throw me on the tooooooooooooooooopp broooooooooooo");
        mMoment.addNote("Taylor Gray switch pop-shuv");
        mMoment.addNote("ayyyyyy baybeeeeeeeeeee you ever been to Tijuana???");
        mMoment.addNote("throw me on the tooooooooooooooooopp broooooooooooo");
        mMoment.addNote("Taylor Gray switch pop-shuv");
        mMoment.addNote("ayyyyyy baybeeeeeeeeeee you ever been to Tijuana???");

    }

    /**
     * CALLBACK IMPLEMENTATIONS
     */

    // the callback for clicks on the interviewing_prompt
    public void onInterviewingPromptClick() {
        // deal with acquiring an interviewee, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "opening the InterviewingActivity");

    }

    // the callback for when the description_prompt is clicked
    public void onDescriptionPromptClick() {
        // deal with acquiring a description/title, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "opening the DescriptionActivity");

    }

    public void onVideoPromptClick() {
        // deal with loading a FilmVideoIntent, adding the localVideoUri to the Moment, and refreshing

        Log.d(TAG, "expressing a video Intent");

    }

    public void onUploadPromptClick() {
        // deal with loading a FindVideoIntent, adding the localVideoUri to the Moment, and refreshing

        Log.d(TAG, "expressing a fetch video Intent");

    }

    public void onNotesPromptClick() {
        // deal with acquiring a new note, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "loading a NoteActivity");

    }

}
