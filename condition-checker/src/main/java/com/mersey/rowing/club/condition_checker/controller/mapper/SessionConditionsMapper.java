package com.mersey.rowing.club.condition_checker.controller.mapper;

import com.mersey.rowing.club.condition_checker.controller.boats.BoatCapabilityClient;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import com.mersey.rowing.club.condition_checker.model.response.BoatsAllowed;
import com.mersey.rowing.club.condition_checker.model.response.WeatherConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionConditionsMapper {

    @Autowired
    BoatCapabilityClient boatCapabilityClient;


    public void mapSessionConditionsFromOpenWeatherResponse(OpenWeatherResponse openWeatherResponse){
        BoatsAllowed boatsAllowed = boatCapabilityClient.getBoatsAllowed(openWeatherResponse);


        WeatherConditions weatherConditions = WeatherConditions.builder().description(openWeatherResponse.getDescription()).windSpeed((int) openWeatherResponse.getWindSpeed()).tempFeelsLike((int) openWeatherResponse.getFeelsLike()).build();

    }
}
