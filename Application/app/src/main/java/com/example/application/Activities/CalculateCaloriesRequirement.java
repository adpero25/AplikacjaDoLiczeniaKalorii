package com.example.application.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.enums.ActivityIndicator;
import com.example.application.database.models.enums.MassTarget;
import com.google.android.material.snackbar.Snackbar;

public class CalculateCaloriesRequirement extends AppCompatActivity {

    EditText AgeBox;
    EditText WeightBox;
    EditText HeightBox;
    RadioGroup GenderGroup;
    RadioButton MaleButton;
    RadioButton FemaleButton;
    RadioGroup TargetGroup;
    RadioButton ReduceButton;
    RadioButton GainButton;
    Button ConfirmButton;
    Button CancelButton;
    Spinner activitySpinner;
    ActivityIndicator activityIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories_requirement);
        activityIndicator = ActivityIndicator.LightlyActivity;

        assignItems();

        ConfirmButton.setOnClickListener(v -> {

            if(!checkNullOrEmpty(AgeBox.getText().toString()) || !checkNullOrEmpty(WeightBox.getText().toString()) || !checkNullOrEmpty(HeightBox.getText().toString())){
                Snackbar.make(v, getString(R.string.MissingRequiredData), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            double result = MaleButton.isChecked() ? CalculateMaleResult() : CalculateFemaleResult();
            double fat, carb, protein;

            /*
                można wyliczać przedziały zpotrzebowań wtedy
                redu = białko 1.7 - 1.9 g/kg mc; fat 20-23% rawResult; węgle dopełnienie
                masa = białko 1.5 - 1.7 g/kg mc; fat 23-25% rawResult; węgle dpołenienie
                utrzymanie = białko 1.6 - 1.8 g/kg mc; fat 20 - 25% rawResult; węgle dopełnienie
             */
            DailyRequirements requirements = new DailyRequirements();
            if(ReduceButton.isChecked()){
                result -= 200;
                fat = (result * 0.2) / 9;
                protein = (1.8 * Double.parseDouble(WeightBox.getText().toString()));
                carb = ((result - (fat * 9 + protein * 4)) / 4);
                requirements.massTarget = MassTarget.Reduce;
            }
            else if(GainButton.isChecked()){
                result = result * 1.15;
                fat = (int)(result * 0.24) / 9;
                protein = (1.6 * Double.parseDouble(WeightBox.getText().toString()));
                carb = ((result - (fat * 9 + protein * 4)) / 4);
                requirements.massTarget = MassTarget.Gain;
            } else{
                result = result;
                fat = ((result * 0.22) / 9);
                protein = (1.6 * Double.parseDouble(WeightBox.getText().toString()));
                carb = ((result - (fat * 9 + protein * 4)) / 4);
                requirements.massTarget = MassTarget.Maintenance;
            }

            requirements.activityIndicator = activityIndicator;
            requirements.age = Integer.parseInt(AgeBox.getText().toString());
            requirements.height = Double.parseDouble(HeightBox.getText().toString());
            requirements.weight = Double.parseDouble(WeightBox.getText().toString());
            requirements.isMale = MaleButton.isChecked();
            requirements.nutritionalValuesTarget.calories = result;
            requirements.nutritionalValuesTarget.proteins = protein;
            requirements.nutritionalValuesTarget.fats = fat;
            requirements.nutritionalValuesTarget.carbohydrates = carb;

            CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplication());
            db.dailyRequirementsDao().insert(requirements);
            finish();
        });
        CancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void assignItems(){
        AgeBox = findViewById(R.id.AgeInput);
        WeightBox = findViewById(R.id.WeightInput);
        HeightBox = findViewById(R.id.HeightInput);
        GenderGroup = findViewById(R.id.GenderOptions);
        MaleButton = findViewById(R.id.MaleButton);
        FemaleButton = findViewById(R.id.FemaleButton);
        TargetGroup = findViewById(R.id.TargetOptions);
        ReduceButton = findViewById(R.id.MassReduction);
        GainButton = findViewById(R.id.MassGrow);
        ConfirmButton = findViewById(R.id.ConfirmDataButton);
        CancelButton = findViewById(R.id.CancelCalculationButton);

        activitySpinner = findViewById(R.id.ActivityRateSpinner);
        activitySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.custom_spinner, ActivityIndicator.values()));
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                activityIndicator = ActivityIndicator.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        activitySpinner.setSelection(activityIndicator.ordinal());
    }

    private double CalculateMaleResult(){
        double hbPPM =  66.5 + (13.75 * Double.parseDouble(WeightBox.getText().toString()))
                + (5 * Double.parseDouble(HeightBox.getText().toString()))
                - (6.775 * Integer.parseInt(AgeBox.getText().toString()));
        double mjPPM = (10 * Double.parseDouble(WeightBox.getText().toString()))
                + (6.25 * Double.parseDouble(HeightBox.getText().toString()))
                - (5 * Integer.parseInt(AgeBox.getText().toString())) + 5;

        return ((hbPPM + mjPPM) / 2) * activityIndicator.getIndicator();
    }
    private double CalculateFemaleResult(){
        double hbPPM =  655.1 + (9.563 * Double.parseDouble(WeightBox.getText().toString()))
                + (1.85 * Double.parseDouble(HeightBox.getText().toString()))
                - (4.676 * Integer.parseInt(AgeBox.getText().toString()));
        double mjPPM = (10 * Double.parseDouble(WeightBox.getText().toString()))
                + (6.25 * Double.parseDouble(HeightBox.getText().toString()))
                - (5 * Integer.parseInt(AgeBox.getText().toString())) - 161;

        return ((hbPPM + mjPPM) / 2) * activityIndicator.getIndicator();
    }
    private boolean checkNullOrEmpty(String title) {
        return title != null && !TextUtils.isEmpty(title);
    }
}