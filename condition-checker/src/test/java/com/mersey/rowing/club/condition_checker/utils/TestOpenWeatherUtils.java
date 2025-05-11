package com.mersey.rowing.club.condition_checker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.Weather;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.WeatherData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestOpenWeatherUtils {

  public static final String BASE_URL = "http://localhost:5050";
  public static final String DUMMY_API_KEY = "testApiKey";
  public static final int TEST_EPOCH_TIME = 1718653615;

  public static String getOpenWeatherResponseAsString(int dt) {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    try {
      return ow.writeValueAsString(getOpenWeatherResponse(dt));
    } catch (JsonProcessingException e) {
      log.error("Error while parsing JSON", e);
      throw new RuntimeException(e);
    }
  }

  public static OpenWeatherResponse getOpenWeatherResponse(int dtToUse) {
    List<Weather> weatherList = List.of(new Weather(800, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(dtToUse, 279.13, 276.44, 3.6, 3.6, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }
}
