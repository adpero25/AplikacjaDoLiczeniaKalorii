package com.example.application.webservices.spoonacular.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Nutrition implements Serializable {
    @SerializedName("nutrients")
    List<Nutrients> nutrients;
}
