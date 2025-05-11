package com.mersey.rowing.club.condition_checker.controller.openweather;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OWCallCounter {

  private static String currentDate =
      LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
  private static int counter = 0;

  public static void checkDateAndAddCounter() {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

    if (currentDate.equals(today)) {
      counter++;
    } else {
      currentDate = today;
      counter = 1;
    }

    if (counter > 900 && counter < 1000) {
      log.warn("There are only {} OW API calls left", 1000 - counter);
    } else if (counter > 1000) {
      log.error("There are no OW API calls left for today");
    } else {
      log.debug("There are a total of: {} OW API calls left for today", 1000 - counter);
    }

    log.debug("Updated in-memory state: Date = {}, Counter = {}", currentDate, counter);
  }
}
