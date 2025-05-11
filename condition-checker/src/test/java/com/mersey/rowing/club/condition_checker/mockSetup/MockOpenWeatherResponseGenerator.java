package com.mersey.rowing.club.condition_checker.mockSetup;

import static com.mersey.rowing.club.condition_checker.utils.TestOpenWeatherUtils.TEST_EPOCH_TIME;

import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.Weather;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.WeatherData;
import java.util.List;

public class MockOpenWeatherResponseGenerator {

  public static OpenWeatherResponse getOpenWeatherResponseAllGood() {
    List<Weather> weatherList = List.of(new Weather(800, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, 2, 2, weatherList)); // in imperial
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseExemptId() {
    List<Weather> weatherList = List.of(new Weather(101, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, 2, 2, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseUnacceptableIdSpecific() {
    List<Weather> weatherList = List.of(new Weather(302, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, 2, 2, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseUnacceptableIdXx() {
    List<Weather> weatherList = List.of(new Weather(250, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, 2, 2, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseWindSpeed(double windSpeed) {
    List<Weather> weatherList = List.of(new Weather(800, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, windSpeed, windSpeed, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseWindGustSpeed(double windSpeed) {
    List<Weather> weatherList = List.of(new Weather(800, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(new WeatherData(TEST_EPOCH_TIME, 50, 50, 2, windSpeed, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }

  public static OpenWeatherResponse getOpenWeatherResponseFeelsLike(double feelsLike) {
    double feelsLikeFahrenheit = (feelsLike * 9 / 5) + 32;
    List<Weather> weatherList = List.of(new Weather(800, "clear sky", "01d"));
    List<WeatherData> weatherDataList =
        List.of(
            new WeatherData(
                TEST_EPOCH_TIME, feelsLikeFahrenheit, feelsLikeFahrenheit, 2, 2, weatherList));
    return OpenWeatherResponse.builder().data(weatherDataList).build();
  }
}
