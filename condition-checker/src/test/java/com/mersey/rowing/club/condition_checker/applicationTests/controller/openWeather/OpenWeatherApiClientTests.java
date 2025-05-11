package com.mersey.rowing.club.condition_checker.applicationTests.controller.openWeather;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mersey.rowing.club.condition_checker.applicationTests.WireMockSpecificDtBaseTests;
import com.mersey.rowing.club.condition_checker.controller.openweather.OpenWeatherApiClient;
import com.mersey.rowing.club.condition_checker.model.StatusCodeObject;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.utils.TestOpenWeatherUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Tag("wiremock")
public class OpenWeatherApiClientTests extends WireMockSpecificDtBaseTests {

  @Autowired private OpenWeatherApiClient openWeatherApiClient;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void getOpenWeatherAPIResponse_apiGivesExpectedResponse_mapsToOWResponse_withCorrectDT()
      throws JSONException, JsonProcessingException {
    int testDateTime = 1720626363;

    setupWiremockMappingForDt(testDateTime);

    OpenWeatherResponse actualMappedResponse =
        (OpenWeatherResponse)
            openWeatherApiClient.getOpenWeatherAPIResponse((long) testDateTime).getOwResponse();
    String actualJson = objectMapper.writeValueAsString(actualMappedResponse);
    String expectedJson = TestOpenWeatherUtils.getOpenWeatherResponseAsString(testDateTime);
    JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
  }

  @Test
  void getOpenWeatherAPIResponse_apiDoesntGive200_returnsNullOw() {

    setupUnauthorisedWiremockMapping();
    StatusCodeObject statusCodeObject =
        new StatusCodeObject(HttpStatus.UNAUTHORIZED, "10/07/2024 16:46");

    assertThat(statusCodeObject)
        .isEqualTo(openWeatherApiClient.getOpenWeatherAPIResponse(1720626363L));
  }
}
