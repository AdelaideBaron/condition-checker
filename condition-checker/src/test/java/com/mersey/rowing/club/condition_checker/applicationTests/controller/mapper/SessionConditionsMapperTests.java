package com.mersey.rowing.club.condition_checker.applicationTests.controller.mapper;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mersey.rowing.club.condition_checker.controller.boats.BoatCapabilityClient;
import com.mersey.rowing.club.condition_checker.controller.mapper.SessionConditionsMapper;
import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.mockSetup.MockOpenWeatherResponseGenerator;
import com.mersey.rowing.club.condition_checker.model.StatusCodeObject;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.model.response.BoatsAllowed;
import com.mersey.rowing.club.condition_checker.model.response.SessionConditions;
import com.mersey.rowing.club.condition_checker.model.response.TimeType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@Slf4j
@SpringBootTest(properties = {"open-weather-api.key=test"})
public class SessionConditionsMapperTests {

  @MockBean private Clock clock;

  @MockBean private BoatCapabilityClient boatCapabilityClient;

  @MockBean private DateUtil dateUtil;

  @InjectMocks private SessionConditionsMapper sessionConditionsMapper;

  @Autowired private ObjectMapper mapper;

  private static OpenWeatherResponse MOCK_OW_RESPONSE =
      MockOpenWeatherResponseGenerator.getOpenWeatherResponseAllGood();

  private static final String EXPECTED_RESPONSE =
      """
          {
          "status": "200 OK",
          "weather_conditions": {
          "description": "clear sky",
          "temp_feels_like": 10,
          "wind_speed": 2
          },
          "boats_allowed": {
          "single": true,
          "doubles": true,
          "quads": true,
          "eight": true
          }}
          """;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    Instant fixedInstant = Instant.parse("2024-07-21T12:50:00Z");
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.of("Europe/London"));

    BoatsAllowed mockBoatsAllowed =
        BoatsAllowed.builder().doubles(true).single(true).quads(true).eight(true).build();
    when(boatCapabilityClient.getBoatsAllowed(MOCK_OW_RESPONSE)).thenReturn(mockBoatsAllowed);

    when(dateUtil.getDatetimeFromEpochSeconds(1721581200L)).thenReturn("17/06/2024 20:46");
  }

  @Test
  void mapFromStatusCodeObject_validOpenWeatherResponse_mapsAsExpected() {
    StatusCodeObject statusCodeObject = new StatusCodeObject(HttpStatus.OK, MOCK_OW_RESPONSE);

    SessionConditions actualSessionConditions =
        sessionConditionsMapper.mapFromStatusCodeObject(
            statusCodeObject,
            UUID.fromString("5db375dc-28dc-4c43-a081-eeec53e19556"),
            TimeType.SESSION_START);

    try {
      String sessionConditionsJson = mapper.writeValueAsString(actualSessionConditions);
      JSONAssert.assertEquals(EXPECTED_RESPONSE, sessionConditionsJson, false);
    } catch (AssertionError e) {
      log.error("JSON objects are not equal: " + e.getMessage());
      throw e;
    } catch (JSONException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void mapFromUnhappyOwResponse_401Response_mapsToSessionResponse() {
    StatusCodeObject statusCodeObject =
        new StatusCodeObject(HttpStatus.UNAUTHORIZED, "17/06/2024 20:46");
    SessionConditions actualSessionConditions =
        sessionConditionsMapper.mapFromStatusCodeObject(
            statusCodeObject, UUID.randomUUID(), TimeType.ERROR);
    SessionConditions expectedSessionConditionsResponse =
        SessionConditions.builder()
            .status(HttpStatus.UNAUTHORIZED.toString())
            .date("17/06/2024 20:46")
            .build();
    try {
      String sessionConditionsJson = mapper.writeValueAsString(actualSessionConditions);
      String expectedSessionConditionsJson =
          mapper.writeValueAsString(expectedSessionConditionsResponse);
      JSONAssert.assertEquals(expectedSessionConditionsJson, sessionConditionsJson, false);
    } catch (AssertionError e) {
      log.error("JSON objects are not equal: " + e.getMessage());
      throw e;
    } catch (JSONException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
