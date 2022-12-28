package com.example.application;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;
import com.example.application.database.repositories.DaysRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int CALCULATE_DAILY_REQUIREMENTS_REQUEST = 1;
    public static final String SHARED_PREFERENCES_FILE_NAME = "CaloriesCalculatorPreferences";
    public static final String WATER_GLASSES_KEY = "water_glasses";

    TextView waterLabel;
    Button registerWaterButton;
    ProgressBar waterProgressBar;

    public int dailyGlassesOfWater = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waterLabel = findViewById(R.id.howManyWaterToday);
        registerWaterButton = findViewById(R.id.registerWater);
        waterProgressBar = findViewById(R.id.waterProgress);

        Button scanProduct = findViewById(R.id.scanCodeBTN);
        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeScanningActivity.class);
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

        waterLabel.setText(getString(R.string.glassesOfWater, dailyGlassesOfWater));
    }
}