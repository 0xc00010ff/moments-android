package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rengwuxian.materialedittext.MaterialEditText;

public class DescriptionActivity extends AppCompatActivity {

    // a tag for degugging purposes
    final String TAG = "DescriptionActivity";

    // Strings for Extra argument identification
    String mPrimaryKeyExtra;

    // the Activity title for display in the toolbar
    String mActivityTitle;

    // the Moment passed by the MakeAMomentActivity
    Moment mMoment;

    // ui references
    Toolbar mToolbar;
    MaterialEditText mDescriptionTitleEditText;
    MaterialEditText mDescriptionDescriptionEditText;

    // "save" menu item
    MenuItem mSaveMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // fetch the Extra argument identifiers from resources
        mPrimaryKeyExtra = getString(R.string.primary_key_extra);

        // get the primaryKey from the Intent to get the Moment from Realm
        String primaryKey = getIntent().getStringExtra(mPrimaryKeyExtra);

        // get the Moment from Realm with the key

        // get the toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.description_toolbar);
        mActivityTitle = getString(R.string.description_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditTexts
        mDescriptionTitleEditText = (MaterialEditText) findViewById(R.id.description_title_editText);
        mDescriptionDescriptionEditText = (MaterialEditText) findViewById(R.id.description_description_editText);

        // fill them in with values from the mMoment if they're present
        setViewsFromMoment();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // get the MenuItem
        mSaveMenuItem = menu.findItem(R.id.save_menu_item);

        // disable it until the TextWatcher determines it should be enabled
        mSaveMenuItem.setEnabled(false);

        // add a TextWatcher to both MaterialEditTexts to toggle the "save" MenuItem if conditions are right
        TextWatcher mTextWatcher = buildTextWatcher();

        mDescriptionTitleEditText.addTextChangedListener(mTextWatcher);

        mDescriptionDescriptionEditText.addTextChangedListener(mTextWatcher);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from mDescriptionTitleEditText
                String title = mDescriptionTitleEditText.getText().toString();

                // get the text from mDescriptionDescriptionEditText
                String description = mDescriptionDescriptionEditText.getText().toString();

                if(title != null && title.length() >= 1 && description != null && description.length() >= 1) {
                    // if the user has entered information for title and description

                    // add the title and description to the Moment
                    mMoment.setTitle(title);
                    mMoment.setDescription(description);

                    // update the Moment in Realm

                    // make an intent with the MakeAMomentActivity
                    Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

                    // set the result
                    setResult(RESULT_OK, makeAMomentIntent);

                    // return to the calling Activity
                    finish();

                }

                break;

        }

        return true;
    }

    private TextWatcher buildTextWatcher() {
        // build a TextWatcher to watch the EditTexts and determine if the mSaveMenuItem should be enabled

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if( mDescriptionTitleEditText.getText().length() > 0 && mDescriptionDescriptionEditText.getText().length() > 0 ) {
                    // if both EditTexts now have text in them, then turn save white to indicate it's clickable

                    // set the Save button to white
                    mSaveMenuItem.setEnabled(true);
                    Log.d(TAG, "SAVE ENABLED");

                }

                else if( mDescriptionDescriptionEditText.getText().length() == 0 || mDescriptionTitleEditText.getText().length() == 0 ) {
                    // if either of the EditTexts now have no text in them, then turn save grey to indicate it's not clickable

                    // set the save button to grey
                    mSaveMenuItem.setEnabled(false);
                    Log.d(TAG, "SAVE DISABLED");

                }

            }

        };

        return textWatcher;

    }

    public void setViewsFromMoment() {
        // fills the EditTexts from the Moment that's passed in if the values are present

        if(mMoment.getTitle() != null) {

            mDescriptionTitleEditText.setText(mMoment.getTitle());

        }

        if(mMoment.getDescription() != null){

            mDescriptionDescriptionEditText.setText(mMoment.getDescription());

        }

    }

}

