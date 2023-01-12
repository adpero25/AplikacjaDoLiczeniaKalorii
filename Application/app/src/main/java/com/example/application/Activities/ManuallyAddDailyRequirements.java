package com.example.application.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.application.R;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Date;

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
            dailyRequirements.entryDate = new Date();

            if(checkNullOrEmpty(ageInput.getText().toString())){
                dailyRequirements.age = Integer.parseInt(ageInput.getText().toString());
            }

            if(checkNullOrEmpty(heightInput.getText().toString())){
                dailyRequirements.height = Double.parseDouble(heightInput.getText().toString());
            }

            if(checkNullOrEmpty(weightInput.getText().toString())){
                dailyRequirements.weight = Double.parseDouble(weightInput.getText().toString());
            }

            if(dailyRequirements.height != 0 && dailyRequirements.weight != 0) {
                SharedPreferences userDataSharedPreferences = getSharedPreferences(USER_DATA_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userDataSharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(dailyRequirements);
                editor.putString(USER_DATA_KEY, json);
                editor.apply();
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