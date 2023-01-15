package com.example.application.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.application.R;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.repositories.DailyRequirementsRepository;
import com.example.application.helpers.StringHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.time.LocalDate;

public class ManuallyAddDailyRequirements extends DrawerActivity {

    public static final String USER_DATA_KEY = "USER_DATA_KEY";
    public static final String USER_DATA_SHARED_PREFERENCES_FILE_NAME = "USER_DATA_SHARED_PREFERENCES_FILE_NAME";

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
            if (!StringHelper.checkNullOrEmpty(caloriesInput.getText().toString()) || !StringHelper.checkNullOrEmpty(proteinsInput.getText().toString())
                    || !StringHelper.checkNullOrEmpty(carbohydratesInput.getText().toString()) || !StringHelper.checkNullOrEmpty(fatInput.getText().toString())) {
                Snackbar.make(v, getString(R.string.MissingRequiredData), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            if(!StringHelper.checkPositiveNumber(caloriesInput.getText().toString()) || !StringHelper.checkPositiveNumber(proteinsInput.getText().toString())
                    || !StringHelper.checkPositiveNumber(carbohydratesInput.getText().toString()) || !StringHelper.checkPositiveNumber(fatInput.getText().toString())){
                Snackbar.make(v, getString(R.string.non_negative), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            DailyRequirements dailyRequirements = new DailyRequirements();
            dailyRequirements.nutritionalValuesTarget.calories = Double.parseDouble(caloriesInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.proteins = Double.parseDouble(proteinsInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.carbohydrates = Double.parseDouble(carbohydratesInput.getText().toString());
            dailyRequirements.nutritionalValuesTarget.fats = Double.parseDouble(fatInput.getText().toString());
            dailyRequirements.entryDate = LocalDate.now();

            if (StringHelper.checkNullOrEmpty(ageInput.getText().toString())) {
                if(!StringHelper.checkPositiveNumber(ageInput.getText().toString())){
                    Snackbar.make(v, getString(R.string.non_negative), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                dailyRequirements.age = Integer.parseInt(ageInput.getText().toString());
            }

            if (StringHelper.checkNullOrEmpty(heightInput.getText().toString())) {
                if(!StringHelper.checkPositiveNumber(heightInput.getText().toString())){
                    Snackbar.make(v, getString(R.string.non_negative), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                dailyRequirements.height = Double.parseDouble(heightInput.getText().toString());
            }

            if (StringHelper.checkNullOrEmpty(weightInput.getText().toString())) {
                if(!StringHelper.checkPositiveNumber(weightInput.getText().toString())){
                    Snackbar.make(v, getString(R.string.non_negative), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                dailyRequirements.weight = Double.parseDouble(weightInput.getText().toString());
            }

            DailyRequirementsRepository repo = new DailyRequirementsRepository(getApplication());
            repo.insertOrUpdate(dailyRequirements);
            finish();
        });

        Button CancelButton = findViewById(R.id.daily_cancelButton);
        CancelButton.setOnClickListener(v -> {
            finish();
        });
    }
}