package com.tikkunolam.momentsintime;

import android.app.Application;

import io.realm.Realm;

public class MomentsInTimeApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        // initialize Realm
        Realm.init(this);

    }

}
