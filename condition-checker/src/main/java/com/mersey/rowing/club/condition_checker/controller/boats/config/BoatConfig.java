package com.mersey.rowing.club.condition_checker.controller.boats.config;

import static com.mersey.rowing.club.condition_checker.model.boat.BoatType.*;

import com.mersey.rowing.club.condition_checker.model.boat.BoatLimits;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoatConfig {

  @Value("${boats.feelsLikeTempMinCelsius}")
  private int feelsLikeTempMinCelsius;

  @Value("${boats.feelsLikeTempMaxCelsius}")
  private int feelsLikeTempMaxCelsius;

  @Value("${boats.unacceptableIds}")
  private String unacceptableIdsString;

  @Value("${boats.exceptionsToTheAbove}")
  private String exceptionsToTheAboveString;

  @Value("${boats.boatTypes.single.wind}")
  private int singleWindLimit;

  @Value("${boats.boatTypes.double.wind}")
  private int doubleWindLimit;

  @Value("${boats.boatTypes.eight.wind}")
  private int eightWindLimit;

  @Value("${boats.boatTypes.quads.wind}")
  private int quadsWindLimit;

  @Bean
  public BoatLimits boatLimits() {
    return BoatLimits.builder()
        .boatTypeWindLimit(
            Map.of(
                SINGLE,
                singleWindLimit,
                DOUBLE,
                doubleWindLimit,
                EIGHT,
                eightWindLimit,
                QUADS,
                quadsWindLimit))
        .feelsLikeTempMaxCelsius(feelsLikeTempMaxCelsius)
        .feelsLikeTempMinCelsius(feelsLikeTempMinCelsius)
        .unacceptableIds(unacceptableIdsString.split(","))
        .exceptionsToTheAbove(exceptionsToTheAboveString.split(","))
        .build();
  }
}
