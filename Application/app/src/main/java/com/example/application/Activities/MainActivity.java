package com.example.application.Activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import com.example.application.CaloriesCalculatorContext;
import com.example.application.Fragments.EatenCaloriesFragment;
import com.example.application.Fragments.StepsDetailsFragment;
import com.example.application.R;
import com.example.application.backgroundTasks.NotifyAboutWater;
import com.example.application.backgroundTasks.StepCounterService;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.DayDao;
import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.DaysRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends DrawerActivity {
    private static final int ACTIVITY_PERMISSION = 100;
    private static final int ADD_MANUALLY_DAILY_REQUIREMENTS_REQUEST = 20;
    public static int STEPS_TARGET = 5000;
    private boolean stepCounterServiceBound = false;
    public static final int CALCULATE_DAILY_REQUIREMENTS_REQUEST = 1;
    public static final String SHARED_PREFERENCES_FILE_NAME = "CaloriesCalculatorPreferences";
    public static final String WATER_GLASSES_KEY = "water_glasses";
    public static final String STEP_COUNTER_KEY = "service_started";
    public static final String CHANNEL_ID = "WATER_CHANNEL_ID";
    public static final String COUNTER_RESET = "COUNTER_RESET";

    TextView waterLabel;
    TextView totalStepsTextView;
    TextView totalDistanceTextView;
    TextView totalCaloriesBurntTextView;
    TextView currentDateTextView;
    StepCounterService.Walk walk;
    StepCounterService scService;
    Button registerWaterButton;
    ProgressBar waterProgressBar;
    ProgressBar stepProgress;
    Button addDailyWaterRequirement;
    Button setStepTarget;
    Button previousDate;
    Button nextDate;
    public int dailyGlassesOfWater = 0;
    public static final int waterNotification = 10;
    EatenCaloriesFragment caloriesFragment;
    DayWithServings currentDay;

    public DayWithServings getCurrentDay(){
        return currentDay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_new);

        dismissNotification();
        if(!isServiceRunning(NotifyAboutWater.class)){
            startWaterNotificationService();
        }

        if(currentDay ==null){
            DaysRepository repo = new DaysRepository(getApplication());
            repo.getOrCreateToday().thenAccept((newDay)->{
                currentDay = newDay;
                setCurrentDate(currentDay.day.dayId);
                recreate();
            });
        }

        createNotificationChannel();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

        currentDateTextView = findViewById(R.id.dayTextView);
        waterLabel = findViewById(R.id.howManyWaterToday);
        registerWaterButton = findViewById(R.id.registerWater);
        waterProgressBar = findViewById(R.id.waterProgress);
        stepProgress = findViewById(R.id.stepsProgress);
        stepProgress.setMax(STEPS_TARGET);
        setStepTarget = findViewById(R.id.dailyStepTarget);
        totalStepsTextView = findViewById(R.id.howManyStepsToday);
        totalDistanceTextView = findViewById(R.id.distanceToday);
        totalCaloriesBurntTextView = findViewById(R.id.caloriesBurnedToday);
        caloriesFragment = (EatenCaloriesFragment) getSupportFragmentManager().findFragmentById(R.id.calories_fragment_container);

        ConstraintLayout stepCounterContainer = findViewById(R.id.stepsContainer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        ACTIVITY_PERMISSION);
            }
            //serviceStopped();
            startStepCounterService();

            stepCounterContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, StepsHistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            stepCounterContainer.setMaxHeight(0);
        }





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


        previousDate = findViewById(R.id.previousDateBTN);
        previousDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplicationContext());
                DayWithServings day =  db.dayDao().getByDate(getDayBefore(currentDay.day.dayId));
                currentDay = day == null ? currentDay : day;
                setCurrentDate(currentDay.day.dayId);



            }
        });


        nextDate = findViewById(R.id.nextDateBTN);
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplicationContext());
                DayWithServings day = db.dayDao().getByDate(getDayAfter(currentDay.day.dayId));
                currentDay = day == null ? currentDay : day;
                setCurrentDate(currentDay.day.dayId);



            }
        });

    }

    private void setCurrentDate(Date currentDay) {

        currentDateTextView.setText(getResources().getString(R.string.currentDate, currentDay.getDate(), currentDay.getMonth(), currentDay.getYear()+1900));
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
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

        stepCounterServiceBound = true;

        // Reload fragment
        caloriesFragment.refresh(currentDay.day.dayId);
    }



    @Override
    protected void onStop() {
        super.onStop();
        stepCounterServiceBound = false;
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
        Intent intent = new Intent(this, StepCounterService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(intent);
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
            if(stepCounterServiceBound) {
                walk = scService.GetWalk();
                MainActivity.this.runOnUiThread((Runnable) () -> {
                    Log.d("GET_WALK", "GET WALK Running");
                    stepProgress.setProgress((int) walk.stepsMade);
                    totalStepsTextView.setText(getResources().getString(R.string.stepsMade, (int) walk.stepsMade, walk.stepsTarget));
                    totalDistanceTextView.setText(getResources().getString(R.string.distanceMade, walk.calculateDistance()));
                    totalCaloriesBurntTextView.setText(getResources().getString(R.string.caloriesBurnt, walk.calculateBurnedCalories()));
                });
            }
        }
    }


    private Date getDayBefore(Date date) {
        Date dayBefore;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime() - 24 * 3600);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        dayBefore = calendar.getTime();

        return dayBefore;
    }

    private Date getDayAfter(Date date) {
        Date dayAfter;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, date.getDate() + 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        dayAfter = calendar.getTime();

        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, new Date().getMonth() + 1);

        Date currentDate = calendar.getTime();

        if(dayAfter.before(currentDate))
            return dayAfter;
        else
            return date;
    }
}