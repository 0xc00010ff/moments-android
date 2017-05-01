package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class MakeAMomentActivity extends AppCompatActivity implements HolderInteractionListener{

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // Strings for use as Extra argument identifiers
    String nameExtra, roleExtra, photoUriExtra, titleExtra, descriptionExtra, noteExtra;

    // integers for use as request codes between Intents
    final int VIDEO_FROM_GALLERY = 1;
    final int VIDEO_FROM_CAMERA = 2;
    final int INTERVIEWING_INTENT = 3;
    final int DESCRIPTION_INTENT = 4;
    final int NOTE_INTENT = 5;

    // ui references
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    // Menu item so the color can be changed later
    MenuItem mSaveMenuItem;

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

        // get all the Extra argument names from resources
        nameExtra = getString(R.string.name_extra);
        roleExtra = getString(R.string.role_extra);
        photoUriExtra = getString(R.string.photo_uri_extra);
        titleExtra = getString(R.string.title_extra);
        descriptionExtra = getString(R.string.description_extra);
        noteExtra = getString(R.string.note_extra);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);


        // get the RecyclerView. this holds everything in this activity but the toolbar
        mRecyclerView = (RecyclerView) findViewById(R.id.make_a_moment_recyclerView);

        // make a new moment
        mMoment = new Moment();

        // make a new mViewModelList
        mViewModelList = new ArrayList<Object>();

        // set up the RecyclerView
        setUpRecyclerView();

        // set up the mViewModelList
        setUpViewModelList();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.make_a_moment_menu, menu);

        mSaveMenuItem = menu.findItem(R.id.save_menu_item);

        return true;
    }

    public void setUpRecyclerView() {
        // sets up the RecyclerView with the MakeAMomentAdapter and mViewModelList

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

        // clear it
        mViewModelList.clear();

        // add the titles and prompts to the beginning of the mViewModelList
        addTitlesAndPrompts();

        // add all of the notes from the Moment
        mViewModelList.addAll(mMoment.getNotes());

        // tell the adapter to update itself
        mMakeAMomentAdapter.notifyDataSetChanged();

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called upon returning from another Activity called with an implicit Intent

        // make sure the operation went through without a hitch
        if(resultCode == RESULT_OK) {

            switch(requestCode) {
                // determine which implicit intent we're receiving from based on the resultCode

                case INTERVIEWING_INTENT:
                    // a Moment is being returned from InterviewingActivity

                    // fetch the name and assign it to the mMoment
                    String name = data.getStringExtra(nameExtra);
                    mMoment.setInterviewee(name);

                    // fetch the role and assign it to the mMoment
                    String role = data.getStringExtra(roleExtra);
                    mMoment.setIntervieweeRole(role);

                    // fetch the photoUri and assign it to the mMoment
                    String photoUri = data.getStringExtra(photoUriExtra);

                    if(photoUri != null) {

                        mMoment.setIntervieweePhotoUri(Uri.parse(photoUri));

                    }

                    // do whatever to refresh the list... probably replace whatever item in the list with an Interviewee

                    break;

                case DESCRIPTION_INTENT:
                    // a Moment is being returned from DescriptionActivity

                    // get the title from the Intent and assign it to the mMoment
                    String title = data.getStringExtra(titleExtra);
                    mMoment.setName(title);

                    // get the description from the Intent and assign it to the mMoment
                    String description = data.getStringExtra(descriptionExtra);
                    mMoment.setDescription(description);

                    // do whatever to refresh the list... probably replace whatever item is in the list with a Description


                    break;

                case NOTE_INTENT:
                    // a note is being returned from NotesActivity. get it, add it to the mMoment, and refresh the Adapter

                    String note = data.getStringExtra(noteExtra);

                    mMoment.addNote(note);

                    // add it to the viewlist
                    mViewModelList.add(note);

                    mMakeAMomentAdapter.notifyDataSetChanged();

                    break;


                case VIDEO_FROM_GALLERY:
                    // the user just fetched a video from the gallery. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    Uri selectedVideoUri = data.getData();

                    // convert it to a string
                    String selectedVideoString = selectedVideoUri.toString();

                    // put it in the mMoment's localVideoUri field
                    mMoment.setLocalVideoUri(selectedVideoString);

                    // replace the section_prompt with a video_card

                    break;

                case VIDEO_FROM_CAMERA:
                    // the user filmed a video. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    Uri filmedVideoUri = data.getData();

                    // convert it to a string
                    String filmedVideoString = filmedVideoUri.toString();

                    // put it in the mMoment's localVideoUri field
                    mMoment.setLocalVideoUri(filmedVideoString);

                    // replace the view with a video_card

            }

        }


    }

    /**
     * CALLBACK IMPLEMENTATIONS
     */

    // the callback for clicks on the interviewing_prompt
    public void onInterviewingPromptClick() {
        // deal with acquiring an interviewee, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "opening the InterviewingActivity");

        // make an intent with the InterviewingActivity
        Intent interviewingIntent = new Intent(getBaseContext(), InterviewingActivity.class);

        startActivityForResult(interviewingIntent, INTERVIEWING_INTENT);

    }

    // the callback for when the description_prompt is clicked
    public void onDescriptionPromptClick() {
        // deal with acquiring a description/title, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "opening the DescriptionActivity");

        // make an Intent with the DescriptionActivity
        Intent descriptionIntent = new Intent(getBaseContext(), DescriptionActivity.class);

        // start the Activity
        startActivityForResult(descriptionIntent, DESCRIPTION_INTENT);

    }

    public void onVideoPromptClick() {
        // deal with loading a FilmVideoIntent, adding the localVideoUri to the Moment, and refreshing

        Log.d(TAG, "expressing a video Intent");

        // express an implicit intent for to film a video
        Intent filmVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        startActivityForResult(filmVideoIntent, VIDEO_FROM_CAMERA);


    }

    public void onUploadPromptClick() {
        // deal with loading a FindVideoIntent, adding the localVideoUri to the Moment, and refreshing

        Log.d(TAG, "expressing a fetch video Intent");

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_FROM_GALLERY);

    }

    public void onNotesPromptClick() {
        // deal with acquiring a new note, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "loading a NoteActivity");

        // make an Intent with the NoteActivity
        Intent noteIntent = new Intent(getBaseContext(), NoteActivity.class);

        startActivityForResult(noteIntent, NOTE_INTENT);

    }

    private void enableSave() {
        // change menu item color to white to indicate save is clickable

        // make a SpannableString from the title of the mSaveMenuItem
        SpannableString saveString = new SpannableString(mSaveMenuItem.getTitle());

        // color the SpannableString
        saveString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, saveString.length(), 0);

        // set the mSaveMenuItem's title to the SpannableString
        mSaveMenuItem.setTitle(saveString);

    }

    private void disableSave() {
        // change menu item color to grey to indicate save is unclickable

        // make a SpannableString from the title of the mSaveMenuItem
        SpannableString saveString = new SpannableString(mSaveMenuItem.getTitle());

        // color the SpannableString
        saveString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textLight)), 0, saveString.length(), 0);

        // set the mSaveMenuItem's title to the SpannableString
        mSaveMenuItem.setTitle(saveString);

    }

}
