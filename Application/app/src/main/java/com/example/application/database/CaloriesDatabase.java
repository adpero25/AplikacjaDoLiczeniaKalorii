package com.example.application.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.application.database.dao.CategoryDao;
import com.example.application.database.dao.DayDao;
import com.example.application.database.dao.MealDao;
import com.example.application.database.dao.OpenFoodFactDao;
import com.example.application.database.dao.ServingDao;
import com.example.application.database.models.Category;
import com.example.application.database.models.Day;
import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;
import com.example.application.database.models.Serving;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(
        version = 2,
        exportSchema = true,
        entities = {Day.class, Serving.class, OpenFoodFact.class, Meal.class, Category.class},
        autoMigrations = {
                @AutoMigration(
                        from = 1,
                        to = 2
                )
        }
)
public abstract class CaloriesDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "calories_database";

    private static CaloriesDatabase databaseInstance;

    public abstract DayDao dayDao();
    public abstract MealDao mealDao();
    public abstract CategoryDao categoryDao();
    public abstract ServingDao servingDao();
    public abstract OpenFoodFactDao openFoodFactDao();

    public static CaloriesDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            CaloriesDatabase.class, DATABASE_NAME)
                    .addCallback(roomDatabaseCallback)
                    .build();
        }
        return databaseInstance;
    }


    private static final Callback roomDatabaseCallback = new RoomDatabase.Callback() {

     @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseInstance.getQueryExecutor().execute(() -> {
                //mb add some seeding later
            });
        }
    };
}