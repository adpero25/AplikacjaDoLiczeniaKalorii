package com.example.application.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.activities.MainActivity;
import com.example.application.database.models.Day;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StepsDetailsFragment extends Fragment {

    TextView stepDetails;
    TextView caloriesDetails;
    TextView distanceDetails;
    TextView dateDetails;
    android.widget.ProgressBar progressBar;
    View view;
    Day day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_steps_details, container, false);

        stepDetails = view.findViewById(R.id.stepsDetails);
        caloriesDetails = view.findViewById(R.id.caloriesDetails);
        distanceDetails = view.findViewById(R.id.distanceDetails);
        dateDetails = view.findViewById(R.id.dateDetails);
        progressBar = view.findViewById(R.id.stepsDetailsProgress);
        progressBar.setMax(MainActivity.STEPS_TARGET);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


        Gson converter = new Gson();
        String json = converter.toJson(day);
        outState.putString("Day", json);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        try {
            String dayJson = savedInstanceState.getString("Day");
            day = new Gson().fromJson(dayJson, Day.class);
            if (day != null)
                setDate(day);
        } catch (Exception ignored) {
        }
    }

    public void setDate(Day currentDay) {
        this.day = currentDay;

        String date = ConvertToShortDateString(currentDay.dayId);

        stepDetails.setText(getResources().getString(R.string.stepsMade, currentDay.stepsCount, MainActivity.STEPS_TARGET));
        caloriesDetails.setText(getResources().getString(R.string.caloriesBurnt, currentDay.burnedCalories));
        distanceDetails.setText(getResources().getString(R.string.distanceDetails, currentDay.totalDistance));
        dateDetails.setText(getResources().getString(R.string.dateDetails, date));
        progressBar.setProgress(currentDay.stepsCount);
    }

    private String ConvertToShortDateString(LocalDate dataBaseDay) {
        if (dataBaseDay == null) {
            return null;
        }

        DateTimeFormatter a = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return a.format(dataBaseDay);
    }
}