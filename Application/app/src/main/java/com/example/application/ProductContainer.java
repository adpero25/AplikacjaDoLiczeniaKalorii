package com.example.application;

import com.google.gson.annotations.SerializedName;

public class ProductContainer {

    @SerializedName("product")
    private Product product;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product _product) {
        this.product = _product;
    }
}
