package com.example.application.Activities;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.CaloriesCalculatorContext;
import com.example.application.R;
import com.example.application.backgroundTasks.NotifyAboutWater;
import com.example.application.backgroundTasks.StepCounterService;
import com.example.application.broadcastReceivers.WaterBroadcastReceiver;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.repositories.DaysRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_PERMISSION = 100;
    private static int stepsTarget = 5000;
    private boolean stepCounterServiceBound = false;
    public static final int CALCULATE_DAILY_REQUIREMENTS_REQUEST = 1;
    public static final String SHARED_PREFERENCES_FILE_NAME = "CaloriesCalculatorPreferences";
    public static final String WATER_GLASSES_KEY = "water_glasses";
    public static final String STEP_COUNTER_KEY = "service_started";
    public static final String CHANNEL_ID = "WATER_CHANNEL_ID";

    TextView waterLabel;
    TextView totalStepsTextView;
    TextView totalDistanceTextView;
    TextView totalCaloriesBurntTextView;
    StepCounterService.Walk walk;
    StepCounterService scService;
    Button registerWaterButton;
    ProgressBar waterProgressBar;
    ProgressBar stepProgress;
    Button addDailyWaterRequirement;
    Button setStepTarget;
    public int dailyGlassesOfWater = 0;
    public static final int waterNotification = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dismissNotification();
        if(!isServiceRunning(NotifyAboutWater.class)){
            startWaterNotificationService();
        }

        createNotificationChannel();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

        waterLabel = findViewById(R.id.howManyWaterToday);
        registerWaterButton = findViewById(R.id.registerWater);
        waterProgressBar = findViewById(R.id.waterProgress);
        stepProgress = findViewById(R.id.stepsProgress);
        stepProgress.setMax(stepsTarget);
        addDailyWaterRequirement = findViewById(R.id.dailyWaterRequirement);
        setStepTarget = findViewById(R.id.dailyStepTarget);
        totalStepsTextView = findViewById(R.id.howManyStepsToday);
        totalDistanceTextView = findViewById(R.id.distanceToday);
        totalCaloriesBurntTextView = findViewById(R.id.caloriesBurnedToday);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        ACTIVITY_PERMISSION);
            }
            //serviceStopped();
            startStepCounterService();
        }
        else {
            ConstraintLayout stepCounterContainer = findViewById(R.id.stepsContainer);
            stepCounterContainer.setMaxHeight(0);
        }

        Button scanProduct = findViewById(R.id.scanCodeBTN);
        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddingServingActivity.class);
                startActivity(intent);
            }
        });

        Button loadDay = findViewById(R.id.loadDayBTN);
        loadDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaysRepository repo = new DaysRepository(getApplication());

                repo.getOrCreateToday().thenAccept((day)->{
                    TextView textView = findViewById(R.id.textView);
                    StringBuilder sb = new StringBuilder();

                    //dlaczego StringBuilder nie ma metody appendLine()?
                    //dlaczego java nie ma extension methods żebyśmy se sami ją dodali?
                    //dlaczego wyciąganie znaku nowej lini wymaga użycia stringa?
                    sb.append("day_id = ");
                    sb.append(day.day.dayId);
                    sb.append(System.getProperty("line.separator"));
                    sb.append("glasses_of_water = ");
                    sb.append(day.day.glassesOfWater);
                    textView.setText(sb);
                });
                CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplication());
                List<DayWithDailyRequirementsAndServings> tmp =  db.dailyRequirementsDao().getDayWithServingsWithDailyRequirements();

            }
        });


        Button addGlassOfWater = findViewById(R.id.addGlassOfWaterBTN);
        addGlassOfWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaysRepository repo = new DaysRepository(getApplication());

                repo.getOrCreateToday().thenAccept((day)->{
                    day.day.glassesOfWater += 1;
                    repo.update(day.day);
                });
                updateWaterLabel();
            }
        });

        Button calculateRequirement = findViewById(R.id.calculateRequirementBTN);
        calculateRequirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalculateCaloriesRequirement.class);
                startActivityForResult(intent, CALCULATE_DAILY_REQUIREMENTS_REQUEST);
            }
        });

        registerWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView text = popupView.findViewById(R.id.popupText);
                EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                text.setText(R.string.addWaterAmountLabel);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                submitButton.setOnClickListener(view ->{
                    if(enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())){
                        popupWindow.dismiss();
                        return;
                    }
                    int value = Integer.parseInt(enteredValue.getText().toString());
                    DaysRepository repo = new DaysRepository(getApplication());

                    repo.getOrCreateToday().thenAccept((day) -> {
                        day.day.glassesOfWater += value;
                        repo.update(day.day);
                    });
                    popupWindow.dismiss();
                    updateWaterLabel();
                });


            }
        });

        addDailyWaterRequirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView text = popupView.findViewById(R.id.popupText);
                EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                text.setText(R.string.setDailyWaterRequirement);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                submitButton.setOnClickListener(view ->{
                    if(enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())){
                        popupWindow.dismiss();
                        return;
                    }
                    dailyGlassesOfWater = Integer.parseInt(enteredValue.getText().toString());
                    updateWaterLabel();
                    popupWindow.dismiss();
                });

            }
        });

        setStepTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView text = popupView.findViewById(R.id.popupText);
                EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                text.setText(R.string.setDailyStepTarget);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                submitButton.setOnClickListener(view -> {
                    if(enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())){
                        popupWindow.dismiss();
                        return;
                    }

                    walk.stepsTarget = Integer.parseInt(enteredValue.getText().toString());
                    stepProgress.setMax(walk.stepsTarget);

                    popupWindow.dismiss();
                });
            }
        });

        setStepTarget.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int stepsMade = scService.GetWalkStepsAndReset();

                DaysRepository repo = new DaysRepository(CaloriesDatabase.getDatabase(CaloriesCalculatorContext.getAppContext()));

                repo.getOrCreateToday().thenAccept((day) -> {
                    day.day.stepsCount += stepsMade;
                    repo.update(day.day);
                } );

                return true;
            }
        });

        updateWaterLabel();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Bind to StepCounterService
            Intent intent = new Intent(this, StepCounterService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        preferencesEditor.putInt(WATER_GLASSES_KEY, dailyGlassesOfWater);
        preferencesEditor.apply();

        scService.saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void updateWaterLabel(){
        DaysRepository repo = new DaysRepository(getApplication());

        repo.getOrCreateToday().thenAccept((day) -> {

            waterLabel.setText(getString(R.string.glassesOfWater, day.day.glassesOfWater));
            //updateProgressBar

            if(dailyGlassesOfWater > 0){
                waterProgressBar.setMax(dailyGlassesOfWater);
                waterProgressBar.setProgress(day.day.glassesOfWater);
            }

        });
    }

    private void dismissNotification(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.cancel(waterNotification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startWaterNotificationService(){
        Intent intent = new Intent(this, NotifyAboutWater.class);
            startService(intent);
    }

    private void startStepCounterService() {
        startService(new Intent(this, StepCounterService.class));

        try {
/*            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 50);
            calendar.set(Calendar.SECOND, 0);*/


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDate now = LocalDate.now();
                Date date = dateFormatter.parse(now + " 23:59:00");

                Intent ishintent = new Intent(this, StepCounterService.class);
                PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, PendingIntent.FLAG_MUTABLE);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.cancel(pintent);
                alarm.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pintent);
            }
        }
        catch (Exception e) {
            int c = 1;
        }
    }

    private void serviceStarted() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STEP_COUNTER_KEY, true);
        editor.apply();
    }

    private void serviceStopped() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STEP_COUNTER_KEY, false);
        editor.apply();
    }

    private boolean checkStarted() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        boolean serviceStarted = sharedPreferences.getBoolean(STEP_COUNTER_KEY, false);
        return serviceStarted;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            StepCounterService.LocalBinder binder = (StepCounterService.LocalBinder) service;
            scService = binder.getService();
            stepCounterServiceBound = true;

            int period = 500;
            Date date = new Date();
            Timer timer = new Timer();
            timer.schedule(new MainActivity.GetWalk(), date, period); // downloads walk object every 500ms when MainActivity is running on the foreground
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            stepCounterServiceBound = false;
        }

    };

    public class GetWalk extends TimerTask {
        @Override
        public void run() {
            walk = scService.GetWalk();

            MainActivity.this.runOnUiThread((Runnable) () -> {
                stepProgress.setProgress((int)walk.stepsMade);
                totalStepsTextView.setText(getResources().getString(R.string.stepsMade, (int)walk.stepsMade, walk.stepsTarget));
                totalDistanceTextView.setText(getResources().getString(R.string.distanceMade, walk.calculateDistance()));
                totalCaloriesBurntTextView.setText(getResources().getString(R.string.caloriesBurnt, walk.calculateCaloriesBurnt()));
            });
        }
    }
}