package com.example.application.webservices.spoonacular.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MealSearchResult implements Serializable {
    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
