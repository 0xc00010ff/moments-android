package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import static com.tikkunolam.momentsintime.R.string.moment_extra;

public class MakeAMomentActivity extends AppCompatActivity implements HolderInteractionListener{

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // Strings for use as Extra argument identifiers
    String mMomentExtra, mNameExtra, mRoleExtra, mPhotoUriExtra, mTitleExtra, mDescriptionExtra, mNoteExtra;

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
        mMomentExtra = getString(moment_extra);
        mNameExtra = getString(R.string.name_extra);
        mRoleExtra = getString(R.string.role_extra);
        mPhotoUriExtra = getString(R.string.photo_uri_extra);
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);
        mNoteExtra = getString(R.string.note_extra);

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

        mSaveMenuItem = menu.findItem(R.id.make_a_moment_submit);
        mSaveMenuItem.setEnabled(false);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // called when a MenuItem is selected

        switch(item.getItemId()) {

            case R.id.make_a_moment_submit:
                // SUBMIT was selected... check if mMoment is submittable and if so submit it

                if(isMomentComplete()) {

                    // submit the Moment
                    Log.d(TAG, "submitting the Moment");
                    submitMoment();
                    finish();

                }


        }

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

    private void enableDisableMomentSubmission() {
        // checks if the mMoment is ready for submission, and if so makes submit MenuItem white, if not makes it grey

        if(isMomentComplete()) {

            // turn MenuItem white
            mSaveMenuItem.setEnabled(true);

        }

        else {

            // turn MenuItem grey
            mSaveMenuItem.setEnabled(false);

        }


    }

    private boolean isMomentComplete() {
        // checks if the mMoment is sufficiently filled out to be submitted

        // if the user has provided a title, description, interviewee, and video then it's good
        if (mMoment.getName() != null && mMoment.getDescription() != null
                && mMoment.getInterviewee() != null && mMoment.getLocalVideoUri() != null) {

            return true;

        }

        else {

            return false;

        }

    }

    private void submitMoment() {
        // submits the Moment

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called upon returning from another Activity called with an implicit Intent

        // make sure the operation went through without a hitch
        if(resultCode == RESULT_OK) {

            switch(requestCode) {
                // determine which implicit intent we're receiving from based on the resultCode

                case INTERVIEWING_INTENT:
                    // a Moment is being returned from InterviewingActivity

                    mMoment = data.getParcelableExtra(mMomentExtra);

                    // insert an interviewing_card in the mViewModelList
                    insertInterviewingCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case DESCRIPTION_INTENT:
                    // a Moment is being returned from DescriptionActivity

                    //get the Moment from the Intent
                    mMoment = data.getParcelableExtra(mMomentExtra);

                    // insert a description_card in place of section_prompt_text
                    insertDescriptionCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case NOTE_INTENT:
                    // a note is being returned from NotesActivity. get it, add it to the mMoment, and refresh the Adapter

                    String note = data.getStringExtra(mNoteExtra);

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

                    // put it in the mMoment's localVideoUri field
                    mMoment.setLocalVideoUri(selectedVideoUri);

                    // replace the section_prompt with a video_card
                    insertVideoCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case VIDEO_FROM_CAMERA:
                    // the user filmed a video. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    Uri filmedVideoUri = data.getData();

                    // put it in the mMoment's localVideoUri field
                    mMoment.setLocalVideoUri(filmedVideoUri);

                    // replace the view with a video_card
                    insertVideoCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

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

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.interviewing)
                .items(R.array.interviewing_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        // when a dialog option is selected these callbacks are used

                        switch(position) {

                            case 0:
                                // they chose Facebook...

                                break;

                            case 1:
                                // they chose to make one manually

                                // make an intent with the InterviewingActivity
                                Intent interviewingIntent = new Intent(getBaseContext(), InterviewingActivity.class);

                                interviewingIntent.putExtra(mMomentExtra, mMoment);

                                startActivityForResult(interviewingIntent, INTERVIEWING_INTENT);

                                break;

                            case 2:
                                // they chose to cancel. auto dismiss is on so do nothing

                                break;

                        }

                    }

                })
                .show();

    }

    // the callback for when the description_prompt is clicked
    public void onDescriptionPromptClick() {
        // deal with acquiring a description/title, adding it to the Moment, and refreshing the Adapter

        Log.d(TAG, "opening the DescriptionActivity");

        // make an Intent with the DescriptionActivity
        Intent descriptionIntent = new Intent(getBaseContext(), DescriptionActivity.class);

        descriptionIntent.putExtra(mMomentExtra, mMoment);

        // start the Activity
        startActivityForResult(descriptionIntent, DESCRIPTION_INTENT);

    }

    public void onVideoPromptClick() {
        // deal with loading a FilmVideoIntent, adding the localVideoUri to the Moment, and refreshing

        Log.d(TAG, "expressing a video Intent");

        // express an implicit intent to film a video
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

    public void onVideoDotsClick() {
        // open the dialog to ask the user to edit/delete the video

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.video_dialog_title)
                .items(R.array.video_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to edit


                                break;

                            case 1:
                                // user chose to delete

                                break;

                            case 2:
                                // user chose to cancel. auto dismiss is on so do nothing

                                break;

                        }

                    }

                })
                .show();

    }

    public void onNoteCardDotsClick(final int notePosition) {
        // open the dialog to ask the user to delete the note

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.note_dialog_title)
                .items(R.array.note_dialog_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to delete... delete the note at the position from the mViewModelList

                                // delete it
                                mViewModelList.remove(notePosition);

                                // tell the Adapter to update the RecyclerView
                                mMakeAMomentAdapter.notifyDataSetChanged();

                                break;

                            case 1:
                                // user chose to cancel... auto dismiss is on so do nothing

                                break;

                        }

                    }

                })
                .show();


    }

    // the callback for when the play button in the video_card is clicked
    public void onPlayButtonClick() {
        // start a VideoViewActivity

        // make the Intent
        Intent videoViewActivityIntent = new Intent(this, VideoViewActivity.class);

        // bundle the Moment with it
        videoViewActivityIntent.putExtra(mMomentExtra, mMoment);

        // start the Activity
        startActivity(videoViewActivityIntent);

    }

    /**
     * methods that insert views into the RecyclerView
     */

    private void insertInterviewingCard() {
        // inserts a filled out interviewing_card in place of the corresponding section_prompt_text

        // make a new InterviewingCardData
        InterviewingCardData interviewingCardData = new InterviewingCardData(mMoment.getInterviewee());

        // add the intervieweeRole if there is one
        if(mMoment.getIntervieweeRole() != null) {

            interviewingCardData.setIntervieweeRole(mMoment.getIntervieweeRole());

        }

        // add the intervieweePhotoUri if there is one
        if(mMoment.getIntervieweePhotoUri() != null) {

            interviewingCardData.setIntervieweePhotoUri(mMoment.getIntervieweePhotoUri());

        }

        // replace the prompt with the InterviewingCardData
        mViewModelList.set(1, interviewingCardData);

        // tell the adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void insertDescriptionCard() {
        // inserts a filled out description_card in place of the corresponding section_prompt_text

        // mMoment is guaranteed to have the necessary fields so just fill out a DescriptionCardData
        DescriptionCardData descriptionCardData = new DescriptionCardData(mMoment.getName(), mMoment.getDescription());

        // replace the prompt with the DescriptionCardData
        mViewModelList.set(3, descriptionCardData);

        // tell the adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void insertVideoCard() {
        // inserts a filled out video_card in place of the corresponding section_prompt_text

        // mMoment is guaranteed to have the necessary field so just fill out a VideoCardData

        // make a new VideoCardData with it
        VideoCardData videoCardData = new VideoCardData(mMoment.getLocalVideoUri());

        // replace the prompt with the VideoCardData
        mViewModelList.set(5, videoCardData);

        // tell the Adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();


    }

}
