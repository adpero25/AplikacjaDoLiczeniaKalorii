package com.example.application.database.models;

import static androidx.room.ForeignKey.SET_NULL;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="meal",
        foreignKeys ={
                @ForeignKey(
                        onDelete = SET_NULL,
                        entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id"
                )
        }
)
public class Meal {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meal_id")
    public Long mealId;
    @ColumnInfo(name = "category_id")
    public Long category_id;

    @NonNull
    public String name;

    @NonNull
    public String description = "";

    @ColumnInfo(name = "picture_path")
    public String picturePath;

    @NonNull
    @Embedded
    public NutritionalValues nutritionalValues = new NutritionalValues();
}
