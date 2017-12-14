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

    private PrivacyPolicyInteractionListener mListener;

    // ui references
    RelativeLayout mMainRelativeLayout;
    PDFView mPDFView;
    TextView mAcceptTextView;

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

        mPDFView = (PDFView) mMainRelativeLayout.findViewById(R.id.privacy_policy_fragment_pdfView);

        mAcceptTextView = (TextView) mMainRelativeLayout.findViewById(R.id.privacy_policy_fragment_accept_textView);

        setup();

        return mMainRelativeLayout;

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (context instanceof PrivacyPolicyInteractionListener) {

            mListener = (PrivacyPolicyInteractionListener) context;

        }

        else {

            throw new RuntimeException(context.toString()
                    + " must implement PrivacyPolicyInteractionListener");

        }

    }

    @Override
    public void onDetach() {

        super.onDetach();

        mListener = null;

    }

    private void setup() {

        setupAccept();

        showPrivacyPolicy();

    }

    // setup interaction with the Accept button
    private void setupAccept() {

        // set an OnClickListener on the Accept button to notify the Activity
        mAcceptTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // tell the parent Activity the accept button was clicked
                mListener.onPrivacyPolicyAccept();

            }

        });

    }

    // loads the privacy policy in the PDFView
    private void showPrivacyPolicy() {

        // get the name of the pdf asset from String resources
        String pdfName = mPDFView.getContext().getString(R.string.privacy_policy_name);

        // display it
        mPDFView.fromAsset(pdfName)
                .spacing(1) // in dp
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

    }

    // interface to be implemented by the parent Activity, to notify it of interactions
    public interface PrivacyPolicyInteractionListener {

        // when the User chooses to accept
        void onPrivacyPolicyAccept();

    }
}