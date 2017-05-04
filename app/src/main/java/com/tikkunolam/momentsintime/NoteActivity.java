package com.tikkunolam.momentsintime;

import android.content.Intent;
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

import com.rengwuxian.materialedittext.MaterialEditText;

public class NoteActivity extends AppCompatActivity {

    // a tag for degugging purposes
    final String TAG = "NoteActivity";

    // the String for sending the intent extra arguments
    String mNoteExtra;

    // the Activity title for display in the toolbar
    String mActivityTitle;

    // the Moment this title and description are being added to
    Moment mMoment;

    // ui references
    Toolbar mToolbar;
    MaterialEditText mNoteEditText;

    // "save" menu item
    MenuItem mSaveMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // get the Extra argument identifiers from resources
        mNoteExtra = getString(R.string.note_extra);

        // get the toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.note_toolbar);
        mActivityTitle = getString(R.string.note_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditText
        mNoteEditText = (MaterialEditText) findViewById(R.id.note_editText);

        // build a TextWatcher and add it to the MaterialEditText to watch for changes and enable/disable save
        TextWatcher mTextWatcher = buildTextWatcher();
        mNoteEditText.addTextChangedListener(mTextWatcher);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // get the "save" MenuItem so it can be toggled by the TextWatcher
        mSaveMenuItem = menu.findItem(R.id.save_menu_item);

        // disable it to begin with
        mSaveMenuItem.setEnabled(false);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from mNoteEditText and add it to the Moment's notes
                String note = mNoteEditText.getText().toString();

                if(note != null && note.length() >= 1) {
                    // if the user has entered any information

                    // make an intent, add the note to it, and return to the calling Activity
                    Intent returnIntent = new Intent();

                    returnIntent.putExtra(mNoteExtra, note);

                    setResult(RESULT_OK, returnIntent);

                    finish();

                }

        }

        return true;
    }

    private TextWatcher buildTextWatcher() {
        // builds a TextWatcher to monitor the EditTexts and determine when to enable and disable mSaveMenuItem

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(mNoteEditText.getText().length() > 0) {
                    // if there is now any text in the MaterialEditText

                    // turn the save menu item white to indicate it is now clickable
                    mSaveMenuItem.setEnabled(true);
                    Log.d(TAG, "SAVE ENABLED");

                }

                else if(mNoteEditText.length() == 0) {
                    // if there's now no text in the MaterialEditText

                    // turn the save menu item grey to indicate it isn't clickable
                    mSaveMenuItem.setEnabled(true);
                    Log.d(TAG, "SAVE DISABLED");

                }

            }

        };

        return textWatcher;

    }

}
