package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.DailyRequirementsDao;
import com.example.application.database.dao.DayDao;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.repositories.base.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DaysRepository extends Repository {

    private final DayDao dayDao;
    private final DailyRequirementsDao dailyRequirementsDao;

    public DaysRepository(Application application) {
        super(application);

        dayDao = database.dayDao();
        dailyRequirementsDao = database.dailyRequirementsDao();
    }

    public DaysRepository(CaloriesDatabase database) {
        super(database);

        dayDao = database.dayDao();
        dailyRequirementsDao = database.dailyRequirementsDao();

    }

    private void insert(Day day) {
        queryExecutor.execute(() -> dayDao.insert(day));
    }

    public void update(Day day) {
        queryExecutor.execute(() -> dayDao.update(day));
    }

    public void delete(Day day) {
        queryExecutor.execute(() -> dayDao.delete(day));
    }

    public CompletableFuture<DayWithServings> getOrCreateToday() {
        return getOrCreateByDate(LocalDate.now());
    }

    public CompletableFuture<DayWithServings> getOrCreateByDate(LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            DayWithServings day;
            try {
                day = getByDate(date).get();
            } catch (Exception e) {
                day = null;
            }

            if (day == null) {
                DailyRequirements requirements = dailyRequirementsDao.getLastRequirement(date);
                try {
                    if (requirements != null) {
                        dayDao.insert(new Day() {{
                            dayId = date;
                            dailyRequirementsId = requirements.requirementId;
                        }});
                    } else {
                        dayDao.insert(new Day() {{
                            dayId = date;
                        }});
                    }
                } catch (Exception e) {
                    dayDao.insert(new Day() {{
                        dayId = date;
                    }});
                }
            }

            try {
                return day == null ? getByDate(date).get() : day;
            } catch (Exception e) {
                return null;
            }
        }, queryExecutor);
    }

    public CompletableFuture<DayWithServings> getByDate(LocalDate date) {
        return CompletableFuture.supplyAsync(() -> dayDao.get(date), queryExecutor);
    }

    public CompletableFuture<List<Day>> getAllDays() {
        return CompletableFuture.supplyAsync(dayDao::getAll, queryExecutor);
    }

    public CompletableFuture<DailyRequirements> getDayDailyRequirements(LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
                    DayWithServings day;
                    try {
                        day = getOrCreateByDate(date).get();
                    } catch (Exception e) {
                        return null;
                    }

                    try {
                        DailyRequirements requirements = dailyRequirementsDao.getById(day.day.dailyRequirementsId);
                        if (requirements == null) {
                            return new DailyRequirements() {{

                                nutritionalValuesTarget.fats = 48d;
                                nutritionalValuesTarget.carbohydrates = 100d;
                                nutritionalValuesTarget.proteins = 70d;
                                nutritionalValuesTarget.calories = 2400d;
                            }};
                        }
                        return requirements;
                    } catch (Exception e) {
                        return new DailyRequirements() {{

                            nutritionalValuesTarget.fats = 48d;
                            nutritionalValuesTarget.carbohydrates = 100d;
                            nutritionalValuesTarget.proteins = 70d;
                            nutritionalValuesTarget.calories = 2400d;
                        }};
                    }

                }
        );
    }

}