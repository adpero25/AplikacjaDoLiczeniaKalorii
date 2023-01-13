package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.converters.NormalDateConverter;
import com.example.application.database.models.Day;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.models.junctions.ServingWithMeal;

import java.util.Date;
import java.util.List;

@TypeConverters({DateConverters.class})
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
    List<ServingWithMeal> get(Date date);

    @Transaction
    @TypeConverters({NormalDateConverter.class})
    @Query("SELECT * FROM serving WHERE day_id = :date")
    List<ServingWithMeal> getServingsByDate(Date date);
}
