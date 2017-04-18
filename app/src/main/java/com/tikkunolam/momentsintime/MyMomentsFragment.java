package com.tikkunolam.momentsintime;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyMomentsFragment extends Fragment {

    private final String TAG = "My Moments Fragment";

    VimeoNetworker networker;

    public MyMomentsFragment() {

        // Required empty public constructor

    }

    public static MyMomentsFragment newInstance() {

        MyMomentsFragment fragment = new MyMomentsFragment();

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and return it
        return inflater.inflate(R.layout.fragment_my_moments, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

}
