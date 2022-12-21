package com.example.application;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductService {
    @GET("api/v0/product/{code}")
    Call<ProductContainer> findProducts(@Path("code") String query);
}
