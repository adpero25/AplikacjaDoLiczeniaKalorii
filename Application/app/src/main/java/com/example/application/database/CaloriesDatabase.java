package com.example.application.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.application.database.converters.ActivityIndicatorConverter;
import com.example.application.database.converters.DateConverters;
import com.example.application.database.converters.MassTargetConverter;
import com.example.application.database.converters.MealTypeConverter;
import com.example.application.database.dao.CategoryDao;
import com.example.application.database.dao.DailyRequirementsDao;
import com.example.application.database.dao.DayDao;
import com.example.application.database.dao.MealDao;
import com.example.application.database.dao.OpenFoodFactDao;
import com.example.application.database.dao.ServingDao;
import com.example.application.database.migrations.MigrationFrom2To3;
import com.example.application.database.migrations.MigrationFrom6To7;
import com.example.application.database.models.Category;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.Day;
import com.example.application.database.models.Meal;
import com.example.application.database.models.OpenFoodFact;
import com.example.application.database.models.Serving;

@TypeConverters({ActivityIndicatorConverter.class, MassTargetConverter.class, MealTypeConverter.class, DateConverters.class})
@androidx.room.Database(
        version = 7,
        exportSchema = true,
        entities = {Day.class, Serving.class, OpenFoodFact.class, Meal.class, Category.class, DailyRequirements.class},
        autoMigrations = {
                @AutoMigration(
                        from = 1,
                        to = 2
                ),
                @AutoMigration(
                        from = 2,
                        to = 3,
                        spec = MigrationFrom2To3.class
                ),
                @AutoMigration(
                        from = 3,
                        to = 4
                ),
                @AutoMigration(
                        from = 4,
                        to = 5
                ),
                @AutoMigration(
                        from = 5,
                        to = 6
                ),
                @AutoMigration(
                        from = 6,
                        to = 7,
                        spec = MigrationFrom6To7.class
                )
        }
)
public abstract class CaloriesDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "calories_database";

    private static CaloriesDatabase databaseInstance;
    private static final Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseInstance.getQueryExecutor().execute(() -> {});
        }
    };

    public static CaloriesDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            CaloriesDatabase.class, DATABASE_NAME)
                    .addCallback(roomDatabaseCallback)
                    .allowMainThreadQueries()
                    .build();
        }
        return databaseInstance;
    }

    public abstract DayDao dayDao();

    public abstract MealDao mealDao();

    public abstract CategoryDao categoryDao();

    public abstract ServingDao servingDao();

    public abstract OpenFoodFactDao openFoodFactDao();

    public abstract DailyRequirementsDao dailyRequirementsDao();
}