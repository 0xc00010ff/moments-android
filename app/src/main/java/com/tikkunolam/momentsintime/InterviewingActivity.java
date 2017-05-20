package com.tikkunolam.momentsintime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;

import io.realm.Realm;

public class InterviewingActivity extends AppCompatActivity {

    // tag for log statements
    final String TAG = "InterviewingActivity";


    Context mContext = this;
    // ui references
    Toolbar mToolbar;
    MaterialEditText mNameEditText;
    MaterialEditText mRoleEditText;
    RelativeLayout mPhotoViewRelativeLayout;
    ImageView mCameraImageView;
    ImageView mIntervieweeImageView;
    View mCircleContainerView;
    TextView mAddAPictureTextView;

    // "save" menu item
    MenuItem mSaveMenuItem;

    // the Activity title for display in the Toolbar
    String mActivityTitle;

    // Strings for Extra argument identification
    String mIntervieweeExtra, mIntervieweeRoleExtra, mIntervieweePhotoFileExtra;

    // Strings for holding the values set within this Activity
    String mIntervieweeName;
    String mIntervieweeRole;
    String mIntervieweePhotoFile;

    // request codes for implicit intent receipt
    final int PHOTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interviewing);

        // get the Extra argument identifiers from resources
        mIntervieweeExtra = getString(R.string.interviewee_extra);
        mIntervieweeRoleExtra = getString(R.string.interviewee_role_extra);
        mIntervieweePhotoFileExtra = getString(R.string.interviewee_photo_file_extra);

        // get the mIntervieweeName, mIntervieweeDescription, and mIntervieweePhotoFile if they were passed in
        mIntervieweeName = getIntent().getStringExtra(mIntervieweeExtra);
        mIntervieweeRole = getIntent().getStringExtra(mIntervieweeRoleExtra);
        mIntervieweePhotoFile = getIntent().getStringExtra(mIntervieweePhotoFileExtra);

        // get the Toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.interviewing_toolbar);
        mActivityTitle = getString(R.string.interviewing_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditTexts
        mNameEditText = (MaterialEditText) findViewById(R.id.name_editText);
        mRoleEditText = (MaterialEditText) findViewById(R.id.role_editText);

        // get a TextWatcher and add it to the mNameEditText to indicate save is clickable if there is text
        TextWatcher mTextWatcher = buildTextWatcher();
        mNameEditText.addTextChangedListener(mTextWatcher);

        // get the views dealing with the picture
        mPhotoViewRelativeLayout = (RelativeLayout) findViewById(R.id.photo_view_relativeLayout);
        mIntervieweeImageView = (ImageView) findViewById(R.id.interviewee_imageView);
        mCameraImageView = (ImageView) findViewById(R.id.camera_imageView);
        mCircleContainerView = findViewById(R.id.circle_container);

        // get the mAddAPictureTextView and set its onClick()
        mAddAPictureTextView = (TextView) findViewById(R.id.add_picture_textView);
        mAddAPictureTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // broadcast an implicit intent to retrieve a picture

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

                // make a new Intent
                Intent photoIntent = new Intent();

                // set it to receive images
                photoIntent.setType("image/*");
                photoIntent.setAction(Intent.ACTION_GET_CONTENT);

                // start the Activity
                startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);

            }

        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        mSaveMenuItem = menu.findItem(R.id.save_menu_item);
        mSaveMenuItem.setEnabled(false);

        // fill the views with values from the mMoment passed in, if there are any
        // adding some views affects the MenuItem so it must be done after the Item is instantiated in the line above
        setViewsFromIntent();

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from the mNameEditText
                mIntervieweeName = mNameEditText.getText().toString();

                // get the text from the mRoleEditText
                mIntervieweeRole = mRoleEditText.getText().toString();

                if (mIntervieweeName != null && mIntervieweeName.length() >= 1) {
                    // if the necessary information has been entered

                    // make an Intent with the MakeAMomentActivity
                    Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

                    // add all the fields' values to it
                    makeAMomentIntent.putExtra(mIntervieweeExtra, mIntervieweeName);
                    makeAMomentIntent.putExtra(mIntervieweeRoleExtra, mIntervieweeRole);
                    makeAMomentIntent.putExtra(mIntervieweePhotoFileExtra, mIntervieweePhotoFile);

                    // signal that the results are okay and attach the Intent
                    setResult(RESULT_OK, makeAMomentIntent);

                    // return to the the MakeAMomentActivity
                    finish();

                }

                break;

        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called upon return to the Activity from whatever an implicit Intent did

        if(resultCode == RESULT_OK) {
            // the user went through with it and chose a picture

            if(requestCode == PHOTO_REQUEST_CODE) {
                // it's returning from selecting a photo. save it to mMoment and change the views to display it

                // get the photo's Uri from the Intent and set the mIntervieweePhotoUri with it
                Uri photoUri = data.getData();

                FileDealer fileDealer = new FileDealer();

                mIntervieweePhotoFile = fileDealer.getPath(this, photoUri);

                // add the picture to the screen
                replaceWithPicture();

            }

        }

        else {
            // the user didn't go through with it... do nothing!


        }

    }

    private TextWatcher buildTextWatcher() {
        // build a TextWatcher to watch the EditTexts and determine if mSaveMenuItem should be enabled

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // if the mMoment has an interviewee, the user is coming back to edit and the MenuItem should start off enabled
                if(mIntervieweeName != null) {

                    mSaveMenuItem.setEnabled(true);

                }
                else {

                    mSaveMenuItem.setEnabled(false);

                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(mNameEditText.getText().length() > 0) {
                    // if the EditText now has some text in it

                    // change the color of the save menu item to white, to indicate it's clickable
                    mSaveMenuItem.setEnabled(true);

                }

                else if(mNameEditText.getText().length() == 0) {
                    // if the EditText now has nothing in it

                    // change the color of the save menu to grey, to indicate it's not clickable
                    mSaveMenuItem.setEnabled(false);

                }

            }

        };

        return textWatcher;

    }

    public void setViewsFromIntent() {
        // fills the views with views from values in the Moment being passed in, if there are any

        if(mIntervieweeName != null) {

            mNameEditText.setText(mIntervieweeName);

        }

        if(mIntervieweeRole != null) {

            mRoleEditText.setText(mIntervieweeRole);

        }

        if(mIntervieweePhotoFile != null) {

            replaceWithPicture();

        }

    }

    public void replaceWithPicture() {
        // replaces the mCameraImageView and mCircleView with the mIntervieweeImageView

        // make the circle and camera invisible
        mCircleContainerView.setVisibility(View.GONE);
        mCameraImageView.setVisibility(View.GONE);

        // make the mIntervieweeImageView visible
        mIntervieweeImageView.setVisibility(View.VISIBLE);

        File imageFile = new File(mIntervieweePhotoFile);

        // set the image on the ImageView
        mIntervieweeImageView.setImageURI(Uri.fromFile(imageFile));

    }



}
