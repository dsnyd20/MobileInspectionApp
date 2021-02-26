package com.example.mobilefieldinspector.database;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return  date == null ? null : date.getTime();
    }

    public static Long toTimestamp(String dateVal) {
        try {
            Date date = DateConverter.dateFormat.parse(dateVal + TimeZone.getDefault().getDisplayName());
            return date.getTime();
        }
        catch (ParseException e) {
            return 0l;
        }
    }

    public static long nowDate() {
        String currentDate = DateConverter.dateFormat.format(new Date());
        return toTimestamp(currentDate);
    }
}

