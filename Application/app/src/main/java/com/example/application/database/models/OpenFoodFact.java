package com.example.application.database.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="open_food_fact",
        foreignKeys ={
            @ForeignKey(
                    onDelete = CASCADE,
                    entity = Meal.class,
                    parentColumns = "meal_id",
                    childColumns = "meal_id"
            )
        }
)
public class OpenFoodFact {
    @PrimaryKey
    @ColumnInfo(name = "open_food_fact_id")
    public long openFoodFactId;

    @ColumnInfo(name = "meal_id")
    @NonNull
    public long mealId;

    @NonNull
    public String code = "";
}
