package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.application.database.models.Day;
import com.example.application.database.models.Meal;
import com.example.application.database.models.Serving;

import java.util.List;

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
