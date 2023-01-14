package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.ServingWithMeal;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface ServingDao {
    @Insert
    long insert(Serving serving);

    @Update
    void update(Serving... servings);

    @Delete
    void delete(Serving... servings);

    @Transaction
    @Query("SELECT * FROM serving WHERE day_id = :date")
    List<ServingWithMeal> get(LocalDate date);

    @Transaction
    @Query("SELECT * FROM serving WHERE day_id = :date")
    List<ServingWithMeal> getServingsByDate(LocalDate date);
}
