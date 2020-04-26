package com.leeyaonan.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Rot
 * @date 2020/4/26 12:00
 */
public class DateUtils {

    public static String localDateTimeToString(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
