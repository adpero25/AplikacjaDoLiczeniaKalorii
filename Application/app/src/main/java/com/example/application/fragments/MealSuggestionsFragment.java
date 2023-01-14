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

import com.example.application.webservices.spoonacular.model.MealSearchResult;
import com.example.application.webservices.spoonacular.MealSuggestionService;
import com.example.application.webservices.RetrofitInstance;
import com.example.application.activities.SuggestedMealActivity;
import com.example.application.R;
import com.example.application.database.models.Day;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealSuggestionsFragment extends Fragment {

    public static final String MEAL_ID = "MEAL_ID";

    View view;

    LayoutInflater inflater;
    ViewGroup listRoot;
    Day day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater=inflater;
        view = inflater.inflate(R.layout.fragment_meal_sugesstions, container, false);


        ((Button)view.findViewById(R.id.refresh)).setOnClickListener((v)->{
            loadMealsForDay(day);
        });

        listRoot = view.findViewById(R.id.list_root);
        inflater.inflate(R.layout.loading_list_item,listRoot);

        loadMealsForDay(new Day());
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void loadMealsForDay(Day newDay){
        listRoot.removeAllViews();
        inflater.inflate(R.layout.loading_list_item,listRoot);

        MealSuggestionService mealSuggestionService = RetrofitInstance.getSpoonacularClientInstance().create(MealSuggestionService.class);
        try {

            //TODO calculate reqs
            Call<List<MealSearchResult>> productsApiCall = mealSuggestionService.search(100L,100L,100L,100L,8L);

            productsApiCall.enqueue(new Callback<java.util.List<MealSearchResult>>() {
                @Override
                public void onResponse(@NonNull Call<List<MealSearchResult>> call, @NonNull Response<List<MealSearchResult>> response) {
                    listRoot.removeAllViews();
                    if (response.body() != null) {
                        List<MealSearchResult> responseBody = response.body();
                        for(MealSearchResult result : responseBody){
                            listRoot.post(() -> {
                                ViewGroup listItem = (ViewGroup) inflater.inflate(R.layout.one_button_list_item, listRoot);
                                ((TextView) listItem.findViewById(R.id.name)).setText(result.getTitle());


                                ((Button) listItem.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), SuggestedMealActivity.class);
                                        intent.putExtra(MEAL_ID, result.getId());
                                        startActivity(intent);
                                    }
                                });
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<MealSearchResult>> call, @NonNull Throwable t) {
                    listRoot.removeAllViews();
                }
            });
        }
        catch (Exception ignored) {}
    }
}