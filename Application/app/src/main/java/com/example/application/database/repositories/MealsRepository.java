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

    MealsRepository(Application application) {
        super(application);

        mealDao = database.mealDao();
        openFoodFactDao = database.openFoodFactDao();
    }

    MealsRepository(CaloriesDatabase database) {
        super(database);

        mealDao = database.mealDao();
        openFoodFactDao = database.openFoodFactDao();
    }

    void insert(Meal meal) {
        queryExecutor.execute(() -> mealDao.insert(meal));
    }

    void insertWithBarcode(Meal meal, String barcode) {
        queryExecutor.execute(
                () -> {
                    long newMealId = mealDao.insert(meal);
                    openFoodFactDao.insert(new OpenFoodFact(){{
                        code=barcode;
                        mealId=newMealId;
                    }});
                }
        );
    }

    void update(Meal meal) {
        queryExecutor.execute(() -> mealDao.update(meal));
    }

    void delete(Meal meal) {
        queryExecutor.execute(() -> mealDao.delete(meal));
    }

    CompletableFuture<OpenFoodFactWithMeal> getByBarcode(String barcode){
        return CompletableFuture.supplyAsync(() -> openFoodFactDao.get(barcode), queryExecutor);
    }

    CompletableFuture<List<MealWithOpenFoodFact>> getAll(){
        return CompletableFuture.supplyAsync(mealDao::getAll, queryExecutor);
    }
}