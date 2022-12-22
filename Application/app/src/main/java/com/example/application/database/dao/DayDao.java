package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;

import java.util.Date;
import java.util.List;

@TypeConverters({DateConverters.class})
@Dao
public interface DayDao {
    @Insert
    void insert(Day... days);

    @Update
    void update(Day... days);

    @Delete
    void delete(Day... days);

    @Transaction
    @Query("SELECT * FROM day WHERE day_id = :date")
    DayWithServings get(Date date);
}
