package com.example.application.backgroundTasks;

import android.app.AlarmManager;
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
import com.example.application.CaloriesCalculatorContext;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.Day;
import com.example.application.database.repositories.DaysRepository;
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

    public StepCounterService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSensor();

        try {
            Date d = new Date();
            if (d.getHours() == 0 && d.getMinutes() == 0)
                resetWalk();
        }
        catch (Exception e) { }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadData();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        saveData();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        saveData();
    }

    public void initSensor() {
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

    public void ResetWalk() {
        resetWalk();
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
        public float stepLength = 78.0f;  // step length in cm
        public float weight = 80.0f;  // weight in kg
        public int stepsTarget = 5000;

        public Walk() {
            // stepLength = height / 4 + 37;
            // weight =
        }

        public void Reset() {
            startingStepsCounter += stepsMade;
            stepsMade = 0;
        }

        public float calculateDistance() {

            return (stepsMade * this.stepLength) / 100;
        }

        public float calculateCaloriesBurnt() {
            // Liczba kroków x długość kroku (km)) x 0,5 x waga osoby (kg)
            return (float)(stepsMade * stepLength / 100000 * 0.5 * weight);
        }
    }

    private void resetWalk() {
        DaysRepository repo = new DaysRepository(CaloriesDatabase.getDatabase(CaloriesCalculatorContext.getAppContext()));

        repo.getOrCreateToday().thenAccept((day) -> {
            day.day.stepsCount += (int) walk.stepsMade;
            repo.update(day.day);
        } );

        walk.Reset();
        saveData();
        initSensor();
    }
}