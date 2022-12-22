package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.CategoryDao;
import com.example.application.database.models.Category;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.base.Repository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CategoriesRepository extends Repository {

    private final CategoryDao categoryDao;

    CategoriesRepository(Application application) {
        super(application);

        categoryDao = database.categoryDao();
    }

    CategoriesRepository(CaloriesDatabase database) {
        super(database);

        categoryDao = database.categoryDao();
    }

    void insert(Category category) {
        queryExecutor.execute(() -> categoryDao.insert(category));
    }

    void insert(String categoryName) {
        insert(new Category(){{
            name = categoryName;
        }});
    }

    void update(Category category) {
        queryExecutor.execute(() -> categoryDao.update(category));
    }

    void delete(Category category) {
        queryExecutor.execute(() -> categoryDao.delete(category));
    }

    CompletableFuture<List<CategoryWithMeals>> getAll(){
        return CompletableFuture.supplyAsync(categoryDao::getAll, queryExecutor);
    }

    CompletableFuture<List<CategoryWithMeals>> getAllWithoutMeals(){
        return CompletableFuture.supplyAsync(categoryDao::getAll, queryExecutor);
    }
}