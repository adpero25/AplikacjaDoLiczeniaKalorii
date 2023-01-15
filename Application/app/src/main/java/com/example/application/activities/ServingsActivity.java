package com.example.application.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.application.R;
import com.example.application.database.models.enums.MealType;
import com.example.application.fragments.ServingsFragment;

import java.time.LocalDate;

public class ServingsActivity extends DrawerActivity {

    public static final String SERVING_DATE = "SERVING_DATE";

    ServingsFragment firstServingsFragment;
    ServingsFragment secondServingsFragment;
    ServingsFragment thirdServingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings);

        getIntent();
        Intent intent = getIntent();
        LocalDate currentDay = (LocalDate) intent.getSerializableExtra(ServingsActivity.SERVING_DATE);

        firstServingsFragment = (ServingsFragment) getSupportFragmentManager().findFragmentById(R.id.servings_fragment_container_1);
        secondServingsFragment = (ServingsFragment) getSupportFragmentManager().findFragmentById(R.id.servings_fragment_container_2);
        thirdServingsFragment = (ServingsFragment) getSupportFragmentManager().findFragmentById(R.id.servings_fragment_container_3);

        firstServingsFragment.setDayAndMealType(currentDay, MealType.Breakfast);
        secondServingsFragment.setDayAndMealType(currentDay, MealType.Dinner);
        thirdServingsFragment.setDayAndMealType(currentDay, MealType.Supper);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        firstServingsFragment.tryToReload();
        secondServingsFragment.tryToReload();
        thirdServingsFragment.tryToReload();
    }
}