package com.gaggledemo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Central place for all date-related logic
 */
public class DateUtil {
    public static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Gives back a string formatted in yyyy-MM-dd'T'HH:mm:ss.SSS format
     */
    public static String formatLocalDateTime(LocalDateTime ldt) {
        return TIMESTAMP_FORMAT.format(ldt);
    }
}
