package com.tikkunolam.momentsintime;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.provider.ContactsContract.Contacts;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import io.realm.RealmList;

import static com.tikkunolam.momentsintime.R.string.primary_key_extra;

public class MakeAMomentActivity extends AppCompatActivity implements HolderInteractionListener{

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // Strings for use as Extra argument identifiers
    String mPrimaryKeyExtra, mNoteExtra, mLocalVideoFileExtra, mIntervieweeExtra, mRelationExtra, mIntervieweePhotoFileExtra,
            mTitleExtra, mDescriptionExtra;

    // integers for use as request codes between Intents
    final int VIDEO_FROM_GALLERY = 1;
    final int VIDEO_FROM_CAMERA = 2;
    final int INTERVIEWING_INTENT = 3;
    final int TOPIC_INTENT = 4;
    final int NOTE_INTENT = 5;
    final int INTERVIEWEE_FROM_CONTACTS = 6;

    // integers to specify which action requested READ_EXTERNAL_STORAGE permission
    final int UPLOAD = 1, FILM = 2, CONTACTS = 3;

    // positions in the mViewModelList
    final int INTERVIEWING_TITLE = 0, INTERVIEWING_SLOT = 1, TOPIC_TITLE = 2, TOPIC_SLOT = 3,
        VIDEO_TITLE = 4, VIDEO_SLOT = 5, NOTES_TITLE = 6, NOTES_PROMPT = 7, FIRST_NOTE_LOCATION = 8;

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

    // a boolean indicating whether the views should be clickable or not
    boolean mClickable = true;

    // a boolean to indicate whether a Contact was just selected
    boolean mGotContact = false;

    // the Uri to be returned from a contact fetch
    Uri uriContact;
    String mContactID;

    // String array that will hold the default notes to be supplied to every new Moment
    String[] defaultNotes;

    // int representing the position of the selected note
    int mSelectedNotePosition;

