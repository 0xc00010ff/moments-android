package com.tikkunolam.momentsintime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rengwuxian.materialedittext.MaterialEditText;

public class NoteActivity extends AppCompatActivity {

    // a tag for degugging purposes
    final String TAG = "NoteActivity";

    // the String for sending and receiving the intent extra arguments
    String momentExtra;

    // the Activity title for display in the toolbar
    String mActivityTitle;

    // the Moment this title and description are being added to
    Moment mMoment;

    // ui references
    Toolbar mToolbar;
    MaterialEditText mNoteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // get the intent extra argument name from resources
        momentExtra = getString(R.string.moment_extra);

        // get the Moment bundled in the Intent
        mMoment = getIntent().getExtras().getParcelable(momentExtra);

        // get the toolbar, get the activity title from resources, and set the toolbar title
        mToolbar = (Toolbar) findViewById(R.id.note_toolbar);
        mActivityTitle = getString(R.string.note_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mActivityTitle);

        // get the MaterialEditText
        mNoteEditText = (MaterialEditText) findViewById(R.id.note_editText);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.save_menu_item:
                // save was clicked

                // get the text from mNoteEditText and add it to the Moment's notes
                String note = mNoteEditText.getText().toString();
                mMoment.addNote(note);

                // make an intent with the MakeAMomentActivity
                Intent makeAMomentIntent = new Intent(getBaseContext(), MakeAMomentActivity.class);

                // add the Moment as an extra
                makeAMomentIntent.putExtra(momentExtra, mMoment);

                // start the Activity
                startActivity(makeAMomentIntent);

        }

        return true;
    }

}
