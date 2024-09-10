package com.mersey.rowing.club.condition_checker.controller.openweather;

import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.model.StatusCodeObject;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.io.*;

@Component
@Slf4j
public class OpenWeatherApiClient {

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private DateUtil dateUtil;

    @Autowired APICounter apiCounter;

    @Value("${open-weather-api.key}")
    private String apiKey;

    @Value("${open-weather-api.baseUrl}")
    private String apiBaseUrl;

    @Value("${open-weather-api.endpoint}")
    private String apiEndpoint;

    String counterPath = System.getProperty("user.dir") + "/condition-checker/src/main/resources/counter.txt";



    public StatusCodeObject getOpenWeatherAPIResponse(long epoch) {

        String url = String.format(apiBaseUrl + apiEndpoint, epoch, apiKey);
        Class<OpenWeatherResponse> responseType = OpenWeatherResponse.class;
        try {
            apiCounter.checkDateAndAddCounter();
            OpenWeatherResponse openWeatherResponse = restTemplate.getForObject(url, responseType);
            log.info("Successfully retrieved and mapped response from open weather API");
            return new StatusCodeObject(HttpStatus.OK, openWeatherResponse);
        } catch (RestClientResponseException e) {
            log.error("Open Weather API gave an unexpected response: {}", e.getStatusCode());
            return new StatusCodeObject(
                    (HttpStatus) e.getStatusCode(), dateUtil.getDatetimeFromEpochSeconds(epoch));
        } catch (Exception e) {
            log.error("Unexpected error: " + e.getMessage());
            throw e;
        }
    }



}
