package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;

public class OpenFoodFactWithMeal {
    @Embedded
    public OpenFoodFact openFoodFact;

    @Relation(
            entity = Meal.class,
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public Meal meal;
}