    // boolean signifying whether a note was just selected for editing
    boolean mEditingNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_moment);

        // get all the Extra argument names from resources
        mPrimaryKeyExtra = getString(primary_key_extra);
        mNoteExtra = getString(R.string.note_extra);
        mLocalVideoFileExtra = getString(R.string.local_video_file_extra);
        mIntervieweeExtra = getString(R.string.interviewee_extra);
        mRelationExtra = getString(R.string.interviewee_relation_extra);
        mIntervieweePhotoFileExtra = getString(R.string.interviewee_photo_file_extra);
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);

        // get the default notes
        defaultNotes = getResources().getStringArray(R.array.default_notes);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);

        // fetch the primaryKey that may have been passed in
        String primaryKey = getIntent().getStringExtra(mPrimaryKeyExtra);

        // if there's no primaryKey, they're making a new Moment, so create a Moment and set its state to PRIVATE
        if(primaryKey == null) {

            // make a new managed Moment
            mMoment = Moment.createMoment();

            // set the Moment's status to PRIVATE
            mMoment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    mMoment.setEnumState(MomentStateEnum.PRIVATE);

                }

            });

            // add the default notes to the Moment
            addDefaultNotes();

        }

        // otherwise they're editing a Moment.. find it
        else {

            mMoment = Moment.findMoment(primaryKey);

        }

        // determine if the views on screen should be clickable and set mClickable accordingly
        evaluateClickability();

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

        enableDisableMomentSubmission();

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
        mMakeAMomentAdapter = new MakeAMomentAdapter(this, mViewModelList, mClickable);

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
        String topic = getBaseContext().getResources().getString(R.string.topic);
        mTitles.add(new SectionTitle(topic));
        String video = getBaseContext().getResources().getString(R.string.video);
        mTitles.add(new SectionTitle(video));
        String notes = getBaseContext().getResources().getString(R.string.notes);
        mTitles.add(new SectionTitle(notes));

        // retrieve all the prompt strings and add them to mPrompts as SectionPrompts
        String interviewingPrompt = getBaseContext().getResources().getString(R.string.interviewing_prompt);
        mPrompts.add(new SectionPrompt(interviewingPrompt));
        String topicPrompt = getBaseContext().getResources().getString(R.string.add_topic_prompt);
        mPrompts.add(new SectionPrompt(topicPrompt));
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

            insertTopicCard();

        }

        if(moment.getLocalVideoFilePath() != null) {

            insertVideoCard();

        }

        // add what notes may already exist
        addNotesFromMoment(moment);

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

            if(mClickable) {

                // turn MenuItem white
                mSaveMenuItem.setEnabled(true);

            }

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
                    final String intervieweeRelation = data.getStringExtra(mRelationExtra);
                    final String intervieweePhotoFile = data.getStringExtra(mIntervieweePhotoFileExtra);

                    // add these to the Moment and update them in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setInterviewee(interviewee);
                            mMoment.setIntervieweeRelation(intervieweeRelation);
                            mMoment.setIntervieweePhotoFile(intervieweePhotoFile);

                        }

                    });

                    // insert an interviewing_card in the mViewModelList
                    insertInterviewingCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    if(mGotContact) {

                        // flip the indactor back to false, indicating any further return form InterviewingActivity...
                        // ...wasn't after selecting a contact
                        mGotContact = false;

                        // go through the invite motions
                        inviteContact();

                    }

                    break;

                case INTERVIEWEE_FROM_CONTACTS:
                    // the user just chose an interviewee from Contacts...
                    // get their name, relation, and photo file, set them on the Moment, and insertInterviewingCard

                    uriContact = data.getData();

                    // indicate that we just selected a contact
                    mGotContact = true;

                    // get the Contact's information, and save them to the Moment
                    retrieveContactInfo();

                    // make an intent with the InterviewingActivity
                    Intent interviewingIntent = new Intent(getBaseContext(), InterviewingActivity.class);

                    // attach the Moment's interviewee, interviewee relation, and interviewee photo uri
                    interviewingIntent.putExtra(mIntervieweeExtra, mMoment.getInterviewee());
                    interviewingIntent.putExtra(mRelationExtra, mMoment.getIntervieweeRelation());
                    interviewingIntent.putExtra(mIntervieweePhotoFileExtra, mMoment.getIntervieweePhotoFile());

                    startActivityForResult(interviewingIntent, INTERVIEWING_INTENT);

                    break;

                case TOPIC_INTENT:

                    // get the title and description from the TopicActivity
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


                    // insert a topic_card in place of section_prompt_text
                    insertTopicCard();

                    // enable or disable submission depending on mMoment's contents
                    enableDisableMomentSubmission();

                    break;

                case NOTE_INTENT:
                    // a note String is being returned from NotesActivity. get it, add it to the mMoment, and refresh the Adapter

                    final String noteString = data.getStringExtra(mNoteExtra);

                    // if we're returning from editing a note
                    if(mEditingNote) {
                        // replace the note with the new String

                        // flip the editing indicator back to false
                        mEditingNote = false;

                        // add it to the view list
                        mViewModelList.set(mSelectedNotePosition, noteString);

                        // get the position within the RealmList to update it
                        final int noteListPosition = mSelectedNotePosition - FIRST_NOTE_LOCATION;

                        // update the Note
                        mMoment.persistUpdates(new PersistenceExecutor() {

                            @Override
                            public void execute() {

                                RealmList<Note> notes = mMoment.getNotes();

                                Note note = notes.get(noteListPosition);

                                note.setNote(noteString);

                            }

                        });

                        mMakeAMomentAdapter.notifyDataSetChanged();

                    }

                    else {
                        // we're returning from making a new note, not editing one.

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

                    }

                    break;


                case VIDEO_FROM_GALLERY:
                    // the user just fetched a video from the gallery. get the uri and assign it to the mMoment
                    // replace the section_prompt with a video_card

                    // get the Uri from the Intent
                    final String selectedVideoUri = data.getData().toString();

                    FileDealer fileDealer = new FileDealer();

                    // get the path to the video we just retrieved
                    String oldFilePath = fileDealer.getPath(this, Uri.parse(selectedVideoUri));

                    // save it locally
                    final String localFilePath = fileDealer.saveVideoLocally(this, oldFilePath);

                    // update the Moment in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setLocalVideoFilePath(localFilePath);

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
                    String oldVideoPath = fileDealer.getPath(this, Uri.parse(filmedVideoUri));

                    // save it locally
                    final String newVideoPath = fileDealer.saveVideoLocally(this, oldVideoPath);

                    // update the Moment in Realm
                    mMoment.persistUpdates(new PersistenceExecutor() {

                        @Override
                        public void execute() {

                            mMoment.setLocalVideoFilePath(newVideoPath);

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

        final Context context = this;

        // if the Moment has an interviewee, then go straight to editing
        if(mMoment.getInterviewee() != null) {

            // make an intent with the InterviewingActivity
            Intent interviewingIntent = new Intent(getBaseContext(), InterviewingActivity.class);

            // attach the Moment's interviewee, interviewee relation, and interviewee photo uri
            interviewingIntent.putExtra(mIntervieweeExtra, mMoment.getInterviewee());
            interviewingIntent.putExtra(mRelationExtra, mMoment.getIntervieweeRelation());
            interviewingIntent.putExtra(mIntervieweePhotoFileExtra, mMoment.getIntervieweePhotoFile());

            startActivityForResult(interviewingIntent, INTERVIEWING_INTENT);

        }

        // otherwise give them the option to choose between contacts or entering manually
        else {

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.interviewing)
                    .items(R.array.interviewing_dialog_array)
                    .itemsColor(getResources().getColor(R.color.actionBlue))
                    .itemsCallback(new MaterialDialog.ListCallback() {

                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            // when a dialog option is selected these callbacks are used

                            switch(position) {

                                case 0:
                                    // they chose Contacts...

                                    // if their phone uses the new permission flow and READ_CONTACTS hasn't been granted
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                                            != PackageManager.PERMISSION_GRANTED) {

                                        // request the permission
                                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                                CONTACTS);
                                    }

                                    // otherwise we don't need to request permission so just open the Activity
                                    else {

                                        Intent contactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);

                                        startActivityForResult(contactIntent, INTERVIEWEE_FROM_CONTACTS);

                                    }

                                    break;

                                case 1:
                                    // they chose to make one manually

                                    // make an intent with the InterviewingActivity
                                    Intent interviewingIntent = new Intent(getBaseContext(), InterviewingActivity.class);

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

    }

    // the callback for when the description_prompt is clicked
    public void onTopicPromptClick() {
        // deal with acquiring a description/title, adding it to the Moment, and refreshing the Adapter

        // make an Intent with the TopicActivity
        Intent topicIntent = new Intent(this, TopicActivity.class);

        // start the Activity
        startActivityForResult(topicIntent, TOPIC_INTENT);

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

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILM);

        }

        else {
            // the user has already granted permission. go ahead and open the activity

            // express an implicit intent to film a video
            Intent filmVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            startActivityForResult(filmVideoIntent, VIDEO_FROM_CAMERA);

        }


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
                    UPLOAD);
        }

        // otherwise we don't need to request permission so just open the Activity
        else {

            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_FROM_GALLERY);

        }

    }

    public void onNotesPromptClick() {
        // deal with acquiring a new note, adding it to the Moment, and refreshing the Adapter

        // make an Intent with the NoteActivity
        Intent noteIntent = new Intent(getBaseContext(), NoteActivity.class);

        startActivityForResult(noteIntent, NOTE_INTENT);

    }

    public void onVideoDotsClick() {
        // open the dialog to ask the user to edit/delete the video

        final Context context = this;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.video_dialog_title)
                .items(R.array.video_dialog_array)
                .itemsColor(getResources().getColor(R.color.actionBlue))
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            case 0:
                                // user chose to edit
                                MaterialDialog editDialog = new MaterialDialog.Builder(context)
                                        .title(R.string.edit_video_title)
                                        .content(R.string.edit_video_content)
                                        .positiveText(R.string.edit_video_positive_text)
                                        .positiveColor(getResources().getColor(R.color.actionBlue))
                                        .negativeText(R.string.edit_video_delete_video)
                                        .negativeColor(getResources().getColor(R.color.red))
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                deleteVideo();

                                            }
                                        })
                                        .show();


                                break;

                            case 1:
                                // user chose to delete
                                deleteVideo();

                                break;

                        }

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.textLight))
                .show();

    }

    public void onNoteCardDotsClick(final int notePosition) {
        // open the dialog to ask the user to delete the note

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.note_dialog_title)
                .negativeText(getString(R.string.note_dialog_delete))
                .negativeColor(getResources().getColor(R.color.red))
                .onNegative(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

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

                    }

                })
                .positiveText(getString(R.string.dialog_cancel))
                .positiveColor(getResources().getColor(R.color.textLight))
                .show();


    }

    public void onNoteCardClick(int position) {
        // a note card was clicked so go to edit the note and remember that we're editing this particular note

        mSelectedNotePosition = position;

        mEditingNote = true;

        String note = (String) mViewModelList.get(position);

        Intent noteIntent = new Intent(this, NoteActivity.class);

        noteIntent.putExtra(mNoteExtra, note);

        startActivityForResult(noteIntent, NOTE_INTENT);

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

        // add the mIntervieweeRelation if there is one
        if(mMoment.getIntervieweeRelation() != null && !mMoment.getIntervieweeRelation().equals("")) {

            interviewingCardData.setIntervieweeRelation(mMoment.getIntervieweeRelation());

        }

        // add the intervieweePhotoUri if there is one
        if(mMoment.getIntervieweePhotoFile() != null) {

            File intervieweePhotoFile = new File(mMoment.getIntervieweePhotoFile());
            interviewingCardData.setIntervieweePhotoUri(Uri.fromFile(intervieweePhotoFile));

        }

        // replace the prompt with the InterviewingCardData
        mViewModelList.set(INTERVIEWING_SLOT, interviewingCardData);

        // tell the adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void insertTopicCard() {
        // inserts a filled out topic_card in place of the corresponding section_prompt_text

        // mMoment is guaranteed to have the necessary fields so just fill out a DescriptionCardData
        TopicCardData topicCardData = new TopicCardData(mMoment.getTitle(), mMoment.getDescription());

        // replace the prompt with the DescriptionCardData
        mViewModelList.set(TOPIC_SLOT, topicCardData);

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
        mViewModelList.set(VIDEO_SLOT, videoCardData);

        // tell the Adapter to update the list on screen
        mMakeAMomentAdapter.notifyDataSetChanged();

    }

    private void deleteVideo() {
        // the user chose to delete the video... delete it from realm and re-insert the video prompt

        // delete the video from the Moment
        mMoment.persistUpdates(new PersistenceExecutor() {

            @Override
            public void execute() {

                mMoment.setLocalVideoFilePath(null);

            }

        });

        // re-insert the video prompt
        String videoPrompt = getBaseContext().getResources().getString(R.string.video_prompt);
        mViewModelList.remove(VIDEO_SLOT);
        mViewModelList.add(VIDEO_SLOT, new SectionPrompt(videoPrompt));

        // re-load the RecyclerView
        mMakeAMomentAdapter.notifyDataSetChanged();


    }

    public void evaluateClickability() {

        if(mMoment.getMomentState() == MomentStateEnum.LIVE || mMoment.getMomentState() == MomentStateEnum.UPLOADING) {

            mClickable = false;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // called after a call to requestPermissions(). check here if they granted permission...
        // ...and if so, start the activity corresponding to the requestCode

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case UPLOAD:
                // the user wants to get a video from gallery. if permission granted, open that Activity.

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // they granted permission... open the Activity

                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Video"), VIDEO_FROM_GALLERY);

                }


                break;

            case FILM:
                // the user wants to film a video. if permission granted, open that Activity.

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // they granted permission... open the Activity

                    // express an implicit intent to film a video
                    Intent filmVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    startActivityForResult(filmVideoIntent, VIDEO_FROM_CAMERA);

                }

                break;

            case CONTACTS:
                // the user wants to read contacts. if permission granted, open that Activity.


                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // they granted permission... open the Activity

                    Intent contactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);

                    startActivityForResult(contactIntent, INTERVIEWEE_FROM_CONTACTS);

                }

                break;

        }
        
    }

    private void retrieveContactInfo() {
        // retrieves all the info we need from the Contact Uri returned from the contact intent

        // get the name
        retrieveContactName();

        // get the phone number
        retrieveContactPhoneNumber();

        // get the email
        retrieveContactEmail();

        // get the thumbnail photo
        retrieveContactPhoto();

    }

    private void retrieveContactName() {
        // get the Contact's name

        // get a Cursor to get the Contact's name
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // get the name of the Contact and save it to the Moment
            final String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            mMoment.persistUpdates(new PersistenceExecutor() {

                @Override
                public void execute() {

                    mMoment.setInterviewee(contactName);

                }

            });
        }

        cursor.close();

        // get a new cursor to get the Contact's ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            // get the Contact's ID so we can fetch other information with it
            mContactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));

        }

        cursorID.close();

    }

    public void retrieveContactPhoneNumber() {
        // get the Contact's phone number

        // get a Cursor to get the Contact
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        String phoneNumber = null;

        if (cursor != null && cursor.moveToFirst()) {

            // check that there is a phone number
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER);

            // will return a 1 if the contact has a number
            final String hasNumber = cursor.getString(numberIndex);

            // if there is a phone number
            if(hasNumber.equalsIgnoreCase("1")) {
                // get the phone number

                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ mContactID,
                        null, null);

                phones.moveToFirst();

                phoneNumber = phones.getString(phones.getColumnIndex("data1"));

                phones.close();

            }

            // we're returned a number with parentheses and dashes, remove everything that's not a digit
            if(phoneNumber != null) {

                final String number = phoneNumber.replaceAll("[^0-9]", "");

                mMoment.persistUpdates(new PersistenceExecutor() {

                    @Override
                    public void execute() {

                        mMoment.setIntervieweePhoneNumber(number);

                    }

                });

            }

        }

        cursor.close();

    }

    public void retrieveContactEmail() {

        // query for everything email
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,  null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { mContactID }, null);

        // index of the email data
        int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

        if (cursor.moveToFirst()) {

            // get the first email
            final String email = cursor.getString(emailIdx);

            if(email != null) {

                // save the email to the Moment
                mMoment.persistUpdates(new PersistenceExecutor() {

                    @Override
                    public void execute() {

                        mMoment.setIntervieweeEmail(email);

                    }

                });

            }

        }

        cursor.close();

    }


    public void retrieveContactPhoto() {

        // get the Contact data using the Uri found with the Contact's ID
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.valueOf(mContactID));

        // get the thumbnail picture
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);

        ByteArrayInputStream stream;

        Cursor cursor = getContentResolver().query(photoUri, new String[] {Contacts.Photo.PHOTO}, null, null, null);

        if(cursor != null) {

            try {

                if(cursor.moveToFirst()) {

                    byte[] data = cursor.getBlob(0);

                    if(data != null) {

                        // make a new input stream from the picture data
                        stream = new ByteArrayInputStream(data);

                        // make a bitmap with it
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);

                        // send it to the file dealer to make a file and get its path
                        FileDealer fileDealer = new FileDealer();

                        final String pictureFile = fileDealer.bitmapToFile(this, bitmap);

                        // save that path to the Moment
                        mMoment.persistUpdates(new PersistenceExecutor() {

                            @Override
                            public void execute() {

                                mMoment.setIntervieweePhotoFile(pictureFile);

                            }

                        });

                    }

                }

            }

            finally {

                cursor.close();

            }

        }

    }

    public void inviteContact() {
        // present the user with the options to share by text message and email. act on their choice.

        // build the dialog
        MaterialDialog interviewDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.contact_share_dialog_title, mMoment.getInterviewee()))
                .items(R.array.sms_and_email_share)
                .itemsColor(getResources().getColor(R.color.actionBlue))
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch(position) {

                            // user chose to share by text message
                            case 0:

                                Intent smsIntent;
                                Uri phoneNumberUri;

                                // if there is a phone number for the interviewee
                                if(mMoment.getIntervieweePhoneNumber() != null) {

                                    // make a Uri from the phone number
                                    phoneNumberUri = Uri.parse("smsto:" + mMoment.getIntervieweePhoneNumber());

                                }

                                else {
                                    // otherwise make a Uri with no phone number

                                    phoneNumberUri = Uri.parse("smsto:" + "");

                                }

                                // create the Intent with the phone number Uri
                                smsIntent = new Intent(Intent.ACTION_SENDTO, phoneNumberUri);

                                // attach the content of the message
                                smsIntent.putExtra("sms_body", getString(R.string.contact_invite_content_no_title, getString(R.string.contact_invite_store_link) + getPackageName()));

                                // express the sms Intent
                                startActivity(smsIntent);

                                break;

                            case 1:
                                // the user chose to share by email.

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("text/html");
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_invite_subject));

                                // if there is an email for the interviewee
                                if(mMoment.getIntervieweeEmail() != null) {

                                    // add the interviewee's email as an extra
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mMoment.getIntervieweeEmail()});

                                }

                                // attach the message content
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_invite_content_no_title, getString(R.string.contact_invite_store_link) + getPackageName()));

                                // express the email Intent
                                startActivity(emailIntent);

                                break;

                        }

                    }

                })
                .negativeText(getString(R.string.contact_share_negative_text))
                .negativeColor(getResources().getColor(R.color.textLight))
                .show();

    }

    public void addDefaultNotes() {

        for(int i = 0; i < defaultNotes.length; i++) {

            final String noteString = defaultNotes[i];

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

        }

    }


}
