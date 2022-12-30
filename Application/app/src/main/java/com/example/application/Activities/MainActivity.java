package com.example.application.Activities;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.backgroundTasks.NotifyAboutWater;
import com.example.application.broadcastReceivers.WaterBroadcastReceiver;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;
import com.example.application.database.repositories.DaysRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int CALCULATE_DAILY_REQUIREMENTS_REQUEST = 1;
    public static final String SHARED_PREFERENCES_FILE_NAME = "CaloriesCalculatorPreferences";
    public static final String WATER_GLASSES_KEY = "water_glasses";
    public static final String CHANNEL_ID = "WATER_CHANNEL_ID";

    TextView waterLabel;
    Button registerWaterButton;
    ProgressBar waterProgressBar;
    Button addDailyWaterRequirement;
    public int dailyGlassesOfWater = 0;
    public static final int waterNotification = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dismissNotification();
        startWaterNotificationService();
        createNotificationChannel();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

        waterLabel = findViewById(R.id.howManyWaterToday);
        registerWaterButton = findViewById(R.id.registerWater);
        waterProgressBar = findViewById(R.id.waterProgress);
        addDailyWaterRequirement = findViewById(R.id.dailyWaterRequirement);

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

                    dailyGlassesOfWater = Integer.parseInt(enteredValue.getText().toString());

                    updateWaterLabel();
                });

            }
        });

        updateWaterLabel();

    }


    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();


        preferencesEditor.putInt(WATER_GLASSES_KEY, dailyGlassesOfWater);
        preferencesEditor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        dailyGlassesOfWater = sharedPreferences.getInt(WATER_GLASSES_KEY, 0);

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

    private void startWaterNotificationService(){
        Intent intent = new Intent(this, NotifyAboutWater.class);
        startService(intent);
    }

}