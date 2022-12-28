package com.example.application.database.converters;

import androidx.room.TypeConverter;

import com.example.application.database.models.enums.MassTarget;

public class MassTargetConverter {

    @TypeConverter
    public static int getValue(MassTarget indicator){
        return indicator.getId();
    }

    @TypeConverter
    public static MassTarget getTarget(int id){
        return MassTarget.getTarget(id);
    }
}
