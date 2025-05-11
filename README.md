# Condition Checker 
# 🛠️ WIP 🛠️
An application to check the weather conditions of Mersey Rowing Club at 6am and 6pm (for morning and evening sessions) to see if boats can go ahead. 

## How to run
### You will need:
- An OpenWeather api key - _you need to sign up for a free OpenWeather account for this https://home.openweathermap.org/api_keys_
- You need to be signed up for the One Call API 3.0 

### Environment Variables 
- `OPENWEATHER_API_KEY`

### Locally using IntelliJ 
- Install the Env file plugin https://plugins.jetbrains.com/plugin/7861-envfile 
- Add your api key to the .env file - you need to sign up for a free OpenWeather account for this https://home.openweathermap.org/api_keys 
- Edit the run configurations for ConditionCheckerApplication -> edit configurations -> enable env file -> select the .env file 
- Run the app 

FROM THE COMMAND LINE
```
mvn spring-boot:run
```

### Formatting 

mvn com.spotify:fmt-maven-plugin:2.10.0:format



### Using the jar 
- <tests requiring docker>/...
- Using the maven window, or maven commend (`mvn clean package`) create the JAR file 
- Set the environment variables & run (e.g. `set OPENWEATHER_API_KEY=value && java -jar condition-checker.jar`)

# Docker/Images
### to build the image 
- Using the maven window, or maven command (`mvn clean package`) create the JAR file
- Run the dockerfile (or execute `docker build -t condition_checker:latest .`)

### Run the image 
- docker run -p 8080:8080 -e OPENWEATHER_API_KEY="your_actual_api_key" condition_checker

### Testing 

Prerequisites:
- Docker Desktop

Instructions:
- Run `docker-compose up -d ` (this starts the wiremock container)
- Run the maven unit tests _(right click on `java.com.mersey.rowing.club.condition_checker` -> run all tests)_

### Counter.txt File
The counter.txt file is essential for keeping an eye on the number of API calls made per day.  
It contains the current date which is updated automatically when the API is called, and a counter, which is also updated and refreshed each day automatically, to keep track of the number of API calls made on the current date.

To exclude wiremock tests, run `mvn test` - those that use wiremock are annotated with `@Tag("wiremock")`

# Application Logic and Reasoning
Currently, we are working from a [Miro](https://miro.com/app/board/uXjVPMF8Djc=/?moveToWidget=3458764584603444169&cot=14) (_currently private, please request access if necessary_) page, designed by Adelaide Baron. Additionally, work is being prioritised through the GitHub Project associated with this repository. 

### Conditions
Currently, the conditions are rough estimations of the limits at Mersey Rowing Club, but data such as the max temp has been obtained from external resources: https://plus.britishrowing.org/2023/06/08/beat-the-heat/ 

The conditions are set in the application.yml. 

### Reasons for this application
This process is currently a manual check done by a single member of the rowing club. This app, once complete, will enable crews to check this themselves to see if boats are going ahead. 


# ADDI TODO 
Run the app using mvn: mvn compile followed by mvn exec:java