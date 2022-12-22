package com.example.application.database.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName="open_food_fact",
        foreignKeys = {
            @ForeignKey(
                    onDelete = CASCADE,
                    entity = Meal.class,
                    parentColumns = "meal_id",
                    childColumns = "meal_id"
            )
        },
        indices = {
            @Index(
                    value = "code",
                    unique = true
            )
        }
)
public class OpenFoodFact {
    @PrimaryKey
    @ColumnInfo(name = "open_food_fact_id")
    public Long openFoodFactId;

    @ColumnInfo(name = "meal_id")
    @NonNull
    public Long mealId = (long) -1;

    @NonNull
    public String code = "";
}
