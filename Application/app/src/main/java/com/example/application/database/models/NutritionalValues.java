package com.example.application.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class NutritionalValues {
    @NonNull
    public Double calories = new Double(0);

    @NonNull
    public Double carbohydrates = new Double(0);

    @NonNull
    public Double fats = new Double(0);

    @NonNull
    public Double proteins = new Double(0);
}
