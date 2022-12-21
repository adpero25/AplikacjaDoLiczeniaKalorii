package com.example.application.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.application.database.dao.DayDao;
import com.example.application.database.dao.MealDao;
import com.example.application.database.dao.OpenFoodFactDao;
import com.example.application.database.models.Category;
import com.example.application.database.models.Day;
import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Day.class, Serving.class, OpenFoodFact.class, Meal.class, Category.class}, version = 1, exportSchema = false)
public abstract class CaloriesDatabase extends RoomDatabase {

    private static final int PROTEINS = 0;


    private static CaloriesDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract DayDao dayDao();
    public abstract MealDao mealDao();
    public abstract OpenFoodFactDao openFoodFactDao();

    public static CaloriesDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            CaloriesDatabase.class, "calories_database_test_9")
                    .addCallback(roomDatabaseCallback)
                    .build();
        }
        return databaseInstance;
    }


    private static final Callback roomDatabaseCallback = new RoomDatabase.Callback() {

     @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                DayDao dao = databaseInstance.dayDao();

                Day day = new Day() {{
                    dayId = new Date();
                }};
                dao.insert(day);

                MealDao dao2 = databaseInstance.mealDao();
                OpenFoodFactDao dao3 = databaseInstance.openFoodFactDao();
                Meal meal = new Meal(){{
                        name="meal name";
                    }};
                OpenFoodFact openFoodFact = new OpenFoodFact(){{
                        code="code 123213";
                        mealId = dao2.insert(meal);
                    }};

                dao3.insert(openFoodFact);
            });
        }
    };
}