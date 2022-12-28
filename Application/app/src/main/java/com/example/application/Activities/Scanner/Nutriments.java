package com.example.application.Activities.Scanner;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Nutriments implements Serializable {
    @SerializedName("carbohydrates_100g")
    private String carbohydrates_100g;
    @SerializedName("energy-kcal_100g")
    private String energy_kcal_100g;
    @SerializedName("fat_100g")
    private String fat_100g;
    @SerializedName("fiber_100g")
    private String fiber_100g;
    @SerializedName("proteins_100g")
    private String proteins_100g;
    @SerializedName("salt_100g")
    private String salt_100g;
    @SerializedName("saturated-fat_100g")
    private String saturated_fat_100g;
    @SerializedName("sodium_100g")
    private String sodium_100g;
    @SerializedName("sugars_100g")
    private String sugars_100g;

    public String toStringEn() {
        return  " - Proteins: " + proteins_100g + " g,\n" +
                " - Carbohydrates: " + carbohydrates_100g + " g,\n" +
                " - Sugars: " + sugars_100g + " g,\n" +
                " - Fat: " + fat_100g + " g,\n" +
                " - Saturated fat: " + saturated_fat_100g + " g,\n" +
                " - Fiber: " + fiber_100g + " g,\n" +
                " - Salt: " + salt_100g + " g,\n" +
                " - Sodium: " + sodium_100g + " g,\n" +
                " - Energy: " + energy_kcal_100g + " kcal,\n";
    }

    public String toStringPl() {
        return  " - Białko: " + proteins_100g + " g,\n" +
                " - Węglowodany: " + carbohydrates_100g + " g,\n" +
                " - Cukier: " + sugars_100g + " g,\n" +
                " - Tłuszcz: " + fat_100g + " g,\n" +
                " - Tłuszcze nasycone: " + saturated_fat_100g + " g,\n" +
                " - Błonnik: " + fiber_100g + " g,\n" +
                " - Sól: " + salt_100g + " g,\n" +
                " - Sód: " + sodium_100g + " g,\n" +
                " - Energia: " + energy_kcal_100g + " kcal,\n";
    }


    public String getCarbohydrates_100g() {
        return carbohydrates_100g;
    }

    public void setCarbohydrates_100g(String carbohydrates_100g) {
        this.carbohydrates_100g = carbohydrates_100g;
    }

    public String getEnergy_kcal_100g() {
        return energy_kcal_100g;
    }

    public void setEnergy_kcal_100g(String energy_kcal_100g) {
        this.energy_kcal_100g = energy_kcal_100g;
    }

    public String getFat_100g() {
        return fat_100g;
    }

    public void setFat_100g(String fat_100g) {
        this.fat_100g = fat_100g;
    }

    public String getFiber_100g() {
        return fiber_100g;
    }

    public void setFiber_100g(String fiber_100g) {
        this.fiber_100g = fiber_100g;
    }

    public String getProteins_100g() {
        return proteins_100g;
    }

    public void setProteins_100g(String proteins_100g) {
        this.proteins_100g = proteins_100g;
    }

    public String getSalt_100g() {
        return salt_100g;
    }

    public void setSalt_100g(String salt_100g) {
        this.salt_100g = salt_100g;
    }

    public String getSaturated_fat_100g() {
        return saturated_fat_100g;
    }

    public void setSaturated_fat_100g(String saturated_fat_100g) {
        this.saturated_fat_100g = saturated_fat_100g;
    }

    public String getSodium_100g() {
        return sodium_100g;
    }

    public void setSodium_100g(String sodium_100g) {
        this.sodium_100g = sodium_100g;
    }

    public String getSugars_100g() {
        return sugars_100g;
    }

    public void setSugars_100g(String sugars_100g) {
        this.sugars_100g = sugars_100g;
    }
}
