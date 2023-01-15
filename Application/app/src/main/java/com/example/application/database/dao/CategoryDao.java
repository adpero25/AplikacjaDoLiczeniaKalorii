package com.example.application.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.models.Category;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.ServingWithMeal;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    int update(Category categories);

    @Delete
    void delete(Category... categories);

    @Transaction
    @Query("SELECT * FROM category")
    List<CategoryWithMeals> getAll();

    @Query("SELECT * FROM category where name = :name")
    Category getByName(String name);

    @Query("SELECT * FROM category")
    List<Category> getAllWithoutMeals();

    @Query("SELECT * FROM category WHERE category_id=:id")
    Category getById(Long id);
}
