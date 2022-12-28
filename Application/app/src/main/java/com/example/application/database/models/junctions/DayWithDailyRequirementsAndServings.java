package com.example.application.database.models.junctions;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.application.database.models.DailyRequirements;
import com.example.application.database.models.Day;

public class DayWithDailyRequirementsAndServings {
    @Embedded
    public Day day;


    @Relation(
            entity = DailyRequirements.class,
            parentColumn = "daily_requirements_id",
            entityColumn = "daily_requirements_id"
    )

    public DailyRequirements dailyRequirements;
}
