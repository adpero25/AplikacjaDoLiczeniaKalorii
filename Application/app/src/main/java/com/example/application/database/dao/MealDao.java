package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.application.database.models.Meal;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;

import java.util.List;

@Dao
public interface MealDao {
    @Insert
    long insert(Meal meals);

    @Update
    void update(Meal... meals);

    @Delete
    void delete(Meal... meals);

    @Transaction
    @Query("SELECT * FROM meal")
    List<MealWithOpenFoodFact> getAll();

    @Transaction
    @Query("SELECT * FROM meal WHERE category_id IS NULL")
    List<MealWithOpenFoodFact> getAllWithoutCategory();
}
