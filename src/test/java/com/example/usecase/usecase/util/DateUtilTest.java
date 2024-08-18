package com.example.usecase.usecase.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DateUtilTest {

    @Test
    @DisplayName("오늘 날짜 기준 - 00:00:00 테스트")
    void today_success_start_date_test() {
        LocalDateTime test = LocalDate.now().atStartOfDay();

        boolean result = DateUtil.isToday(test);

        assertEquals(0, test.getHour());
        assertEquals(0, test.getMinute());
        assertEquals(0, test.getSecond());

        assertEquals(true, result);
    }

    @Test
    @DisplayName("오늘 날짜 기준 - 00:00:01 테스트")
    void today_success_start_date_plus_one_seconds_test() {
        LocalDateTime test = LocalDate.now().atTime(0, 0, 1);

        boolean result = DateUtil.isToday(test);

        assertEquals(0, test.getHour());
        assertEquals(0, test.getMinute());
        assertEquals(1, test.getSecond());

        assertEquals(true, result);
    }

    @Test
    @DisplayName("오늘 날짜 기준 - 23:59:59 테스트")
    void today_success_end_date_test() {
        LocalDateTime test = LocalDate.now().atTime(23, 59, 59);

        boolean result = DateUtil.isToday(test);

        assertEquals(23, test.getHour());
        assertEquals(59, test.getMinute());
        assertEquals(59, test.getSecond());
        assertEquals(true, result);
    }

    @Test
    @DisplayName("오늘 날짜 기준 - 23:59:58 테스트")
    void today_success_end_date_minus_one_seconds_test() {
        LocalDateTime test = LocalDate.now().atTime(23, 59, 58);

        boolean result = DateUtil.isToday(test);

        assertEquals(23, test.getHour());
        assertEquals(59, test.getMinute());
        assertEquals(58, test.getSecond());

        assertEquals(true, result);
    }

    @Test
    @DisplayName("오늘 날짜 기준 - 전날 23:59:59 테스트")
    void yesterday_fail_end_date_test() {
        LocalDateTime test = LocalDate.now()
                .minusDays(1).atTime(23, 59, 59);

        boolean result = DateUtil.isToday(test);

        assertEquals(23, test.getHour());
        assertEquals(59, test.getMinute());
        assertEquals(59, test.getSecond());
        assertEquals(false, result);
    }

    @Test
    @DisplayName("오늘 날짜 기준 - 다음날 00:00:00 테스트")
    void tomorrow_fail_start_date_test() {
        LocalDateTime test = LocalDate.now()
                .plusDays(1).atStartOfDay();

        boolean result = DateUtil.isToday(test);

        assertEquals(0, test.getHour());
        assertEquals(0, test.getMinute());
        assertEquals(0, test.getSecond());
        assertEquals(false, result);
    }

    @Test
    @DisplayName("30일 이전 일자 구하기")
    void minus_30_days_success_test() {
        LocalDateTime expect = LocalDate.now().minusDays(30)
                .atTime(0, 0, 0);

        LocalDateTime actual = DateUtil.getStartOfMinusDay(30);

        assertEquals(expect.getHour(), actual.getHour());
        assertEquals(expect.getMinute(), actual.getMinute());
        assertEquals(expect.getSecond(), actual.getSecond());
        assertEquals(expect.getYear(), actual.getYear());
        assertEquals(expect.getMonthValue(), actual.getMonthValue());
        assertEquals(expect.getDayOfYear(), actual.getDayOfYear());
    }
}