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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.adapters.OneButtonListItemAdapter;
import com.example.application.adapters.SingleMessageAdapter;
import com.example.application.adapters.SingleSpinnerAdapter;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.ServingsRepository;
import com.example.application.webservices.spoonacular.model.MealSearchResult;
import com.example.application.webservices.spoonacular.MealSuggestionService;
import com.example.application.webservices.RetrofitInstance;
import com.example.application.activities.SuggestedMealActivity;
import com.example.application.R;
import com.example.application.database.models.Day;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealSuggestionsFragment extends Fragment {

    public static final String MEAL_ID = "MEAL_ID";

    View view;

    LayoutInflater inflater;
    RecyclerView listRoot;
    Day day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_meal_sugesstions, container, false);


        ((Button) view.findViewById(R.id.refresh)).setOnClickListener((v) -> {
            loadMealsForDay();
        });

        listRoot = view.findViewById(R.id.list_root);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        listRoot.setLayoutManager(layoutManager);


        loadMealsForDay();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void loadMealsForDay() {
        listRoot.post(() -> {
            listRoot.setAdapter(new SingleSpinnerAdapter());
            listRoot.smoothScrollToPosition(0);
        });

        MealSuggestionService mealSuggestionService = RetrofitInstance.getSpoonacularClientInstance().create(MealSuggestionService.class);
        try {
            DaysRepository dayRepo = new DaysRepository(requireActivity().getApplication());


            dayRepo.getOrCreateByDate(LocalDate.now()).thenAccept(
                    day -> {
                        view.post(() -> {
                            float calories = 0, proteins = 0, carbohydrates = 0, fats = 0;


                                for (ServingWithMeal s : day.servings) {

                                    calories += s.meals.meal.nutritionalValues.calories * s.serving.servingSize;
                                    proteins += s.meals.meal.nutritionalValues.proteins * s.serving.servingSize;
                                    carbohydrates += s.meals.meal.nutritionalValues.carbohydrates * s.serving.servingSize;
                                    fats += s.meals.meal.nutritionalValues.fats * s.serving.servingSize;
                                }

                            final float caloriesF = calories, proteinsF = proteins, carbohydratesF = carbohydrates, fatsF = fats;
                            dayRepo.getDayDailyRequirements(LocalDate.now()).thenAccept(requirements -> {
                                Long caloriesLeft = requirements.nutritionalValuesTarget.calories - caloriesF >= 0d ? Double.valueOf(requirements.nutritionalValuesTarget.calories - caloriesF).longValue() : 0,
                                        proteinsLeft = requirements.nutritionalValuesTarget.proteins - proteinsF >= 0d ? Double.valueOf(requirements.nutritionalValuesTarget.proteins - proteinsF).longValue() : 0,
                                        carbohydratesLeft = requirements.nutritionalValuesTarget.carbohydrates - carbohydratesF >= 0d ? Double.valueOf(requirements.nutritionalValuesTarget.carbohydrates - carbohydratesF).longValue() : 0,
                                        fatsLeft = requirements.nutritionalValuesTarget.fats - fatsF >= 0d ? Double.valueOf(requirements.nutritionalValuesTarget.fats - fatsF).longValue() : 0;

                                Call<List<MealSearchResult>> productsApiCall = mealSuggestionService.search(caloriesLeft, fatsLeft, proteinsLeft, carbohydratesLeft, 8L);

                                productsApiCall.enqueue(new Callback<List<MealSearchResult>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<MealSearchResult>> call, @NonNull Response<List<MealSearchResult>> response) {
                                        listRoot.post(() -> {
                                            if (response.body() != null && response.body().size() != 0) {

                                                OneButtonListItemAdapter<MealSearchResult> adapter = new OneButtonListItemAdapter<MealSearchResult>(response.body(),
                                                        (result) -> result.getTitle(),
                                                        () -> getString(R.string.open),
                                                        (context) ->
                                                                (View.OnClickListener) v -> {
                                                                    Intent intent = new Intent(getActivity(), SuggestedMealActivity.class);
                                                                    intent.putExtra(MEAL_ID, context.object.getId());
                                                                    startActivity(intent);
                                                                }
                                                );
                                                listRoot.setAdapter(adapter);
                                                listRoot.smoothScrollToPosition(0);
                                            } else {
                                                listRoot.setAdapter(new SingleMessageAdapter(getString(R.string.no_meals_returned)));
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<List<MealSearchResult>> call, @NonNull Throwable t) {
                                        listRoot.post(() -> {
                                            listRoot.setAdapter(new SingleMessageAdapter(getString(R.string.fetching_failed)));
                                            listRoot.smoothScrollToPosition(0);
                                        });
                                    }
                                });
                            });


                        });
                    }
            );

        } catch (Exception ignored) {
        }
    }
}