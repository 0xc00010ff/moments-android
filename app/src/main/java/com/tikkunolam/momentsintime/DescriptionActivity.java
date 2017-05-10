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

import io.realm.Realm;
import io.realm.RealmResults;

public class DescriptionActivity extends AppCompatActivity {

    // a tag for degugging purposes
    final String TAG = "DescriptionActivity";

    // Strings for Extra argument identification
    String mTitleExtra, mDescriptionExtra;

    // the Activity title for display in the toolbar
    String mActivityTitle;

    // Strings for holding the information from the EditTexts
    String mTitle, mDescription;

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
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);

        // set the mTitle and mDescription from the Intent extras if they were present
        mTitle = getIntent().getStringExtra(mTitleExtra);
        mDescription = getIntent().getStringExtra(mDescriptionExtra);

        // get the toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.description_toolbar);
        mActivityTitle = getString(R.string.description_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditTexts
        mDescriptionTitleEditText = (MaterialEditText) findViewById(R.id.description_title_editText);
        mDescriptionDescriptionEditText = (MaterialEditText) findViewById(R.id.description_description_editText);

        // add a TextWatcher to both MaterialEditTexts to toggle the "save" MenuItem if conditions are right
        TextWatcher mTextWatcher = buildTextWatcher();

        mDescriptionTitleEditText.addTextChangedListener(mTextWatcher);

        mDescriptionDescriptionEditText.addTextChangedListener(mTextWatcher);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // get the MenuItem
        mSaveMenuItem = menu.findItem(R.id.save_menu_item);

        // disable it until the TextWatcher determines it should be enabled
        mSaveMenuItem.setEnabled(false);

        // fill the EditTexts
        setViewsFromMoment();


        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from mDescriptionTitleEditText
                mTitle = mDescriptionTitleEditText.getText().toString();

                // get the text from mDescriptionDescriptionEditText
                mDescription = mDescriptionDescriptionEditText.getText().toString();

                if(mTitle != null && mTitle.length() >= 1 && mDescription != null && mDescription.length() >= 1) {
                    // if the user has entered information for title and description

                    // make an intent with the MakeAMomentActivity
                    Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

                    // add the mTitle and mDescription to the Intent
                    makeAMomentIntent.putExtra(mTitleExtra, mTitle);
                    makeAMomentIntent.putExtra(mDescriptionExtra, mDescription);

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

                if(mTitle != null && mDescription != null) {

                    mSaveMenuItem.setEnabled(true);

                }



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

                }

                else if( mDescriptionDescriptionEditText.getText().length() == 0 || mDescriptionTitleEditText.getText().length() == 0 ) {
                    // if either of the EditTexts now have no text in them, then turn save grey to indicate it's not clickable

                    // set the save button to grey
                    mSaveMenuItem.setEnabled(false);

                }

            }

        };

        return textWatcher;

    }

    public void setViewsFromMoment() {
        // fills the EditTexts from the Moment that's passed in if the values are present

        if(mTitle != null) {

            mDescriptionTitleEditText.setText(mTitle);

        }

        if(mDescription != null){

            mDescriptionDescriptionEditText.setText(mDescription);

        }

    }

}

