package com.example.usecase.usecase.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

public class DateUtil {

    public static boolean isToday(LocalDateTime compareDate) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        LocalDateTime endOfOfToday = LocalDate.now().atTime(23, 59, 59);

        return (startOfToday.isEqual(compareDate) || startOfToday.isBefore(compareDate))
                && (endOfOfToday.isEqual(compareDate) || endOfOfToday.isAfter(compareDate));
    }

    public static LocalDateTime getStartOfMinusDay(int minusDay) {
        return LocalDate.now().minusDays(minusDay).atStartOfDay();
    }
}
