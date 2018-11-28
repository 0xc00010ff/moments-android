package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.viewpagerindicator.CirclePageIndicator;

public class TermsAndConditionsActivity extends AppCompatActivity {

    Context mContext = this;

    // string for the shared preferences flag
    String mHasAgreedFlag;

    // ui references
    FrameLayout mAcceptFrameLayout;
    ViewPager mViewPager;
    CirclePageIndicator mPageIndicator;

    // Adapter
    TermsAndConditionsFragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        // get the name of the shared preferences flag
        mHasAgreedFlag = getString(R.string.has_agreed_to_terms_and_conditions);

        setup();

    }

    private void setup() {

        setupViewPager();

        setupAccept();

    }

    private void setupViewPager() {

        // get the ViewPager
        mViewPager = (ViewPager) findViewById(R.id.terms_and_conditions_activity_viewPager);

        // make a new FragmentPagerAdapter
        mPagerAdapter = new TermsAndConditionsFragmentPagerAdapter(getSupportFragmentManager());

        // get the PageIndicator
        mPageIndicator = (CirclePageIndicator) findViewById(R.id.terms_and_conditions_pageIndicator);

        // apply them all
        mViewPager.setAdapter(mPagerAdapter);
        mPageIndicator.setViewPager(mViewPager);

    }

    private void setupAccept() {

        // get the accept FrameLayout
        mAcceptFrameLayout = (FrameLayout) findViewById(R.id.terms_and_conditions_accept_frameLayout);

        // set an OnClickListener on it to respond to 'accept' clicks
        mAcceptFrameLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // show a dialog asking them to confirm
                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.terms_and_conditions_dialog_title)
                        .content(R.string.terms_and_conditions_dialog_content)
                        .positiveText(R.string.terms_and_conditions_positive_text)
                        .positiveColor(getResources().getColor(R.color.actionBlue))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // they've chosen to accept. set the SharedPreferences terms and conditions boolean to true

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

}
