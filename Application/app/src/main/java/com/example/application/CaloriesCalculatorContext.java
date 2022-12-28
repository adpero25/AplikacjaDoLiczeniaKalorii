package com.example.application;

import android.app.Application;
import android.content.Context;

public class CaloriesCalculatorContext extends Application {
    private static Context context;


    public void onCreate(){
        super.onCreate();
        CaloriesCalculatorContext.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
