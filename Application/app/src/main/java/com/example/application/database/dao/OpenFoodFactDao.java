package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.application.database.models.OpenFoodFact;
import com.example.application.database.models.junctions.OpenFoodFactWithMeal;

@Dao
public interface OpenFoodFactDao {
    @Insert
    long insert(OpenFoodFact fact);

    @Transaction
    @Query("SELECT * FROM open_food_fact WHERE code = :code")
    OpenFoodFactWithMeal get(String code);
}
