package ru.javawebinar.topjava.util;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T extends Comparable> boolean isBetweenHalfOpen(T lt, T start, T end) {
        if (lt.getClass() == start.getClass() && lt.getClass() == end.getClass()) {
            if (lt.getClass() == LocalDateTime.class || lt.getClass() == LocalTime.class) {
                return lt.compareTo(start) >= 0 && lt.compareTo(end) < 0;
            }
        }
        throw new InvalidParameterException("Invalid date parameters");
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}
