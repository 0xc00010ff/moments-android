package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class TermsAndConditionsActivity extends AppCompatActivity {

    Context mContext = this;

    // string for the shared preferences flag
    String mHasAgreedFlag;

    // ui references
    Toolbar mToolbar;
    TextView mAcceptTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        // get the name of the shared preferences flag
        mHasAgreedFlag = getString(R.string.has_agreed_to_terms_and_conditions);

        // get the toolbar, set its title, and set it as the action bar
        mToolbar = (Toolbar) findViewById(R.id.terms_and_conditions_toolbar);
        String activityTitle = getString(R.string.terms_and_conditions_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(activityTitle);

        // get the accept TextView and set its onClick
        mAcceptTextView = (TextView) findViewById(R.id.accept_textView);
        mAcceptTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.terms_and_conditions_dialog_title)
                        .content(R.string.terms_and_conditions_dialog_content)
                        .positiveText(R.string.terms_and_conditions_positive_text)
                        .positiveColor(getResources().getColor(R.color.actionBlue))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // they've chosen to accept. set the shared preference boolean to true

                                // get the shared preferences
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                                // set the boolean to true
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(mHasAgreedFlag, true);
                                editor.commit();

                                // return to the MainActivity
                                Intent mainActivityIntent = new Intent(mContext, MainActivity.class);
                                setResult(RESULT_OK, mainActivityIntent);
                                finish();

                            }

                        })
                        .negativeText(R.string.terms_and_conditions_negative_text)
                        .negativeColor(getResources().getColor(R.color.red))
                        .show();

            }

        });

    }

    @Override
    public void onBackPressed() {
        // do nothing. the user should not be able to go back
    }

}
