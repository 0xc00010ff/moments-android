package com.tikkunolam.momentsintime;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import android.content.CursorLoader;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;

import static com.tikkunolam.momentsintime.R.string.primary_key_extra;

public class MakeAMomentActivity extends AppCompatActivity implements HolderInteractionListener{

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // location of first note
    final int FIRST_NOTE_LOCATION = 8;

    // Strings for use as Extra argument identifiers
    String mPrimaryKeyExtra, mNoteExtra, mLocalVideoFileExtra, mIntervieweeExtra, mRoleExtra, mIntervieweePhotoFileExtra,
            mTitleExtra, mDescriptionExtra;

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
        mPrimaryKeyExtra = getString(primary_key_extra);
        mNoteExtra = getString(R.string.note_extra);
        mLocalVideoFileExtra = getString(R.string.local_video_file_extra);
        mIntervieweeExtra = getString(R.string.interviewee_extra);
        mRoleExtra = getString(R.string.interviewee_role_extra);
        mIntervieweePhotoFileExtra = getString(R.string.interviewee_photo_file_extra);
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);

        // fetch the primaryKey that may have been passed in
        String primaryKey = getIntent().getStringExtra(mPrimaryKeyExtra);

        // if there's no primaryKey, they're making a new Moment, so create a Moment and set its state to IN_PROGRESS
        if(primaryKey == null) {

            // make a new managed Moment
            mMoment = Moment.createMoment();

            // set the Moment's status to IN_PROGRESS
            mMoment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    mMoment.setEnumState(MomentStateEnum.IN_PROGRESS);

                }

            });

        }

        // otherwise they're editing a Moment.. find it
        else {

            mMoment = Moment.findMoment(primaryKey);

        }


        // get the RecyclerView. this holds everything in this activity but the toolbar
        mRecyclerView = (RecyclerView) findViewById(R.id.make_a_moment_recyclerView);

        // make a new mViewModelList
        mViewModelList = new ArrayList<Object>();

        // set up the RecyclerView
        setUpRecyclerView();

        // set up the mViewModelList
        setUpViewModelList();

        fillViewsFromMoment(mMoment);

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
                    submitMoment();

                    // set its status to UPLOADING
                    final MomentStateEnum momentState = MomentStateEnum.UPLOADING;
                    mMoment.persistUpdates(new PersistenceExecutor() {
                        @Override
                        public void execute() {

                            mMoment.setEnumState(momentState);

                        }
                    });

                    // signify the operation went through and send the primaryKey of the Moment back to the calling fragment
                    Intent MyMomentsIntent = new Intent(this, MyMomentsFragment.class);
                    MyMomentsIntent.putExtra(mPrimaryKeyExtra, mMoment.getPrimaryKey());
                    setResult(RESULT_OK, MyMomentsIntent);
                    finish();

                }


        }

        return true;

    }

    @Override
    public void onBackPressed() {
        // when the user hits back
        // delete the Moment that was being created if it doesn't have any fields

        // call the superclass's method
        super.onBackPressed();

        if(mMoment.getInterviewee() == null && mMoment.getTitle() == null && mMoment.getDescription() == null && mMoment.getLocalVideoFilePath() == null) {
            // the Moment doesn't have an interviewee, title, description, or video... delete it.

            mMoment.endItAll(this);

        }


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

    private void fillViewsFromMoment(Moment moment) {

        // if there's an interviewee, add the interviewee card
        if(moment.getInterviewee() != null) {

            insertInterviewingCard();

        }

        if(moment.getDescription() != null) {

            insertDescriptionCard();

        }

        if(moment.getLocalVideoFilePath() != null) {

            insertVideoCard();

        }

    }

    private void addNotesFromMoment(Moment moment) {
        // add all the Notes from the Moment to the mViewModelList

        RealmList<Note> notes = moment.getNotes();

        // add every Note to the mViewModelList
        for(Note note: notes) {

            mViewModelList.add(FIRST_NOTE_LOCATION, note.getNote());

        }

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
        if (mMoment.getTitle() != null && mMoment.getDescription() != null
                && mMoment.getInterviewee() != null && mMoment.getLocalVideoFilePath() != null) {

            return true;

        }

        else {

            return false;

        }

    }

    private void submitMoment() {
        // submits the Moment

        // deal with the uploading of the Moment
        mMoment.uploadMoment(this.getApplicationContext());

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called upon returning from another Activity called with an implicit Intent

        // make sure the operation went through without a hitch
        if(resultCode == RESULT_OK) {

            switch(requestCode) {
                // determine which implicit intent we're receiving from based on the resultCode

                case INTERVIEWING_INTENT:

                    // get the Strings returned from the InterviewingActivity
                    final String interviewee = data.getStringExtra(mIntervieweeExtra);
                    final String intervieweeRole = data.getStringExtra(mRoleExtra);
                    final String intervieweePhotoFile = data.getStringExtra(mIntervieweePhotoFileExtra);

                    // add these to the Moment and update them in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setInterviewee(interviewee);
                            mMoment.setIntervieweeRole(intervieweeRole);
                            mMoment.setIntervieweePhotoUri(intervieweePhotoFile);

                        }

                    });

                    // insert an interviewing_card in the mViewModelList
                    insertInterviewingCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case DESCRIPTION_INTENT:

                    // get the title and description from the DescriptionActivity
                    final String title = data.getStringExtra(mTitleExtra);
                    final String description = data.getStringExtra(mDescriptionExtra);

                    // set the Moment's fields and update them in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setTitle(title);
                            mMoment.setDescription(description);

                        }

                    });


                    // insert a description_card in place of section_prompt_text
                    insertDescriptionCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case NOTE_INTENT:
                    // a note String is being returned from NotesActivity. get it, add it to the mMoment, and refresh the Adapter

                    final String noteString = data.getStringExtra(mNoteExtra);

                    // make a new note and add it to realm and to the Moment
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            RealmList noteList = mMoment.getNotes();
                            Note note = new Note();
                            note.setNote(noteString);
                            noteList.add(0, note);

                        }

                    });

                    // add it to the viewlist
                    RealmList<Note> notes = mMoment.getNotes();
                    Note newNote = notes.get(0);

                    mViewModelList.add(FIRST_NOTE_LOCATION, newNote.getNote());

                    mMakeAMomentAdapter.notifyDataSetChanged();

                    break;


                case VIDEO_FROM_GALLERY:
                    // the user just fetched a video from the gallery. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    final String selectedVideoUri = data.getData().toString();

                    FileDealer fileDealer = new FileDealer();

                    final String filePath = fileDealer.getPath(this, Uri.parse(selectedVideoUri));

                    // update the Moment in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setLocalVideoFilePath(filePath);

                        }

                    });

                    // replace the section_prompt with a video_card
                    insertVideoCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case VIDEO_FROM_CAMERA:
                    // the user filmed a video. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    String filmedVideoUri = data.getData().toString();

                    // get a FileDealer
                    fileDealer = new FileDealer();

                    // get the path of the video from the FileDealer
                    final String filmedVideoPath = fileDealer.getPath(this, Uri.parse(filmedVideoUri));

                    // update the Moment in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setLocalVideoFilePath(filmedVideoPath);

                        }

                    });

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

                                // attach the Moment's interviewee, interviewee role, and interviewee photo uri
                                interviewingIntent.putExtra(mIntervieweeExtra, mMoment.getInterviewee());
                                interviewingIntent.putExtra(mRoleExtra, mMoment.getIntervieweeRole());
                                interviewingIntent.putExtra(mIntervieweePhotoFileExtra, mMoment.getIntervieweePhotoFile());

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

        // make an Intent with the DescriptionActivity
        Intent descriptionIntent = new Intent(getBaseContext(), DescriptionActivity.class);

        // add the Moment's title and description to the Intent
        descriptionIntent.putExtra(mTitleExtra, mMoment.getTitle());
        descriptionIntent.putExtra(mDescriptionExtra, mMoment.getDescription());

        // start the Activity
        startActivityForResult(descriptionIntent, DESCRIPTION_INTENT);

    }

    public void onVideoPromptClick() {
        // deal with loading a FilmVideoIntent, adding the localVideoUri to the Moment, and refreshing

        // if the API level > 21 then READ_EXTERNAL_STORAGE isn't automatic and should be requested
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Explain to the user why we need to read external storage

            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // express an implicit intent to film a video
        Intent filmVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        startActivityForResult(filmVideoIntent, VIDEO_FROM_CAMERA);


    }

    public void onUploadPromptClick() {
        // deal with loading a FindVideoIntent, adding the localVideoUri to the Moment, and refreshing

        // if the API level > 21 then READ_EXTERNAL_STORAGE isn't automatic and should be requested
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_FROM_GALLERY);

    }

    public void onNotesPromptClick() {
        // deal with acquiring a new note, adding it to the Moment, and refreshing the Adapter

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

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.actionBlue))
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
                                // user chose to delete... delete the note at the position from the mViewModelList and from the mMoment's noteList

                                // get the position where the notes begin
                                int notesBegin = (mViewModelList.size()) - (mMoment.getNotes().size());

                                // this is the index of the note in the mMoment noteList
                                final int noteListIndex = notePosition - notesBegin;

                                // delete the Note from the mMoment's noteList
                                mMoment.persistUpdates(new PersistenceExecutor() {

                                    @Override
                                    public void execute() {

                                        RealmList<Note> notes = mMoment.getNotes();
                                        notes.remove(noteListIndex);

                                    }

                                });

                                // delete it from the mViewModelList
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

        // add the mMoment's primaryKey as an extra
        videoViewActivityIntent.putExtra(mLocalVideoFileExtra, mMoment.getLocalVideoFilePath());

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

        // add the mIntervieweeRole if there is one
        if(mMoment.getIntervieweeRole() != null) {

            interviewingCardData.setIntervieweeRole(mMoment.getIntervieweeRole());

        }

        // add the intervieweePhotoUri if there is one
        if(mMoment.getIntervieweePhotoFile() != null) {

            File intervieweePhotoFile = new File(mMoment.getIntervieweePhotoFile());
            interviewingCardData.setIntervieweePhotoUri(Uri.fromFile(intervieweePhotoFile));

        }

        // replace the prompt with the InterviewingCardData
        mViewModelList.set(1, interviewingCardData);

        // tell the adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void insertDescriptionCard() {
        // inserts a filled out description_card in place of the corresponding section_prompt_text

        // mMoment is guaranteed to have the necessary fields so just fill out a DescriptionCardData
        DescriptionCardData descriptionCardData = new DescriptionCardData(mMoment.getTitle(), mMoment.getDescription());

        // replace the prompt with the DescriptionCardData
        mViewModelList.set(3, descriptionCardData);

        // tell the adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void insertVideoCard() {
        // inserts a filled out video_card in place of the corresponding section_prompt_text

        // mMoment is guaranteed to have the necessary field so just fill out a VideoCardData

        // get the video file
        File videoFile = new File(mMoment.getLocalVideoFilePath());

        // make a new VideoCardData with it
        VideoCardData videoCardData = new VideoCardData(Uri.fromFile(videoFile));

        // replace the prompt with the VideoCardData
        mViewModelList.set(5, videoCardData);

        // tell the Adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

}
