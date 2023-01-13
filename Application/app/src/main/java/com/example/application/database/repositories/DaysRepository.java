package com.example.application.database.repositories;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.dao.DayDao;
import com.example.application.database.models.Day;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.repositories.base.Repository;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class DaysRepository extends Repository {

    private final DayDao dayDao;

    public DaysRepository(Application application) {
        super(application);

        dayDao = database.dayDao();
    }

    public DaysRepository(CaloriesDatabase database) {
        super(database);

        dayDao = database.dayDao();
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
        return getOrCreateByDate(new Date());
    }

    public CompletableFuture<DayWithServings> getOrCreateByDate(Date date){
        return CompletableFuture.supplyAsync(() -> {
            DayWithServings day;
            try {
                day = getByDate(date).get();
            } catch (Exception e) {
                day = null;
            }

            if(day == null){
                dayDao.insert(new Day() {{
                    dayId = date;
                }});
            }

            try {
                return day == null ? getByDate(date).get() : day;
            } catch (Exception e) {
                return null;
            }
        }, queryExecutor);
    }

    public CompletableFuture<DayWithServings> getByDate(Date date){
        return CompletableFuture.supplyAsync(() -> dayDao.get(date), queryExecutor);
    }

    public CompletableFuture<DayWithServings> getDayByDate(Date date){
        return CompletableFuture.supplyAsync(() -> dayDao.getDayByDate(date), queryExecutor);
    }

}