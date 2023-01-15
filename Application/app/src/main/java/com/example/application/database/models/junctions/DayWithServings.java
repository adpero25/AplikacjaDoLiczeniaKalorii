package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.application.database.models.Day;
import com.example.application.database.models.Serving;

import java.util.List;

public class DayWithServings {
    @Embedded
    public Day day;

    @Relation(
            entity = Serving.class,
            parentColumn = "day_id",
            entityColumn = "day_id"
    )
    public List<ServingWithMeal> servings;
}
