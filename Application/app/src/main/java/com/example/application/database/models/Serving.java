package com.example.application.database.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName="serving",
        foreignKeys ={
            @ForeignKey(
                    onDelete = CASCADE,
                    entity = Meal.class,
                    parentColumns = "meal_id",
                    childColumns = "meal_id"
            ),
            @ForeignKey(
                    onDelete = CASCADE,
                    entity = Day.class,
                    parentColumns = "day_id",
                    childColumns = "day_id"
            )
        },
        indices = {
            @Index(
                    value = "meal_id"
            ),
            @Index(
                    value = "day_id"
            )
        }
)
public class Serving {
    @ColumnInfo(name = "serving_id")
    @PrimaryKey
    public Long servingId;

    @ColumnInfo(name = "meal_id")
    @NonNull
    public Long mealId = (long) -1;

    @ColumnInfo(name = "day_id")
    @NonNull
    public Long dayId = (long) -1;

    @ColumnInfo(name = "serving_size")
    @NonNull
    public Double servingSize = 0.0;
}


