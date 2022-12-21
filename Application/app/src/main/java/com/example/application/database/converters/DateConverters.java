package com.example.application.database.converters;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class DateConverters {
    @TypeConverter
    public static Date fromTimestamp(Long hashedDate) {
        if(hashedDate == null){
            return null;
        }

        int day = (int) (hashedDate % 100);
        int month = (int) ((hashedDate / 100) % 100);
        int year = (int) (hashedDate / 10000);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Date date = cal.getTime();

        return date;
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        if(date == null){
           return null;
        }

        Calendar calendar = Calendar.getInstance();

        Long hashedDate = new Long(0);

        hashedDate += calendar.get(Calendar.YEAR)*10000;
        hashedDate += calendar.get(Calendar.MONTH)*100;
        hashedDate += calendar.get(Calendar.DAY_OF_MONTH);

        return hashedDate;
    }
}