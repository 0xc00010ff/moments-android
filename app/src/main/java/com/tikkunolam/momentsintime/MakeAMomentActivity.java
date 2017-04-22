package com.tikkunolam.momentsintime;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

public class MakeAMomentActivity extends AppCompatActivity {

    // tag for logging purposes
    final String TAG = "MakeAMomentActivity";

    // adapter for the RecyclerView
    NoteCardAdapter mNoteCardAdapter;

    // ui references
    Toolbar mToolbar;
    CoordinatorLayout makeAMomentCoordinatorLayout;
    RecyclerView noteCardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_moment);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);

        // get the main CoordinatorLayout
        makeAMomentCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.make_a_moment_coordinatorLayout);

        // get the RecyclerView for the notes
        noteCardRecyclerView = (RecyclerView) findViewById(R.id.note_card_recyclerView);

        /**
         * I'M GOING TO FAKE THIS FOR NOW I'LL CHANGE IT WHEN IT COMES DOWN TO IT
         */
        // make a fake moment and add a bunch of notes to it to see what's up
        Moment fakeMoment = new Moment();
        fakeMoment.addNote("hahahahahahahahahahahahahaha");
        fakeMoment.addNote("whatuuuuuuuuuuuuuuuuuuuuupppppppppppp");
        fakeMoment.addNote("throw me on the top");
        fakeMoment.addNote("hahahahahahahahahahahahahaha");
        fakeMoment.addNote("whatuuuuuuuuuuuuuuuuuuuuupppppppppppp");
        fakeMoment.addNote("throw me on the top");
        fakeMoment.addNote("hahahahahahahahahahahahahaha");
        fakeMoment.addNote("whatuuuuuuuuuuuuuuuuuuuuupppppppppppp");
        fakeMoment.addNote("throw me on the top");

        // create the adapter and set it on the RecyclerView
        mNoteCardAdapter = new NoteCardAdapter(getBaseContext(), R.id.note_cardView, fakeMoment.getNotes());
        noteCardRecyclerView.setAdapter(mNoteCardAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        noteCardRecyclerView.setLayoutManager(linearLayoutManager);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.make_a_moment_menu, menu);

        return true;
    }
}
