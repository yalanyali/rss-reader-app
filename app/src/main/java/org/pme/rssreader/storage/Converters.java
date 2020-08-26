package org.pme.rssreader.storage;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Converter to store Date as timestamp and retrieve as Date back.
 * Mainly used in order to get lists sorted by date.
 */
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
