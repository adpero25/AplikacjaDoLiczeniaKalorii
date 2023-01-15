package com.example.application.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.models.enums.ActivityIndicator;
import com.example.application.database.models.enums.MassTarget;

import java.time.LocalDate;

@Entity(tableName = "daily_requirements")
@TypeConverters({DateConverters.class})
public class DailyRequirements {

    @PrimaryKey
    @ColumnInfo(name = "daily_requirements_id")
    public Long requirementId;

    @NonNull
    @Embedded
    public NutritionalValues nutritionalValuesTarget = new NutritionalValues();

    @NonNull
    public Double height = 0.0;

    @NonNull
    public Double weight = 0.0;

    @NonNull
    @ColumnInfo(name = "activity_indicator")
    public ActivityIndicator activityIndicator = ActivityIndicator.LightlyActivity;

    @NonNull
    public Boolean isMale = false;

    @NonNull
    public Integer age = -1;

    @NonNull
    @ColumnInfo(name = "mass_target")
    public MassTarget massTarget = MassTarget.Maintenance;

    @ColumnInfo(name = "entry_date")
    public LocalDate entryDate;
}
