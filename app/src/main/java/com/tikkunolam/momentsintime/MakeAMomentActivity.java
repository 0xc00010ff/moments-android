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

    // ui references
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    // adapter for the RecyclerView that fills the whole Activity
    MakeAMomentAdapter mMakeAMomentAdapter;

    // a Moment to be filled by the user or loaded from disk
    Moment mMoment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_moment);

        // get the toolbar and set it
        mToolbar = (Toolbar) findViewById(R.id.make_a_moment_toolbar);
        setSupportActionBar(mToolbar);

        // get the RecyclerView. this holds everything in this activity but the toolbar
        mRecyclerView = (RecyclerView) findViewById(R.id.make_a_moment_recyclerView);

        // make a new Moment. to be filled with values from disk, or by the user
        mMoment = new Moment();

        // make the mMakeAMomentAdapter and set it on the RecyclerView
        mMakeAMomentAdapter = new MakeAMomentAdapter(this, mMoment);
        mRecyclerView.setAdapter(mMakeAMomentAdapter);

        // set a LinearLayoutManager on the mRecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and applies it to the toolbar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.make_a_moment_menu, menu);

        return true;
    }
}
