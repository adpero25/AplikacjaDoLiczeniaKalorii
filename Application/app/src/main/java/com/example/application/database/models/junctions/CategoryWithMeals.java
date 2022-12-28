package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.application.database.models.Category;
import com.example.application.database.models.Meal;
import com.example.application.database.models.Serving;

import java.util.List;

public class CategoryWithMeals {
    @Embedded
    public Category category;

    @Relation(
            entity = Meal.class,
            parentColumn = "category_id",
            entityColumn = "category_id"
    )
    public List<MealWithOpenFoodFact> meals;
}
