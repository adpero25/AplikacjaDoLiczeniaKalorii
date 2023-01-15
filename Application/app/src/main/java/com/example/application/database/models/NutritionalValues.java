package com.example.application.database.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class NutritionalValues implements Serializable {
    @NonNull
    public Double calories = 0D;

    @NonNull
    public Double carbohydrates = 0D;

    @NonNull
    public Double fats = 0D;

    @NonNull
    public Double proteins = 0D;
}
