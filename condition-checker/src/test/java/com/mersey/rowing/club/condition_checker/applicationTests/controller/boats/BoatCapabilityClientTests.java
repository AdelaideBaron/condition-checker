package com.mersey.rowing.club.condition_checker.applicationTests.controller.boats;

import static com.mersey.rowing.club.condition_checker.mockSetup.MockOpenWeatherResponseGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mersey.rowing.club.condition_checker.controller.boats.BoatCapabilityClient;
import com.mersey.rowing.club.condition_checker.mockSetup.MockOpenWeatherResponseGenerator;
import com.mersey.rowing.club.condition_checker.model.response.BoatsAllowed;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {"open-weather-api.key=test", "open-weather-api.baseUrl=http://localhost:5050"})
public class BoatCapabilityClientTests {

  @Autowired private BoatCapabilityClient boatCapabilityClient;

  private final MockOpenWeatherResponseGenerator oWResponseGenerator =
      new MockOpenWeatherResponseGenerator();

  private BoatsAllowed.BoatsAllowedBuilder boatsAllowedBuilder;
  private static BoatsAllowed allBoatsAllowed;
  private static BoatsAllowed allBoatsCancelled;

  @BeforeAll
  static void setup() {
    allBoatsCancelled =
        BoatsAllowed.builder().eight(false).quads(false).doubles(false).single(false).build();
    allBoatsAllowed =
        BoatsAllowed.builder().eight(true).quads(true).doubles(true).single(true).build();
  }

  @BeforeEach
  public void init() {
    boatsAllowedBuilder = BoatsAllowed.builder();
  }

  @Test
  void getBoatsAllowed_allConditionsWithinLimits_allowsAllBoats() {
    assertEquals(
        allBoatsAllowed, boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseAllGood()));
  }

  @ParameterizedTest
  @ValueSource(ints = {-10, -1, 28})
  void getBoatsAllowed_tempOutOfRange_cancelsAllBoats(int inputs) {
    assertEquals(
        allBoatsCancelled,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseFeelsLike(inputs)));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 20, 25})
  void getBoatsAllowed_tempInRange_allowsAllBoats(int inputs) {
    assertEquals(
        allBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseFeelsLike(inputs)));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 10, 12})
  void getBoatsAllowed_windInRange_allowsAllBoats(double inputs) {
    assertEquals(
        allBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindSpeed(inputs)));
  }

  @ParameterizedTest
  @ValueSource(ints = {18, 20})
  void getBoatsAllowed_windAboveRange_cancelsAllBoats(double inputs) {
    assertEquals(
        allBoatsCancelled,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindSpeed(inputs)));
  }

  @Test
  void getBoatsAllowed_windInRangeForAboveSinglesAndDoubles_allowsAllBoatsExceptSingle() {
    BoatsAllowed expectedBoatsAllowed =
        boatsAllowedBuilder.eight(true).quads(true).doubles(false).single(false).build();
    assertEquals(
        expectedBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindSpeed(13)));
  }

  @Test
  void getBoatsAllowed_windInRangeForAboveDoubles_allowsAllBoatsExceptSingleAndDouble() {
    BoatsAllowed expectedBoatsAllowed =
        boatsAllowedBuilder.eight(true).quads(true).doubles(false).single(false).build();
    assertEquals(
        expectedBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindSpeed(14)));
  }

  @Test
  void getBoatsAllowed_gustsAbove30_cancels() {
    BoatsAllowed expectedBoatsAllowed =
        boatsAllowedBuilder.eight(false).quads(false).doubles(false).single(false).build();
    assertEquals(
        expectedBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindGustSpeed(31)));
  }

  @Test
  void getBoatsAllowed_gustsUnder30_allows() {
    BoatsAllowed expectedBoatsAllowed =
        boatsAllowedBuilder.eight(true).quads(true).doubles(true).single(true).build();
    assertEquals(
        expectedBoatsAllowed,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseWindGustSpeed(29)));
  }

  @Test
  void getBoatsAllowed_weatherIdUnaccaptableXx_cancelsAll() {
    assertEquals(
        allBoatsCancelled,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseUnacceptableIdXx()));
  }

  @Test
  void getBoatsAllowed_weatherIdUnaccaptable_cancelsAll() {
    assertEquals(
        allBoatsCancelled,
        boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseUnacceptableIdSpecific()));
  }

  @Test
  void getBoatsAllowed_weatherIdExempt_allowsAllBoats() {
    assertEquals(
        allBoatsAllowed, boatCapabilityClient.getBoatsAllowed(getOpenWeatherResponseExemptId()));
  }
}
