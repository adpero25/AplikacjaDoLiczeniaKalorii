package com.example.application.webservices.spoonacular;

import com.example.application.webservices.spoonacular.model.MealInfo;
import com.example.application.webservices.spoonacular.model.MealSearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MealSuggestionService {
    @GET("recipes/findByNutrients?random=true")
    Call<List<MealSearchResult>> search(@Query("maxCalories") Long maxCalories, @Query("maxFat") Long maxFat, @Query("maxProtein") Long maxProtein, @Query("maxCarbs") Long maxCarbs, @Query("number") Long number);

    @GET("recipes/{id}/information?includeNutrition=true")
    Call<MealInfo> loadMeal(@Path("id") Long id);
}
