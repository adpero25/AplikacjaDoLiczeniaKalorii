package com.example.application.database.models;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.application.database.converters.DateConverters;
import com.example.application.database.converters.NormalDateConverter;

import java.util.Date;

@TypeConverters({NormalDateConverter.class})
@Entity(tableName="day",
        foreignKeys = {
                @ForeignKey(
                        onDelete = SET_NULL,
                        entity = DailyRequirements.class,
                        parentColumns = "daily_requirements_id",
                        childColumns = "daily_requirements_id"
                )
        },
        indices = {
                @Index(
                        value = "daily_requirements_id"
                )
        }
)
public class Day {
    @PrimaryKey()
    @ColumnInfo(name = "day_id")
    public Date dayId;

    @ColumnInfo(name = "glasses_of_water")
    @NonNull
    public Integer glassesOfWater = new Integer(0);

    @ColumnInfo(name = "steps_count")
    @NonNull
    public Integer stepsCount = new Integer(0);

    @ColumnInfo(name = "total_distance", defaultValue = "0.0")
    @NonNull
    public Double totalDistance = 0.0;

    @ColumnInfo(name = "burned_calories", defaultValue = "0.0")
    @NonNull
    public Double burnedCalories = 0.0;

    @NonNull
    @ColumnInfo(name = "has_practiced")
    public Boolean hasPracticed = new Boolean(false);

    @ColumnInfo(name = "daily_requirements_id")
    public Long dailyRequirementsId;
}

