package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rengwuxian.materialedittext.MaterialEditText;

public class CreateTopicActivity extends AppCompatActivity {

    // Strings for Extra argument identification
    String mTitleExtra, mDescriptionExtra;

    // the Activity title for display in the toolbar
    String mActivityTitle;

    // Strings for holding the information from the EditTexts
    String mTitle, mDescription;

    // ui references
    Toolbar mToolbar;
    MaterialEditText mTopicHeadlineEditText;
    MaterialEditText mTopicQuestionEditText;

    // "save" menu item
    MenuItem mSaveMenuItem;

    // a TextWatcher to monitor for if the save menuItem should be enabled/disabled
    TextWatcher mTextWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

        // fetch the Extra argument identifiers from resources
        mTitleExtra = getString(R.string.title_extra);
        mDescriptionExtra = getString(R.string.description_extra);

        // get the toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.create_topic_toolbar);
        mActivityTitle = getString(R.string.create_topic_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditTexts
        mTopicHeadlineEditText = (MaterialEditText) findViewById(R.id.create_topic_headline_editText);
        mTopicQuestionEditText = (MaterialEditText) findViewById(R.id.create_topic_question_editText);

        // build a TextWatcher
        mTextWatcher = buildTextWatcher();

        // add the TextWatcher to the EditTexts
        mTopicHeadlineEditText.addTextChangedListener(mTextWatcher);
        mTopicQuestionEditText.addTextChangedListener(mTextWatcher);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // get the MenuItem
        mSaveMenuItem = menu.findItem(R.id.save_menu_item);

        // disable it until the TextWatcher determines it should be enabled
        mSaveMenuItem.setEnabled(false);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from mDescriptionTitleEditText
                mTitle = mTopicHeadlineEditText.getText().toString();

                // get the text from mDescriptionDescriptionEditText
                mDescription = mTopicQuestionEditText.getText().toString();

                if(mTitle != null && mTitle.length() >= 1 && mDescription != null && mDescription.length() >= 1) {
                    // if the user has entered information for title and description

                    // make an intent with the MakeAMomentActivity
                    Intent topicIntent = new Intent(getBaseContext(), TopicActivity.class);

                    // add the mTitle and mDescription to the Intent
                    topicIntent.putExtra(mTitleExtra, mTitle);
                    topicIntent.putExtra(mDescriptionExtra, mDescription);

                    // set the result
                    setResult(RESULT_OK, topicIntent);

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

                if( mTopicHeadlineEditText.getText().length() > 0 && mTopicQuestionEditText.getText().length() > 0 ) {
                    // if both EditTexts now have text in them, then turn save white to indicate it's clickable

                    // set the Save button to white
                    mSaveMenuItem.setEnabled(true);

                }

                else if( mTopicHeadlineEditText.getText().length() == 0 || mTopicQuestionEditText.getText().length() == 0 ) {
                    // if either of the EditTexts now have no text in them, then turn save grey to indicate it's not clickable

                    // set the save button to grey
                    mSaveMenuItem.setEnabled(false);

                }

                if(mTopicHeadlineEditText.getText().length() > mTopicHeadlineEditText.getMaxCharacters()
                        || mTopicQuestionEditText.getText().length() > mTopicQuestionEditText.getMaxCharacters()) {
                    // if either EditTexts exceed their character limit, disable the mSaveMenuItem

                    mSaveMenuItem.setEnabled(false);

                }

            }

        };

        return textWatcher;

    }

}
