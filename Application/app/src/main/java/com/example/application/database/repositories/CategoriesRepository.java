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

    public CategoriesRepository(Application application) {
        super(application);

        categoryDao = database.categoryDao();
    }

    public CategoriesRepository(CaloriesDatabase database) {
        super(database);

        categoryDao = database.categoryDao();
    }

    public CompletableFuture<Long> insert(Category category) {
        return CompletableFuture.supplyAsync(()->categoryDao.insert(category));
    }

    public void insert(String categoryName) {
        insert(new Category(){{
            name = categoryName;
        }});
    }

    public void update(Category category) {
        queryExecutor.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        queryExecutor.execute(() -> categoryDao.delete(category));
    }

    public CompletableFuture<List<CategoryWithMeals>> getAll(){
        return CompletableFuture.supplyAsync(categoryDao::getAll, queryExecutor);
    }

    public CompletableFuture<Category> getByName(String name){
        return CompletableFuture.supplyAsync(()->categoryDao.getByName(name), queryExecutor);
    }

    public CompletableFuture<List<CategoryWithMeals>> getAllWithoutMeals(){
        return CompletableFuture.supplyAsync(categoryDao::getAll, queryExecutor);
    }

    public CompletableFuture<Category> getById(Long categoryId) {
        return CompletableFuture.supplyAsync(()->categoryDao.getById(categoryId), queryExecutor);
    }
}