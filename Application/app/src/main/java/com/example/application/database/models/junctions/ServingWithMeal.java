package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.application.database.models.Meal;
import com.example.application.database.models.Serving;

public class ServingWithMeal {
    @Embedded
    public Serving serving;

    @Relation(
            entity = Meal.class,
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public MealWithOpenFoodFact meals;
}
