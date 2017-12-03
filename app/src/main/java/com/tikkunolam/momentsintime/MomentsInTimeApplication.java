package com.tikkunolam.momentsintime;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class MomentsInTimeApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // initialize Realm
        Realm.init(this);

    }

}
