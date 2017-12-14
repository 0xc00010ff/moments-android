package com.tikkunolam.momentsintime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class TermsAndConditionsActivity extends AppCompatActivity  {

    Context mContext = this;

    // the currently displayed Fragment
    Fragment mFragment;

    // string for the shared preferences flag
    String mHasAgreedFlag;

    // ui references
    Toolbar mToolbar;
    FrameLayout mFragmentFrameLayout, mAcceptFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        // get the name of the shared preferences flag
        mHasAgreedFlag = getString(R.string.has_agreed_to_terms_and_conditions);

        // get the toolbar and set it as the action bar
        mToolbar = (Toolbar) findViewById(R.id.terms_and_conditions_toolbar);
        setSupportActionBar(mToolbar);

        // get the FrameLayout into which Fragments will be loaded
        mFragmentFrameLayout = (FrameLayout) findViewById(R.id.terms_and_conditions_activity_fragment_frameLayout);

        setup();

    }

    @Override
    public void onBackPressed() {

        // if the PrivacyPolicyFragment is displayed, show the TermsAndConditionsFragment
        if(mFragment instanceof PrivacyPolicyFragment) {

            swapToTermsAndConditions();

        }

        // otherwise don't do anything. there is no escape

    }

    private void setup() {

        showTermsAndConditions();

        setupAccept();

    }

    // show the TermsAndConditionsFragment for the first time (no slide-in animation)
    private void showTermsAndConditions() {

        // set the Activity title to Terms and Conditions
        String termsTitle = getString(R.string.terms_and_conditions_toolbar_title);
        getSupportActionBar().setTitle(termsTitle);

        // begin the FragmentTransaction
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // set the in-view Fragment to be a TermsAndConditionsFragment
        mFragment = new TermsAndConditionsFragment();

        // put the TermsAndConditionsFragment in the frame
        fragmentTransaction.add(mFragmentFrameLayout.getId(), mFragment);

        // commit the transaction
        fragmentTransaction.commit();

    }

    private void setupAccept() {

        // get the accept FrameLayout
        mAcceptFrameLayout = (FrameLayout) findViewById(R.id.terms_and_conditions_accept_frameLayout);

        // set an OnClickListener on it to respond to 'accept' clicks
        mAcceptFrameLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(mFragment instanceof TermsAndConditionsFragment) {

                    onTermsAccept();

                }

                else if(mFragment instanceof PrivacyPolicyFragment) {

                    onPrivacyPolicyAccept();

                }

            }

        });

    }

    // swap back to the TermsAndConditionsFragment from the PrivacyPolicyFragment (slide-in)
    private void swapToTermsAndConditions() {

        // set the Activity title to Terms and Conditions
        String termsTitle = getString(R.string.terms_and_conditions_toolbar_title);
        getSupportActionBar().setTitle(termsTitle);

        // begin the FragmentTransaction
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // set the animation on the transition to slide in from the left
        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_in_from_left, R.animator.fragment_slide_out_to_right);

        // set the mFragment to a TermsAndConditionsFragment
        mFragment = new TermsAndConditionsFragment();

        // put the TermsAndConditionsFragment in the frame
        fragmentTransaction.replace(mFragmentFrameLayout.getId(), mFragment);

        // commit the transaction
        fragmentTransaction.commit();

    }

    // slide to the PrivacyPolicyFragment
    private void swapToPrivacyPolicy() {

        // set the Activity title to Privacy Policy
        String privacyPolicyTitle = getString(R.string.privacy_policy_toolbar_title);
        getSupportActionBar().setTitle(privacyPolicyTitle);

        // begin the FragmentTransaction
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        // set the animation on the fragment transition to slide in from the right
        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_in_from_right, R.animator.fragment_slide_out_to_left);

        // set the mFragment to a PrivacyPolicyFragment
        mFragment = new PrivacyPolicyFragment();

        // put the PrivacyPolicyFragment in the frame
        fragmentTransaction.replace(mFragmentFrameLayout.getId(), mFragment);

        // commit the transaction
        fragmentTransaction.commit();

    }

    // when a user chose to accept the Terms and Conditions
    private void onTermsAccept() {

        // show a dialog asking them to confirm
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.terms_and_conditions_dialog_title)
                .content(R.string.terms_and_conditions_dialog_content)
                .positiveText(R.string.terms_and_conditions_positive_text)
                .positiveColor(getResources().getColor(R.color.actionBlue))
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // they've chosen to accept the terms. show the PrivacyPolicyFragment

                    swapToPrivacyPolicy();

                    }

                })
                .negativeText(R.string.terms_and_conditions_negative_text)
                .negativeColor(getResources().getColor(R.color.red))
                .show();

    }

    // when a user chose to accept the Privacy Policy
    private void onPrivacyPolicyAccept() {

        // show a dialog asking them to confirm
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.privacy_policy_dialog_title)
                .content(R.string.privacy_policy_dialog_content)
                .positiveText(R.string.privacy_policy_positive_text)
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
                .negativeText(R.string.privacy_policy_negative_text)
                .negativeColor(getResources().getColor(R.color.red))
                .show();

    }

}
