package com.mersey.rowing.club.condition_checker.model.boat;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class BoatLimits {
    private int feelsLikeTempMinKelvin;
    private int feelsLikeTempMaxKelvin;
    private String[] unacceptableIds;
    private String[] exceptionsToTheAbove;
    private Map<BoatType, Integer> boatTypeWindLimit;
}