package com.mersey.rowing.club.condition_checker.applicationTests.model.openweatherapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mersey.rowing.club.condition_checker.model.openweatherapi.WeatherData;
import org.junit.jupiter.api.Test;

public class WeatherDataTests {

  @Test
  void feelsLikeFahrenheitToCelsius_weatherDataBuiltWithoutTempInCelsius_setsTempInCelsius() {
    WeatherData myWeatherData = WeatherData.builder().feelsLike(50).build();
    System.out.println(myWeatherData);
    assertEquals(10, myWeatherData.feelsLikeFahrenheitToCelsius());
  }

  @Test
  void mphToKmph_weatherDataBuiltWithoutmphToKmph_setsKmph() {
    WeatherData myWeatherData = WeatherData.builder().windSpeed(1).build();
    System.out.println(myWeatherData);
    assertEquals(1.60934, myWeatherData.mphToKmph());
  }
}
