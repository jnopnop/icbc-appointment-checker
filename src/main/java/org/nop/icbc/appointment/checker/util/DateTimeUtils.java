package org.nop.icbc.appointment.checker.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

    public static String today() {
        return LocalDateTime.now(TIME_ZONE).format(DateTimeFormatter.ISO_DATE);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(TIME_ZONE);
    }

    public static LocalDateTime toLocalDateTime(String date, String time) {
        return LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time));
    }
}
