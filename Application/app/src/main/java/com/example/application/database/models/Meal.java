package com.example.application.database.models;

import static androidx.room.ForeignKey.SET_NULL;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "meal",
        foreignKeys = {
                @ForeignKey(
                        onDelete = SET_NULL,
                        entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id"
                )
        },
        indices = {
                @Index(
                        value = "category_id"
                )
        }
)
public class Meal implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meal_id")
    public Long mealId;
    @ColumnInfo(name = "category_id")
    public Long categoryId;

    @NonNull
    public String name = "";

    @NonNull
    public String description = "";

    @NonNull
    @Embedded
    public NutritionalValues nutritionalValues = new NutritionalValues();

}
