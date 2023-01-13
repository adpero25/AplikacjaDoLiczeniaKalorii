package com.example.application.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.application.Activities.AddingServingActivity.CALORIES_REQUIREMENT;
import static com.example.application.Activities.MainActivity.SHARED_PREFERENCES_FILE_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.Activities.AddingServingActivity;
import com.example.application.R;
import com.example.application.SurfaceViews.CaloriesEatenSurfaceView;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.ServingsRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EatenCaloriesFragment extends Fragment {

    TextView caloriesTextView;
    TextView proteinsTextView;
    TextView carbohydratesTextView;
    TextView fatsTextView;
    CaloriesEatenSurfaceView caloriesEatenSurfaceView;
    TextView caloriesProgressTextView;
    View view;
    Button registerCalories;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_eaten_calories, container, false);

        registerCalories = view.findViewById(R.id.registerCaloriesBTN);
        registerCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddingServingActivity.class);
                startActivity(intent);
            }
        });

        setData(new Date());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void setData(Date date) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        int caloriesTarget = sharedPreferences.getInt(CALORIES_REQUIREMENT, 2000);

        caloriesTextView = view.findViewById(R.id.caloriesTextView);
        proteinsTextView = view.findViewById(R.id.proteinsTextView);
        carbohydratesTextView = view.findViewById(R.id.carbohydratesTextView);
        fatsTextView = view.findViewById(R.id.fatsTextView);
        caloriesEatenSurfaceView = view.findViewById(R.id.caloriesEatenSurfaceView);
        caloriesProgressTextView = view.findViewById(R.id.caloriesProgressTextView);

        caloriesEatenSurfaceView.setMaxValue(caloriesTarget);

        CaloriesDatabase db = CaloriesDatabase.getDatabase(getContext());
        List<ServingWithMeal> servingWithMeals = db.servingDao().getServingsByDate(date);

        float calories = 0, proteins = 0, carbohydrates = 0, fats = 0;
        for (ServingWithMeal s: servingWithMeals) {
            calories += s.meals.meal.nutritionalValues.calories * s.serving.servingSize;
            proteins += s.meals.meal.nutritionalValues.proteins * s.serving.servingSize;
            carbohydrates += s.meals.meal.nutritionalValues.carbohydrates * s.serving.servingSize;
            fats += s.meals.meal.nutritionalValues.fats * s.serving.servingSize;
        }

        caloriesEatenSurfaceView.setProgress((int)calories);
        caloriesProgressTextView.setText(getResources().getString(R.string.caloriesProgress, (int)calories, caloriesTarget));
        caloriesTextView.setText(getResources().getString(R.string.eatenCalories, calories));
        proteinsTextView.setText(getResources().getString(R.string.eatenProteins, proteins));
        carbohydratesTextView.setText(getResources().getString(R.string.eatenCarbohydrates, carbohydrates));
        fatsTextView.setText(getResources().getString(R.string.eatenFats, fats));
    }

    public void refresh(Date date) {
        setData(date);
    }

    public void hideButton() {
        registerCalories.setVisibility(View.INVISIBLE);
    }

    public void showButton() {
        registerCalories.setVisibility(View.VISIBLE);
    }
}