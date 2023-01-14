package com.example.application.webservices.spoonacular.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Nutrients implements Serializable {
    @SerializedName("name")
    String name;

    @SerializedName("amount")
    Double amount = 0D;
}
