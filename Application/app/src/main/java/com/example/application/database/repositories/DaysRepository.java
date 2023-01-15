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
import java.util.Date;
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

    public CompletableFuture<DayWithServings> getOrCreateToday(){
        return getOrCreateByDate(LocalDate.now());
    }

    public CompletableFuture<DayWithServings> getOrCreateByDate(LocalDate date){
        return CompletableFuture.supplyAsync(() -> {
            DayWithServings day;
            try {
                day = getByDate(date).get();
            } catch (Exception e) {
                day = null;
            }

            if(day == null){
                List<DailyRequirements> dailyRequirements = dailyRequirementsDao.listDailyRequirements();
                try{
                    dayDao.insert(new Day() {{
                        dayId = date;
                        dailyRequirementsId = dailyRequirements.get(dailyRequirements.size() - 1).requirementId;
                    }});
                }
                catch (Exception e){
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

    public CompletableFuture<DayWithServings> getByDate(LocalDate date){
        return CompletableFuture.supplyAsync(() -> dayDao.get(date), queryExecutor);
    }

    public CompletableFuture<List<Day>> getAllDays(){
        return CompletableFuture.supplyAsync(dayDao::getAll, queryExecutor);
    }

    public CompletableFuture<DailyRequirements> getDayDailyRequirements(LocalDate date){
        return CompletableFuture.supplyAsync( () -> {
                    DailyRequirements requirement = null;
                    DayWithServings day;
                    try {
                        day = getByDate(date).get();
                    } catch (Exception e) {
                        day = null;
                    }

                    if(day == null){
                        List<DailyRequirements> dailyRequirements = dailyRequirementsDao.listDailyRequirements();
                        try{
                           requirement = dailyRequirements.get(dailyRequirements.size() - 1);
                            dayDao.insert(new Day() {{
                                dayId = date;
                                dailyRequirementsId = dailyRequirements.get(dailyRequirements.size() - 1).requirementId;
                            }});
                        }
                        catch (Exception e){
                            dayDao.insert(new Day() {{
                                dayId = date;
                            }});
                        }

                    }

                    if(requirement == null){
                        try{
                           return dailyRequirementsDao.getById(day.day.dailyRequirementsId);
                        }catch (Exception e){
                            return new DailyRequirements(){{
                                nutritionalValuesTarget.fats = 0d;
                                nutritionalValuesTarget.carbohydrates = 0d;
                                nutritionalValuesTarget.proteins = 0d;
                                nutritionalValuesTarget.calories = 0d;
                            }};
                        }
                    }

                    return  requirement;

                }
        );
    }

}