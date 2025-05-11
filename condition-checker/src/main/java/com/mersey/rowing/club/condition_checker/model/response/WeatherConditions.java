package com.mersey.rowing.club.condition_checker.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@AllArgsConstructor
@Jacksonized
public class WeatherConditions {

  private String description;

  @JsonProperty("temp_feels_like")
  private int tempFeelsLike;

  @JsonProperty("wind_speed")
  private int windSpeed;

  @JsonProperty("wind_gust")
  private int windGust;
}
