package com.tikkunolam.momentsintime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class PrivacyPolicyFragment extends Fragment {

    // ui references
    RelativeLayout mMainRelativeLayout;
    PDFView mPDFView;
    Toolbar mToolbar;


    public PrivacyPolicyFragment() {
        // Required empty public constructor

    }

    public static PrivacyPolicyFragment newInstance() {

        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        // set up the Toolbar
        mToolbar = (Toolbar) mMainRelativeLayout.findViewById(R.id.privacy_policy_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle(mToolbar.getContext().getString(R.string.privacy_policy_toolbar_title));

        // show the privacy policy in the PDFView
        showPrivacyPolicy();

        return mMainRelativeLayout;

    }

    // loads the privacy policy in the PDFView
    private void showPrivacyPolicy() {

        mPDFView = (PDFView) mMainRelativeLayout.findViewById(R.id.privacy_policy_fragment_pdfView);

        // get the name of the pdf asset from String resources
        String pdfName = mPDFView.getContext().getString(R.string.privacy_policy_name);

        // display it
        mPDFView.fromAsset(pdfName)
                .spacing(1) // in dp
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

    }

}