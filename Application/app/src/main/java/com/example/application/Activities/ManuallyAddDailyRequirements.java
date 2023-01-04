package com.example.application.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.application.R;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;
import com.google.android.material.snackbar.Snackbar;

public class ManuallyAddDailyRequirements extends AppCompatActivity {

    EditText caloriesInput;
    EditText proteinsInput;
    EditText carbohydratesInput;
    EditText fatInput;
    EditText ageInput;
    EditText weightInput;
    EditText heightInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_daily_requirements);

        caloriesInput = findViewById(R.id.daily_caloriesRequirementInput);
        proteinsInput = findViewById(R.id.daily_proteinsInput);
        carbohydratesInput = findViewById(R.id.daily_carbohydratesInput);
        fatInput = findViewById(R.id.daily_fatInput);
        ageInput = findViewById(R.id.daily_ageInput);
        weightInput = findViewById(R.id.daily_weightInput);
        heightInput = findViewById(R.id.daily_heightInput);

        Button submitButton = findViewById(R.id.daily_confirmDataButton);
        submitButton.setOnClickListener(v -> {
            if(!checkNullOrEmpty(caloriesInput.getText().toString()) || !checkNullOrEmpty(proteinsInput.getText().toString())
                    || !checkNullOrEmpty(carbohydratesInput.getText().toString()) || !checkNullOrEmpty(fatInput.getText().toString())){
                Snackbar.make(v, getString(R.string.MissingRequiredData), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            DailyRequirements dailyRequirements = new DailyRequirements();
            dailyRequirements.nutritionalValuesTarget.calories = Double.parseDouble(caloriesInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.proteins = Double.parseDouble(proteinsInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.carbohydrates = Double.parseDouble(carbohydratesInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.fats = Double.parseDouble(fatInput.getText().toString());

            if(checkNullOrEmpty(ageInput.getText().toString())){
                dailyRequirements.age = Integer.parseInt(ageInput.getText().toString());
            }

            if(checkNullOrEmpty(heightInput.getText().toString())){
                dailyRequirements.height = Double.parseDouble(heightInput.getText().toString());
            }

            if(checkNullOrEmpty(weightInput.getText().toString())){
                dailyRequirements.weight = Double.parseDouble(weightInput.getText().toString());
            }

            CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplication());
            db.dailyRequirementsDao().insert(dailyRequirements);
            finish();
        });

        Button CancelButton = findViewById(R.id.daily_cancelButton);
        CancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean checkNullOrEmpty(String title) {
        return title != null && !TextUtils.isEmpty(title);
    }
}