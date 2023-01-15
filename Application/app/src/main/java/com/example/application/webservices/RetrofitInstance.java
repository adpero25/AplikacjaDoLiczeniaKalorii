package com.example.application.webservices;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit openFoodFactsClient;
    private static Retrofit spoonacularClient;

    public static final String FOOD_API_URL = "https://world.openfoodfacts.org/";
    public static final String SPOONACULAR_API_URL = "https://api.spoonacular.com/";

    public static Retrofit getOpenFoodFactsClientInstance() {
        if(openFoodFactsClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            openFoodFactsClient = new Retrofit.Builder()
                    .baseUrl(FOOD_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return openFoodFactsClient;
    }

    public static Retrofit getSpoonacularClientInstance() {
        if(spoonacularClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request.Builder requestBuilder = chain.request().newBuilder();
                            requestBuilder.header("x-api-key", "15aed0f6db3a40428381a26fca5b029b");
                            return chain.proceed(requestBuilder.build());
                        }
                    })
                    .build();

            spoonacularClient = new Retrofit.Builder()
                    .baseUrl(SPOONACULAR_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return spoonacularClient;
    }
}
