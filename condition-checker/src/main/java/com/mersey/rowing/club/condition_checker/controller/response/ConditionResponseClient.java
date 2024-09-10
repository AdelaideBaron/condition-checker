package com.mersey.rowing.club.condition_checker.controller.response;

import com.mersey.rowing.club.condition_checker.controller.mapper.SessionConditionsMapper;
import com.mersey.rowing.club.condition_checker.controller.openweather.APICounter;
import com.mersey.rowing.club.condition_checker.controller.openweather.OpenWeatherApiClient;
import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.model.StatusCodeObject;
import com.mersey.rowing.club.condition_checker.model.response.ConditionResponse;
import com.mersey.rowing.club.condition_checker.model.response.SessionConditions;
import com.mersey.rowing.club.condition_checker.model.response.TimeType;
import java.time.LocalDate;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConditionResponseClient {

  private final LocalDate dateToday = LocalDate.now();

  @Autowired private OpenWeatherApiClient owac;

  @Autowired private DateUtil dateUtil;

  @Autowired SessionConditionsMapper sessionConditionsMapper;

  @Autowired APICounter apiCounter;

  public ResponseEntity<ConditionResponse> getConditionResponseFromDateTime( // Todo unit test this
      String date, String time) {
    List<SessionConditions> sessionConditionsList = new ArrayList<>();
    Map<Long, long[]> epochs = getEpochBasedOnLogicWithSessionStart(date, time);

    for (Map.Entry<Long, long[]> entry : epochs.entrySet()) {
      UUID sessionUuid = UUID.randomUUID();
      if (apiCounter.checkIfAPILimitIsReached()) {
        break;
      }

      // for the start
      Long key = entry.getKey();
      long startEpoch = key.longValue();
      StatusCodeObject statusCodeObjectStart = owac.getOpenWeatherAPIResponse(startEpoch);
      sessionConditionsList.add(
          sessionConditionsMapper.mapFromStatusCodeObject(
              statusCodeObjectStart, sessionUuid, TimeType.SESSION_START));

      long[] value = entry.getValue();
      // for mid session
      long midSessionEpoch = value[0];
      StatusCodeObject statusCodeObjectmidSession = owac.getOpenWeatherAPIResponse(midSessionEpoch);
      sessionConditionsList.add(
          sessionConditionsMapper.mapFromStatusCodeObject(
              statusCodeObjectmidSession, sessionUuid, TimeType.DURING_SESSION));

      // for end session
      long endOfSessionEpoch = value[1];
      StatusCodeObject statusCodeObjectEndOfSession =
          owac.getOpenWeatherAPIResponse(endOfSessionEpoch);
      sessionConditionsList.add(
          sessionConditionsMapper.mapFromStatusCodeObject(
              statusCodeObjectEndOfSession, sessionUuid, TimeType.SESSION_END));
    }

    ConditionResponse response =
        ConditionResponse.builder().sessionConditions(sessionConditionsList).build();
    return ResponseEntity.ok(response);
  }

  private Map<Long, long[]> getEpochBasedOnLogicWithSessionStart(String date, String time) {
    List<Long> sessionStarts = new ArrayList<>();
    Map<Long, long[]> map = new HashMap<>();
    if ((date == null && time == null) || (dateToday.toString().equals(date) && time == null)) {
      sessionStarts = dateUtil.getEpochsDateNullAndTimeNullASLIST();
    } else if (date == null) {
      sessionStarts = dateUtil.getEpochsDateOnlyIsNullASLIST(time);
    } else if (time == null) {
      sessionStarts = dateUtil.getEpochsTimeOnlyIsNullASLIST(date);
    } else {
      sessionStarts = dateUtil.getEpochsDateAndTimeSuppliedASLIST(date, time);
    }
    for (Long startTime : sessionStarts) {
      map.put(startTime, dateUtil.getEpochPlusOneAndTwoHours(startTime));
    }
    return map;
  }
}
