package com.mersey.rowing.club.condition_checker.controller.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtil {
    private static final LocalDate dateToday = LocalDate.now();
    private static final LocalDate dateTomorrow = LocalDate.now().plusDays(1);
    private final static String sixMorning = "06:00";
    private final static String sixEvening = "18:00";
    private static final LocalTime currentTime = LocalTime.now();
    private static final ZoneId zoneId = ZoneId.of("Europe/London");
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // think about try catches later and what to do in case of invalid input
    // Take in datetime string in the format "dd/MM/yyyy HH:mm"

    public static long callApiDateAndTimeSupplied(String date, String time) {
        log.info("both date and time supplied");
        // Return epoch time as a long with (date, time)
        return getEpochTimeAsLong(date, time);
    }

    public static long[] callApiTimeOnlyIsNull(String date) {
        log.info("time only is null");

        // Call getEpochTimeAsLong method with sixMorning, and sixEvening
        long morningEpoch = getEpochTimeAsLong(date, sixMorning);
        long eveningEpoch = getEpochTimeAsLong(date, sixEvening);

        // Return epochs as a list
        return new long[] {morningEpoch, eveningEpoch};
    }

    public static long getEpochTimeAsLong(String date, String time) {
        LocalDateTime dateTime1 = LocalDateTime.parse(date + " " + time, dtf);
        return dateTime1.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long callApiDateOnlyIsNull(String time) {
        log.info("date only is null");
        // Call epochTimeAsLong using dateToday, and the time given
        return getEpochTimeAsLong(dateToday.toString(), time);
    }

    public static long[] callApiDateNullAndTimeNull() {
        // Needs splitting up
        log.info("date and time are null OR time is null and date == dateToday");
        // Check to see if current time is after 6AM
        if (currentTime.isAfter(LocalTime.parse(sixMorning)) && currentTime.isBefore(LocalTime.parse(sixEvening))) {
            log.info("current time is: " + currentTime);
            // Call API with (dateToday, sixEvening)
            long eveningEpoch = getEpochTimeAsLong(dateToday.toString(), sixEvening);
            // Call API with (dateTomorrow, sixMorning)
            long morningEpoch = getEpochTimeAsLong(dateTomorrow.toString(), sixMorning);

            return new long[] {eveningEpoch, morningEpoch};
        } else if (currentTime.isBefore(LocalTime.parse(sixMorning))) {
            // Call API with (dateToday, sixMorning)
            long morningEpoch = getEpochTimeAsLong(dateToday.toString(), sixMorning);
            // Call API with (dateToday, sixEvening)
            long eveningEpoch = getEpochTimeAsLong(dateToday.toString(), sixEvening);

            return new long[] {morningEpoch, eveningEpoch};
        } else {
            // Call API with (dateTomorrow, sixMorning
            long morningEpoch = getEpochTimeAsLong(dateTomorrow.toString(), sixMorning);
            // Call API with (dateTomorrow, sixEvening
            long eveningEpoch = getEpochTimeAsLong(dateTomorrow.toString(), sixEvening);

            return new long[] {morningEpoch, eveningEpoch};
        }
    }
}