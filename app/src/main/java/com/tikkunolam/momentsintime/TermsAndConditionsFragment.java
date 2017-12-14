package com.tikkunolam.momentsintime;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TermsAndConditionsFragment extends Fragment {

    private TermsAndConditionsInteractionListener mListener;

    // ui references
    RelativeLayout mMainRelativeLayout;
    TextView mAcceptTextView;

    public TermsAndConditionsFragment() {
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
        mMainRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        mAcceptTextView = (TextView) mMainRelativeLayout.findViewById(R.id.terms_and_conditions_fragment_accept_textView);

        setup();

        return mMainRelativeLayout;

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (context instanceof TermsAndConditionsInteractionListener) {

            mListener = (TermsAndConditionsInteractionListener) context;

        }

        else {

            throw new RuntimeException(context.toString()
                    + " must implement TermsAndConditionsInteractionListener");

        }

    }

    @Override
    public void onDetach() {

        super.onDetach();

        mListener = null;

    }

    // setup the accept button
    private void setup() {

        // set an OnClickListener on the accept button
        mAcceptTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // alert the Activity that the terms were accepted
                mListener.onTermsAccept();

            }

        });

    }

    public interface TermsAndConditionsInteractionListener {

        void onTermsAccept();

    }
}
