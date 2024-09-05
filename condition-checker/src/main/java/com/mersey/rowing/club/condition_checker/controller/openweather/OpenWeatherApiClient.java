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

    @Value("${open-weather-api.key}")
    private String apiKey;

    @Value("${open-weather-api.baseUrl}")
    private String apiBaseUrl;

    @Value("${open-weather-api.endpoint}")
    private String apiEndpoint;

    String counterPath = System.getProperty("user.dir") + "/condition-checker/src/main/resources/counter.txt";

    public boolean checkAPILimitIsNotReached() {
        boolean limitReached = false;
        try {

            // Reads down to counter number, if it's greater than or equal to 994, limit reached boolean is set to true
            BufferedReader bufferedReader = new BufferedReader(new FileReader(counterPath));
            bufferedReader.readLine();
            bufferedReader.readLine();
            bufferedReader.readLine();
            int counter = Integer.parseInt(bufferedReader.readLine());
            if (counter > 999) {
                log.warn("Call limit has been reached, no more calls can be made until tomorrow.");
                limitReached = true;
            }
        } catch (FileNotFoundException e) {
            log.error("File was not found: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return limitReached;
    }

    public StatusCodeObject getOpenWeatherAPIResponse(long epoch) {

        if (checkAPILimitIsNotReached()) {
            log.warn("API limit reached. API is not able to called.");
            return new StatusCodeObject(HttpStatus.TOO_MANY_REQUESTS, "API limit reached, please try again tomorrow");
        }

        String url = String.format(apiBaseUrl + apiEndpoint, epoch, apiKey);
        Class<OpenWeatherResponse> responseType = OpenWeatherResponse.class;
        try {
            checkDateAndAddCounter();
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

    public void checkDateAndAddCounter() {

        BufferedReader bufferedReader = null;
        String currentDate = "12/12/1970";
        int counter = 1;

        // Checking if counter.txt exists
        File file = new File(counterPath);

        // If file does not exist, counter.txt is created and filled with boilerplate code
        if (!file.exists()) {
            try {
                log.info("counter.txt was not found! Creating now.");
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(counterPath));
                bufferedWriter.write("Current Date:\n");
                bufferedWriter.write(currentDate + "\n");
                bufferedWriter.write("Counter:\n");
                bufferedWriter.write(String.valueOf(counter));
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // To check if the file exists but is empty
        try {
            bufferedReader = new BufferedReader(new FileReader(counterPath));
            // Checking to see if file is empty or not
            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                openFileAndUpdateCounter(currentDate, counter);
                log.info("counter.txt was empty! Calls remaining may not be accurate!");
                // Closing reader to reset to beginning of counter.txt
                bufferedReader.close();
            }

            // Reinitialising the buffered reader to start from the top of counter.txt
            bufferedReader = new BufferedReader(new FileReader(counterPath));

            // Skipping first line and grabbing currentDate
            bufferedReader.readLine();
            currentDate = bufferedReader.readLine();

            // Skipping third line and grabbing current number of API calls
            bufferedReader.readLine();
            counter = Integer.parseInt(bufferedReader.readLine());
            bufferedReader.close();

            // Logic to update counter.txt
            if (dateUtil.dtfMinusHours.format(dateUtil.getCurrentDate()).equals(currentDate)) {
                counter++;
            } else {
                currentDate = dateUtil.dtfMinusHours.format(dateUtil.getCurrentDate());
                counter = 1;
            }

            if (counter > 900 && counter < 1000) {
                System.out.println("There are less than 100 calls left for today! Please use sparingly.");
                log.warn("There are only {} calls left", 1000 - counter);
            } else {
                log.info("There are a total of: {} calls left for today", 1000 - counter);
            }

            // Opening and updating counter.txt
            openFileAndUpdateCounter(currentDate, counter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void openFileAndUpdateCounter(String currentDate, Integer counter) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\Sam\\Documents\\Code\\condition-checker\\condition-checker\\src\\main\\resources\\counter.txt"));

        bufferedWriter.write("Current Date:\n");
        bufferedWriter.write(currentDate + "\n");

        bufferedWriter.write("No. of API calls since above date:\n");
        bufferedWriter.write(String.valueOf(counter));
        bufferedWriter.close();
    }
}
