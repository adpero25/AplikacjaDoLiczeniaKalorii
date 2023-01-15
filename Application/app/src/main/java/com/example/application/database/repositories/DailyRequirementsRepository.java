package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.DailyRequirementsDao;
import com.example.application.database.dao.DayDao;
import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.repositories.base.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DailyRequirementsRepository extends Repository {

    private final DailyRequirementsDao dailyRequirementsDao;
    private final DayDao dayDao;

    public DailyRequirementsRepository(Application application) {
        super(application);

        dailyRequirementsDao = database.dailyRequirementsDao();
        dayDao = database.dayDao();
    }

    public DailyRequirementsRepository(CaloriesDatabase database) {
        super(database);

        dailyRequirementsDao = database.dailyRequirementsDao();
        dayDao = database.dayDao();
    }


    public CompletableFuture<Long> insert(DailyRequirements dailyRequirements) {
        return CompletableFuture.supplyAsync(() ->
        {
            Long requirementId = dailyRequirementsDao.insert(dailyRequirements);
            DayWithServings dayWithServings = dayDao.getDayByDate(dailyRequirements.entryDate);
            dayWithServings.day.dailyRequirementsId = requirementId;
            dayDao.update(dayWithServings.day);
            return requirementId;
        });
    }

    public void insertOrUpdate(DailyRequirements dailyRequirements) {
        DailyRequirements requirements = dailyRequirementsDao.getByDate(dailyRequirements.entryDate);

        if (requirements == null) {
            insert(dailyRequirements);
        } else {
            dailyRequirements.requirementId = requirements.requirementId;
            update(dailyRequirements);
        }
    }

    public void update(DailyRequirements dailyRequirements) {
        queryExecutor.execute(() -> dailyRequirementsDao.update(dailyRequirements));
    }

    public void delete(DailyRequirements dailyRequirements) {
        queryExecutor.execute(() -> dailyRequirementsDao.delete(dailyRequirements));
    }

    public CompletableFuture<List<DailyRequirements>> getAll() {
        return CompletableFuture.supplyAsync(dailyRequirementsDao::listDailyRequirements, queryExecutor);
    }

    public CompletableFuture<DailyRequirements> getLastRequirement(LocalDate date){
        return CompletableFuture.supplyAsync(() -> dailyRequirementsDao.getLastRequirement(date), queryExecutor);
    }
}
