package com.mersey.rowing.club.condition_checker;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mersey.rowing.club.condition_checker.mock.WireMockConfiguration;
import com.mersey.rowing.club.condition_checker.mock.WireMockSetup;
import com.mersey.rowing.club.condition_checker.model.openweatherapi.OpenWeatherResponse;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = { "open-weather-api.key=test","open-weather-api.baseUrl=http://localhost:5050" })
@Import(WireMockConfiguration.class)
@Slf4j
public class WiremockBaseTests {

    @Autowired
    private WireMock wireMock;

    @Autowired
    private WireMockSetup wireMockSetup;

    @Value("${open-weather-api.endpoint}")
    private String endpoint;

    private static final String BASE_URL = "http://localhost:5050";
    private static final String DUMMY_API_KEY = "testApiKey";
    private static final String TEST_EPOCH_TIME = "1625088000";

    protected static OpenWeatherResponse expectedOpenWeatherResponse;

    protected static String url;
    protected static String expectedResponseBody;

    @BeforeAll
    static void setUpBeforeClass() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = BASE_URL;

        url = BASE_URL + "/timemachine?lat=53.39293&lon=-2.98532&dt=" + TEST_EPOCH_TIME + "&appid=" + DUMMY_API_KEY;

        log.info("<<<<< SETTING UP WIREMOCK MAPPINGS >>>>>");
        WireMockSetup mockSetup = new WireMockSetup();
        MappingBuilder mappingBuilder = mockSetup.setupOpenWeatherMapping();
        expectedResponseBody = mockSetup.getOpenWeatherResponseAsString();

        WireMock wireMock = new WireMock("localhost", 5050);
        wireMock.register(mappingBuilder);
        log.info("<<<<< WIREMOCK MAPPINGS COMPLETE SETUP >>>>>");

        expectedOpenWeatherResponse = mockSetup.getOpenWeatherResponse();
    }

    @AfterAll
    static void tearDownAfterClass() {
        log.info("<<<<< CLEARING WIREMOCK MAPPINGS >>>>>");
        WireMock wireMock = new WireMock("localhost", 5050);
        wireMock.resetMappings();
        log.info("<<<<< WIREMOCK MAPPINGS CLEARED >>>>>");
    }
}