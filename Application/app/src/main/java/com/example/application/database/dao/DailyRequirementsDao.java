package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface DailyRequirementsDao {
    @Insert
    long insert(DailyRequirements requirements);

    @Update
    void update(DailyRequirements... requirements);

    @Delete
    void delete(DailyRequirements... requirements);

    @Query("SELECT * FROM daily_requirements")
    List<DailyRequirements> listDailyRequirements();

    @Transaction
    @Query("SELECT * FROM day")
    List<DayWithDailyRequirementsAndServings> getDayWithServingsWithDailyRequirements();

    @Query("SELECT * FROM daily_requirements WHERE daily_requirements_id = :id")
    DailyRequirements getById(Long id);

    @Query("SELECT * FROM daily_requirements WHERE entry_date = :date")
    DailyRequirements getByDate(LocalDate date);

    @Query("SELECT * FROM daily_requirements WHERE entry_date <= :date ORDER BY entry_date")
    DailyRequirements getLastRequirement(LocalDate date);
}
