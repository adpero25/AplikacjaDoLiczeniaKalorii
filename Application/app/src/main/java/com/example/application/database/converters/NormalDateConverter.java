package com.example.application.database.converters;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class NormalDateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long hashedDate) {
        if(hashedDate == null){
            return null;
        }

        int day = (int) (hashedDate % 100);
        int month = (int) ((hashedDate / 100) % 100);
        int year = (int) (hashedDate / 10000);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day,0,0,0);
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

        hashedDate += (date.getYear() + 1900) * 10000;
        hashedDate += date.getMonth() * 100; //...
        hashedDate += date.getDate();

        return hashedDate;
    }
}
