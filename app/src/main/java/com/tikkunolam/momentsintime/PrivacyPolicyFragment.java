package com.tikkunolam.momentsintime;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class PrivacyPolicyFragment extends Fragment {

    // ui references
    RelativeLayout mMainRelativeLayout;
    PDFView mPDFView;

    public PrivacyPolicyFragment() {
        // Required empty public constructor

    }

    public static TermsAndConditionsFragment newInstance() {

        TermsAndConditionsFragment fragment = new TermsAndConditionsFragment();

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