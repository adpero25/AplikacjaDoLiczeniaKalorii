package com.example.application.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.io.Serializable;

public class NutritionalValues implements Serializable {
    @NonNull
    public Double calories = new Double(0);

    @NonNull
    public Double carbohydrates = new Double(0);

    @NonNull
    public Double fats = new Double(0);

    @NonNull
    public Double proteins = new Double(0);
}
