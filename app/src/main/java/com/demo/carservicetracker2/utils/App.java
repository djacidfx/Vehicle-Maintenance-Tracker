package com.demo.carservicetracker2.utils;

import android.app.Application;
import android.content.Context;


public class App extends Application {
    private static Context context;
    private static App mInstance;

    @Override 
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = this;
    }

    public static Context getContext() {
        return context;
    }

    public static App getInstance() {
        return mInstance;
    }
}
