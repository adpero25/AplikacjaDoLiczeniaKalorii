package com.example.application.database.converters;

import androidx.room.TypeConverter;

import com.example.application.database.models.enums.ActivityIndicator;

public class ActivityIndicatorConverter {
    @TypeConverter
    public static int getValue(ActivityIndicator indicator) {
        return indicator.getId();
    }

    @TypeConverter
    public static ActivityIndicator getActivity(int id) {
        return ActivityIndicator.getActivity(id);
    }

}
