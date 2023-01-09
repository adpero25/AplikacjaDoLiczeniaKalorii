package com.example.application.Fragments;

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

import com.example.application.Activities.AddingServingActivity;
import com.example.application.Activities.MainActivity;
import com.example.application.R;
import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.ServingsRepository;

public class ServingsFragment extends Fragment {

    public static final String MEAL_ID = "MEAL_ID";

    View view;

    LayoutInflater inflater;
    ViewGroup listRoot;
    DayWithServings day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        day = activity.getCurrentDay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater=inflater;
        view = inflater.inflate(R.layout.fragment_servings_list, container, false);


        ((Button)view.findViewById(R.id.add)).setOnClickListener((v)->{
            Intent intent = new Intent(getActivity(), AddingServingActivity.class);
            startActivity(intent);
        });

        listRoot = view.findViewById(R.id.list_root);
        loadServingsForDay();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void loadServingsForDay(){
        listRoot.removeAllViews();

        ServingsRepository servingsRepo = new ServingsRepository(getActivity().getApplication());
if(day!=null && day.servings!=null){


        for(ServingWithMeal serving : day.servings){
            listRoot.post(() -> {
                ViewGroup listItem = (ViewGroup) inflater.inflate(R.layout.one_button_list_item, null);
                ((TextView) listItem.findViewById(R.id.name)).setText(serving.meals.meal.name);
                listRoot.addView(listItem);

                ((Button) listItem.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        servingsRepo.delete(serving.serving);
                        listRoot.removeView(listItem);
                    }
                });
            });
        }}
    }
}