package com.mersey.rowing.club.condition_checker.controller;

import com.mersey.rowing.club.condition_checker.controller.mapper.SessionConditionsMapper;
import com.mersey.rowing.club.condition_checker.controller.openweather.OpenWeatherApiClient;
import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.model.response.ConditionResponse;
import com.mersey.rowing.club.condition_checker.model.response.SessionConditions;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ConditionController {

  private final LocalDate dateToday = LocalDate.now();
  @Autowired private OpenWeatherApiClient owac;

  @Autowired private DateUtil dateUtil;

  @Autowired SessionConditionsMapper sessionConditionsMapper;

  @GetMapping("/get_conditions")
  public ResponseEntity<ConditionResponse> getConditions(
      @RequestHeader(value = "date", required = false) String date,
      @RequestHeader(value = "time", required = false) String time) {
    // date = dd/MM/yyyy, // time = HH:mm
    // TODO before calling DateUtil, should have validation of user input

    List<SessionConditions> sessionConditionsList = new ArrayList<>();

    long[] epochs = getEpochBasedOnLogic(date, time);
    for (long epoch : epochs) {
      SessionConditions thisTimeResponse =
          sessionConditionsMapper.mapFromStatusCodeObject(owac.getOpenWeatherAPIResponse(epoch));
      sessionConditionsList.add(thisTimeResponse);
    }

    ConditionResponse response =
        ConditionResponse.builder().sessionConditions(sessionConditionsList).build();
    return ResponseEntity.ok(response);
  }

  private long[] getEpochBasedOnLogic(String date, String time) {
    if ((date == null && time == null) || (dateToday.toString().equals(date) && time == null)) {
      return dateUtil.getEpochsDateNullAndTimeNull();
    } else if (date == null) {
      return dateUtil.getEpochsDateOnlyIsNull(time);
    } else if (time == null) {
      return dateUtil.getEpochsTimeOnlyIsNull(date);
    } else {
      return dateUtil.getEpochsDateAndTimeSupplied(date, time);
    }
  }
}
