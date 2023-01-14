package com.example.application.database.converters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverters {
    @TypeConverter
    public static LocalDate fromTimestamp(Long dateAsLong) {
        if(dateAsLong == null){
            return null;
        }

        return Instant.ofEpochMilli(dateAsLong).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        if(date == null){
            return null;
        }

        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}