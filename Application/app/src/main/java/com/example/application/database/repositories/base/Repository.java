package com.example.application.database.repositories.base;

import android.app.Application;

import com.example.application.database.CaloriesDatabase;

import java.util.concurrent.Executor;

public abstract class Repository {
    protected CaloriesDatabase database;
    protected Executor queryExecutor;

    protected Repository(Application application) {
        this(CaloriesDatabase.getDatabase(application));
    }

    protected Repository(CaloriesDatabase database) {
        this.database = database;
        queryExecutor = database.getQueryExecutor();
    }

    protected Repository() {
    }
}
