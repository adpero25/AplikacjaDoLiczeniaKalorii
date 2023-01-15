package com.example.application.database.converters;

import androidx.room.TypeConverter;

import com.example.application.database.models.enums.MealType;

public class MealTypeConverter {

    @TypeConverter
    public static int getValue(MealType indicator) {
        return indicator.getId();
    }

    @TypeConverter
    public static MealType getTarget(int id) {
        return MealType.getMeal(id);
    }

}
