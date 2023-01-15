package com.example.application.webservices.spoonacular.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Optional;

public class MealInfo implements Serializable {
    @SerializedName("sourceUrl")
    private String sourceUrl;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("image")
    private String image;

    @SerializedName("title")
    private String title;

    @SerializedName("readyInMinutes")
    private Long readyInMinutes;

    @SerializedName("servings")
    private Long servings;

    @SerializedName("nutrition")
    private Nutrition nutrition;

    public Long getServings() {
        return servings;
    }

    public Long getReadyInMinutes() {
        return readyInMinutes;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public Double getCalories() {
        Optional<Nutrients> nutrients = nutrition.nutrients.stream().filter(n -> n.name.equals("Calories")).findFirst();
        return (nutrients.orElseGet(Nutrients::new)).amount;
    }

    public Double getFat() {// xD
        Optional<Nutrients> nutrients = nutrition.nutrients.stream().filter(n -> n.name.equals("Fat")).findFirst();
        return (nutrients.orElseGet(Nutrients::new)).amount;
    }

    public Double getCarbohydrates() {
        Optional<Nutrients> nutrients = nutrition.nutrients.stream().filter(n -> n.name.equals("Carbohydrates")).findFirst();
        return (nutrients.orElseGet(Nutrients::new)).amount;
    }

    public Double getProtein() {
        Optional<Nutrients> nutrients = nutrition.nutrients.stream().filter(n -> n.name.equals("Protein")).findFirst();
        return (nutrients.orElseGet(Nutrients::new)).amount;
    }
}

