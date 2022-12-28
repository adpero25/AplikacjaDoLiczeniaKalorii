package com.example.application.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.application.database.converters.DateConverters;

import java.util.Date;

@TypeConverters({DateConverters.class})
@Entity(tableName="day")
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

    @NonNull
    public Boolean hasPracticed = new Boolean(false);
}

