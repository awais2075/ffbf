package com.android.ffbf.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


/**
 * Application Class Loads First
 * Specifically for Firebase Data Persistence i.e. Offline data management
 */
public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}