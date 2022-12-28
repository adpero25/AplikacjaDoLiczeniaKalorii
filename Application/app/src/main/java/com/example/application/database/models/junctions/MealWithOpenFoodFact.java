package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;

public class MealWithOpenFoodFact {
    @Embedded
    public Meal meal;

    @Relation(
            parentColumn = "meal_id",
            entityColumn = "meal_id"
    )
    public OpenFoodFact openFoodFact;
}
