package com.example.application.backgroundTasks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.application.Activities.MainActivity;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class StepCounterService extends Service {

    private static final String WALK_KEY = "WALK";
    private final IBinder binder = new LocalBinder(); // Binder given to clients
    SensorManager sensorManager = null;
    Sensor stepCounterSensor;
    private Walk walk;

    public StepCounterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadData();

        //the Date and time at which you want to execute counting reset
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long millis=System.currentTimeMillis();
            java.sql.Date dateFormat = new java.sql.Date(millis);
            Date date = dateFormatter.parse(  dateFormat + " 23:59:00");
            Timer timer = new Timer();
            long period =  24 * 60 * 60 * 1000; //24h in miliseconds
            timer.schedule(new ResetCounterTask(), date, period);
        }
        catch (Exception e) {
            Toast.makeText(this, "Couldn't set timer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (!walk.sensorSet) {
                    walk.startingStepsCounter = event.values[0];
                    walk.sensorSet = true;
                }

                walk.stepsMade = event.values[0] - walk.startingStepsCounter;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        }, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        saveData();
        serviceStopped();
    }

    private void serviceStopped() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.STEP_COUNTER_KEY, false);
        editor.apply();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(walk);
        editor.putString(WALK_KEY, json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String savedWalk = sharedPreferences.getString(WALK_KEY, null);
        if(savedWalk != null)
            walk = new Gson().fromJson(savedWalk, Walk.class);
        else
            walk = new Walk();
    }

    public Walk GetWalk() {
        if(walk == null)
            loadData();
        return this.walk;
    }

    public class LocalBinder extends Binder {
        // Return this instance of StepCounterService so clients can call public methods to get walk
        public StepCounterService getService() {
            return StepCounterService.this;
        }
    }

    public class Walk {
        public boolean sensorSet = false;
        public float stepsMade = 0f;
        public float startingStepsCounter;
    /*public int day;
    public int month;
    public int year;*/

/*

    public Walk() {
        day = LocalDateTime.now().toLocalDate().getDayOfMonth();
        month = LocalDateTime.now().toLocalDate().getMonthValue();
        year = LocalDateTime.now().toLocalDate().getYear();
    }
*/


        public void Reset() {
            startingStepsCounter += stepsMade;
            stepsMade = 0;
            //sensorSet = false;
            /*Locale userLocale = new Locale("pl");
            Calendar calendar = Calendar.getInstance(userLocale);
            day = calendar.get
            month = LocalDateTime.now().toLocalDate().getMonthValue();
            year = LocalDateTime.now().toLocalDate().getYear();*/
        }
    }

    private class ResetCounterTask extends TimerTask {
        public void run() {
            walk.Reset();
            saveData();
        }
    }
}