package com.mersey.rowing.club.condition_checker.model.openweatherapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@AllArgsConstructor
@Jacksonized
public class Weather {

  @JsonProperty("id")
  private int id;

  @JsonProperty("description")
  private String description;

  @JsonProperty("icon")
  private String icon;
}
