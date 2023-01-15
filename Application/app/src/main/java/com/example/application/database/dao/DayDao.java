package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;

import java.time.LocalDate;
import java.util.List;

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
    DayWithServings get(LocalDate date);

    @Transaction
    @Query("SELECT * FROM day WHERE day_id = :date")
    DayWithServings getDayByDate(LocalDate date);

    @Transaction
    @Query("SELECT * FROM day")
    List<Day> getAll();
}
