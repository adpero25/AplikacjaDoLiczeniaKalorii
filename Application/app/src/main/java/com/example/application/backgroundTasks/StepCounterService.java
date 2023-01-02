package com.example.application.backgroundTasks;

import static android.os.SystemClock.elapsedRealtime;
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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.application.Activities.MainActivity;
import com.example.application.CaloriesCalculatorContext;
import com.example.application.broadcastReceivers.StepCounterBroadcastReceiver;
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

        Log.d("SERVICE_STARTED", "SERVICE STARTED");

        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDate now = LocalDate.now();
                Date date = dateFormatter.parse(now + " 23:59:00");

                int period = 24*60*60*1000;
                Timer timer = new Timer(COUNTER_RESET);

                timer.schedule(new ResetService(), date, period);
            }
        }
        catch (Exception ignored) { }

      /*
        try {
            Date d = new Date();
            if (d.getHours() == 23 && d.getMinutes() == 59) {
                Log.d("SCService", "Service Reset");
                SaveDataAndResetWalk();
                //SetAlarm();
            }

        }
        catch (Exception e) { }*/
        return START_STICKY;
    }

    private void SetAlarm() {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LocalDate now = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                now = LocalDate.now().plusDays(1);
            }
            Date date = dateFormatter.parse(now + " 23:59:00");

            Intent ishintent = new Intent(this, StepCounterBroadcastReceiver.class);
            PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, PendingIntent.FLAG_MUTABLE);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);
            alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pintent);
        }
        catch (Exception e) {}
    }

    private void SaveDataAndResetWalk() {

        DaysRepository repo = new DaysRepository(CaloriesDatabase.getDatabase(CaloriesCalculatorContext.getAppContext()));
        int steps = (int) walk.stepsMade;
        repo.getOrCreateToday().thenAccept((day) -> {
            day.day.stepsCount += steps;
            repo.update(day.day);
        } );

        resetWalk();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        loadData();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "STEP COUNTER CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(11, notification);
        }
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
        /*
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService( getApplicationContext(),
                1,
                restartServiceIntent,
                PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.setExact(AlarmManager.ELAPSED_REALTIME, elapsedRealtime() + 10000, restartServicePendingIntent);
        Log.d("TASK_REMOVED", "task removed ");
        */
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
        else
            walk = new Walk();
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

        resetWalk();

        return stepsMade;
    }

    private synchronized void resetWalk() {
        walk.Reset();
        saveData();
        initSensor();
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

    public class ResetService extends TimerTask {
        @Override
        public void run() {
            Log.d(STEP_COUNTER_KEY, "STEP COUNTER SERVICE RESET!");
            SaveDataAndResetWalk();
        }
    }
}