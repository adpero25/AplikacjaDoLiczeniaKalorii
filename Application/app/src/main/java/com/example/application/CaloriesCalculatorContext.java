package com.example.application;

import android.app.Application;
import android.content.Context;

public class CaloriesCalculatorContext extends Application {
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        CaloriesCalculatorContext.context = getApplicationContext();
    }
}
