package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.MealDao;
import com.example.application.database.dao.OpenFoodFactDao;
import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.models.junctions.OpenFoodFactWithMeal;
import com.example.application.database.repositories.base.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MealsRepository extends Repository {

    private final MealDao mealDao;
    private final OpenFoodFactDao openFoodFactDao;

    public MealsRepository(Application application) {
        super(application);

        mealDao = database.mealDao();
        openFoodFactDao = database.openFoodFactDao();
    }

    public MealsRepository(CaloriesDatabase database) {
        super(database);

        mealDao = database.mealDao();
        openFoodFactDao = database.openFoodFactDao();
    }

    public void insert(Meal meal) {
        queryExecutor.execute(() -> mealDao.insert(meal));
    }

    public void insertWithBarcode(Meal meal, String barcode) {
        queryExecutor.execute(
                () -> {
                    long newMealId = mealDao.insert(meal);
                    openFoodFactDao.insert(new OpenFoodFact() {{
                        code = barcode;
                        mealId = newMealId;
                    }});
                }
        );
    }

    public void update(Meal meal) {
        queryExecutor.execute(() -> mealDao.update(meal));
    }

    public void delete(Meal meal) {
        queryExecutor.execute(() -> mealDao.delete(meal));
    }

    public CompletableFuture<OpenFoodFactWithMeal> getByBarcode(String barcode) {
        return CompletableFuture.supplyAsync(() -> openFoodFactDao.get(barcode), queryExecutor);
    }

    public CompletableFuture<List<MealWithOpenFoodFact>> getAll() {
        return CompletableFuture.supplyAsync(mealDao::getAll, queryExecutor);
    }

    public CompletableFuture<List<MealWithOpenFoodFact>> getAllWithoutCategory() {
        return CompletableFuture.supplyAsync(mealDao::getAllWithoutCategory, queryExecutor);
    }
}