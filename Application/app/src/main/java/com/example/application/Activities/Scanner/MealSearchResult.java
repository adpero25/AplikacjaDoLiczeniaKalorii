package com.example.application.Activities.Scanner;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MealSearchResult implements Serializable {
    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
