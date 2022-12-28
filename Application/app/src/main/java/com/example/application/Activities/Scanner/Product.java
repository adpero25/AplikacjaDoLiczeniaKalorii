package com.example.application.Activities.Scanner;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("allergens")
    private String allergens;
    @SerializedName("brands")
    private String brands;
    @SerializedName("categories")
    private String categories;
    @SerializedName("nutriments")
    private Nutriments nutriments;
    @SerializedName("selected_images")
    private Images images ;
    @SerializedName("expiration_date")
    private String expiration_date;
    @SerializedName("ingredients_text_pl")
    private String ingredients_text_pl;
    @SerializedName("ingredients_text_en")
    private String ingredients_text_en;
    @SerializedName("labels")
    private String labels;
    @SerializedName("product_name_pl")
    private String product_name_pl;
    @SerializedName("product_name_en")
    private String product_name_en;
    @SerializedName("purchase_places")
    private String purchase_places;
    @SerializedName("quantity")
    private String quantity;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getIngredients_text_pl() {
        return ingredients_text_pl;
    }

    public void setIngredients_text_pl(String ingredients_text_pl) {
        this.ingredients_text_pl = ingredients_text_pl;
    }

    public String getIngredients_text_en() {
        return ingredients_text_en;
    }

    public void setIngredients_text_en(String ingredients_text_en) {
        this.ingredients_text_en = ingredients_text_en;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getProduct_name_pl() {
        return product_name_pl;
    }

    public void setProduct_name_pl(String product_name_pl) {
        this.product_name_pl = product_name_pl;
    }

    public String getProduct_name_en() {
        return product_name_en;
    }

    public void setProduct_name_en(String product_name_en) {
        this.product_name_en = product_name_en;
    }

    public String getPurchase_places() {
        return purchase_places;
    }

    public void setPurchase_places(String purchase_places) {
        this.purchase_places = purchase_places;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
