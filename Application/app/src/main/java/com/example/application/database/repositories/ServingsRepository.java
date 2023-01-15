package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.ServingDao;
import com.example.application.database.models.Day;
import com.example.application.database.models.Meal;
import com.example.application.database.models.Serving;
import com.example.application.database.models.enums.MealType;
import com.example.application.database.models.junctions.ServingWithMeal;
import com.example.application.database.repositories.base.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServingsRepository extends Repository {

    private final ServingDao servingDao;

    public ServingsRepository(Application application) {
        super(application);

        servingDao = database.servingDao();
    }

    public ServingsRepository(CaloriesDatabase database) {
        super(database);

        servingDao = database.servingDao();
    }

    void insert(Serving serving) {
        queryExecutor.execute(() -> servingDao.insert(serving));
    }

    public void insert(Day day, Meal meal, double protionSize, MealType type) {
        insert(new Serving() {{
            dayId = day.dayId;
            mealId = meal.mealId;
            servingSize = protionSize;
            mealType = type;
        }});
    }

    public void update(Serving serving) {
        queryExecutor.execute(() -> servingDao.update(serving));
    }

    public void delete(Serving serving) {
        queryExecutor.execute(() -> servingDao.delete(serving));
    }

    public CompletableFuture<List<ServingWithMeal>> getByDate(LocalDate date) {
        return CompletableFuture.supplyAsync(() -> servingDao.get(date), queryExecutor);
    }
}