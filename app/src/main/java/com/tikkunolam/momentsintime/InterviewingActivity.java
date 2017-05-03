package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;

public class InterviewingActivity extends AppCompatActivity {

    // tag for log statements
    final String TAG = "InterviewingActivity";

    // ui references
    Toolbar mToolbar;
    MaterialEditText mNameEditText;
    MaterialEditText mRoleEditText;
    RelativeLayout mPhotoViewRelativeLayout;
    ImageView mCameraImageView;
    ImageView mIntervieweeImageView;

    // "save" menu item
    MenuItem mSaveMenuItem;

    // string for the retrieved photo uri, to be returned to the MakeAMomentActivity
    String mPhotoUriString;

    // the Activity title for display in the Toolbar
    String mActivityTitle;

    // Strings for Extra argument identification
    String mNameExtra, mRoleExtra, mPhotoUriExtra;

    // request codes for implicit intent receipt
    final int PHOTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interviewing);

        // get the Extra argument identifiers from resources
        mNameExtra = getString(R.string.name_extra);
        mRoleExtra = getString(R.string.role_extra);
        mPhotoUriExtra = getString(R.string.photo_uri_extra);

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

        // get the mCameraImageView and set its onClick()
        mCameraImageView = (ImageView) findViewById(R.id.camera_imageView);
        mCameraImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // broadcast an implicit intent to retrieve a picture

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

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from the mNameEditText
                String name = mNameEditText.getText().toString();

                // get the text from the mRoleEditText
                String role = mRoleEditText.getText().toString();

                if (name != null && name.length() >= 1) {
                    // if the necessary information has been entered

                    // make an Intent with the MakeAMomentActivity
                    Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

                    // add the Strings
                    makeAMomentIntent.putExtra(mNameExtra, name).putExtra(mRoleExtra, role).putExtra(mPhotoUriExtra, mPhotoUriString);

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

                // get the photo's Uri from the Intent
                Uri photoUri = data.getData();

                // make the mPhotoViewRelativeLayout invisible
                mPhotoViewRelativeLayout.setVisibility(View.INVISIBLE);

                // make the mIntervieweeImageView visible
                mIntervieweeImageView.setVisibility(View.VISIBLE);

                // set it's image with the photoUri
                mIntervieweeImageView.setImageURI(photoUri);

                // make the mNameEditText be below the new image instead of the now invisible view
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mNameEditText.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.interviewee_imageView);
                mNameEditText.setLayoutParams(params);

                // set the mPhotoUriString for return to MakeAMomentActivity
                mPhotoUriString = photoUri.toString();


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
                    Log.d(TAG, "SAVE ENABLED");

                }

                else if(mNameEditText.getText().length() == 0) {
                    // if the EditText now has nothing in it

                    // change the color of the save menu to grey, to indicate it's not clickable
                    mSaveMenuItem.setEnabled(false);
                    Log.d(TAG, "SAVE DISABLED");

                }

            }

        };

        return textWatcher;

    }

}
