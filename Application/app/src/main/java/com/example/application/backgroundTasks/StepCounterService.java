package com.example.application.backgroundTasks;

import static com.example.application.Activities.MainActivity.COUNTER_RESET;
import static com.example.application.Activities.MainActivity.STEP_COUNTER_KEY;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.application.Activities.CalculateCaloriesRequirement;
import com.example.application.Activities.MainActivity;
import com.example.application.Activities.ManuallyAddDailyRequirements;
import com.example.application.CaloriesCalculatorContext;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.repositories.DaysRepository;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Timer;
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

        Log.d("SERVICE_STARTED", "SERVICE STARTED");

        try {   // setting timer to reset counter

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDate now = LocalDate.now();
                Date date = dateFormatter.parse(now + " 23:50:00");

                int period = 24*60*60*1000; // period 24h
                Timer timer = new Timer(COUNTER_RESET);

                timer.schedule(new ResetService(), date, period);
            }
        }
        catch (Exception ignored) { }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadData();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "STEP_COUNTER_CHANNEL";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "STEP COUNTER CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("CCApplication")
                    .setContentText("CCApplication is now being used").build();

            startForeground(11, notification);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        saveData();
        return true;
    }

    @Override
    public void onDestroy() {
        saveData();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
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
                Log.d("STEPS", "Current Steps: " + (int) walk.stepsMade);
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
        else {
            DailyRequirements rq = GetUserData();
            if(rq != null)
                walk = new Walk(rq.weight, rq.height);
            else
                walk = new Walk();
        }
    }

    protected DailyRequirements GetUserData() {

        SharedPreferences userDataSharedPreferences = getSharedPreferences(ManuallyAddDailyRequirements.USER_DATA_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String savedData = userDataSharedPreferences.getString(ManuallyAddDailyRequirements.USER_DATA_KEY, null);

        if (savedData != null) {
            return new Gson().fromJson(savedData, DailyRequirements.class);
        }
        return null;
    }

    public synchronized Walk GetWalk() {
        if(walk == null)
            loadData();
        return this.walk;
    }

    public int GetWalkStepsAndReset() {
        if(walk == null)
            loadData();

        int stepsMade = (int) walk.stepsMade;
        SaveDataAndResetWalk();

        return stepsMade;
    }

    private void SaveDataAndResetWalk() {

        DaysRepository repo = new DaysRepository(CaloriesDatabase.getDatabase(CaloriesCalculatorContext.getAppContext()));
        int steps = (int) walk.stepsMade;
        double distance =  walk.calculateDistance();
        double calories =  walk.calculateBurnedCalories();

        repo.getOrCreateToday().thenAccept((day) -> {
            day.day.stepsCount += steps;
            day.day.totalDistance += distance;
            day.day.burnedCalories += calories;
            repo.update(day.day);
        } );

        resetWalk();
    }

    private synchronized void resetWalk() {
        walk.Reset();
        setWalkData();
        saveData();
        initSensor();
    }

    private void setWalkData() {
        DailyRequirements rq = GetUserData();
        if(rq != null)
        {
            walk.weight = rq.weight;
            walk.stepLength = (rq.height / 4) + 37;
        }
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
        public double stepLength = 71;  // step length in cm
        public double weight = 70;  // weight in kg
        public int stepsTarget = 5000;

        public Walk() { }
        public Walk(double _weight, double _height) {
            this.weight = _weight;
            this.stepLength = (_height / 4) + 37;
        }

        public void Reset() {
            startingStepsCounter += stepsMade;
            stepsMade = 0;
        }

        public double calculateDistance() {
            return (stepsMade * this.stepLength) / 100;
        }

        // Liczba kroków x długość kroku (km)) x 0,5 x waga osoby (kg)
        public double calculateBurnedCalories() { return stepsMade * stepLength / 100000 * 0.5 * weight; }
    }

    public class ResetService extends TimerTask {
        @Override
        public void run() {
            Log.d("STEP_COUNTER_RESET", "STEP COUNTER SERVICE RESET!");
            SaveDataAndResetWalk();
            initSensor();
        }
    }
}