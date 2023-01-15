package com.example.application.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.activities.ServingsActivity;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.ServingsRepository;
import com.example.application.surfaceViews.CaloriesEatenSurfaceView;

import java.time.LocalDate;
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

    CompletableFuture future;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_eaten_calories, container, false);

        registerCalories = view.findViewById(R.id.registerCaloriesBTN);

        caloriesTextView = view.findViewById(R.id.caloriesTextView);
        proteinsTextView = view.findViewById(R.id.proteinsTextView);
        carbohydratesTextView = view.findViewById(R.id.carbohydratesTextView);
        fatsTextView = view.findViewById(R.id.fatsTextView);
        caloriesEatenSurfaceView = view.findViewById(R.id.caloriesEatenSurfaceView);
        caloriesProgressTextView = view.findViewById(R.id.caloriesProgressTextView);

        setData(LocalDate.now());
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

    private void setData(LocalDate date) {

        DaysRepository daysRepository = new DaysRepository(requireActivity().getApplication());
        daysRepository.getDayDailyRequirements(date).thenAccept(requirements -> {

            registerCalories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ServingsActivity.class);
                    intent.putExtra(ServingsActivity.SERVING_DATE, date);
                    startActivity(intent);
                }
            });

            caloriesEatenSurfaceView.setTarget(requirements.nutritionalValuesTarget.calories.intValue());

            ServingsRepository repo = new ServingsRepository(requireActivity().getApplication());

            Runnable runnable = () -> {
                repo.getByDate(date).thenAccept(
                        servingWithMeals -> {

                            view.post(
                                    () -> {
                                        float calories = 0, proteins = 0, carbohydrates = 0, fats = 0;
                                        for (ServingWithMeal s : servingWithMeals) {
                                            calories += s.meals.meal.nutritionalValues.calories * s.serving.servingSize;
                                            proteins += s.meals.meal.nutritionalValues.proteins * s.serving.servingSize;
                                            carbohydrates += s.meals.meal.nutritionalValues.carbohydrates * s.serving.servingSize;
                                            fats += s.meals.meal.nutritionalValues.fats * s.serving.servingSize;
                                        }

                                        caloriesEatenSurfaceView.setProgress((int) calories);
                                        caloriesProgressTextView.setText(getResources().getString(R.string.caloriesProgress, (int) calories, requirements.nutritionalValuesTarget.calories.intValue()));
                                        caloriesTextView.setText(getResources().getString(R.string.eatenCalories, calories, requirements.nutritionalValuesTarget.calories));
                                        proteinsTextView.setText(getResources().getString(R.string.eatenProteins, proteins, requirements.nutritionalValuesTarget.proteins));
                                        carbohydratesTextView.setText(getResources().getString(R.string.eatenCarbohydrates, carbohydrates, requirements.nutritionalValuesTarget.carbohydrates));
                                        fatsTextView.setText(getResources().getString(R.string.eatenFats, fats, requirements.nutritionalValuesTarget.fats));
                                    }
                            );
                        });
            };

            if (future != null) {
                future = future.thenRunAsync(runnable);
            } else {
                future = CompletableFuture.runAsync(runnable);
            }
        });
    }

    public void refresh(LocalDate date) {
        setData(date);
    }

    public void hideButton() {
        registerCalories.setVisibility(View.INVISIBLE);
    }

    public void showButton() {
        registerCalories.setVisibility(View.VISIBLE);
    }
}