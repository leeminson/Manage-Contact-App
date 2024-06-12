package com.g1.contactapp;

import android.app.Application;
import android.content.Context;

import com.g1.contactapp.utils.AppDatabase;

public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized MyApp getInstance() {
        return instance;
    }

    public static AppDatabase getMyDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }
}
