package com.example.application.database.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.converters.MealTypeConverter;
import com.example.application.database.models.enums.MealType;

import java.time.LocalDate;
import java.util.Enumeration;

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
@TypeConverters({DateConverters.class})
public class Serving {
    @ColumnInfo(name = "serving_id")
    @PrimaryKey
    public Long servingId;

    @ColumnInfo(name = "meal_id")
    @NonNull
    public Long mealId = (long) -1;

    @ColumnInfo(name = "day_id")
    @NonNull
    public LocalDate dayId = LocalDate.now();

    @ColumnInfo(name = "serving_size")
    @NonNull
    public Double servingSize = 0.0;

    @NonNull
    @ColumnInfo(name = "meal_type", defaultValue = "1")
    public MealType mealType = MealType.Breakfast;
}

