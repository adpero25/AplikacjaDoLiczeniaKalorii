package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.application.database.models.Category;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.DayWithDailyRequirementsAndServings;
import com.example.application.database.models.junctions.DayWithServings;

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
}
