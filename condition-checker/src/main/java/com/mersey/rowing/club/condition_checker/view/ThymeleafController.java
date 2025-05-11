package com.mersey.rowing.club.condition_checker.view;

import com.mersey.rowing.club.condition_checker.controller.boats.BoatCapabilityClient;
import com.mersey.rowing.club.condition_checker.controller.response.ConditionResponseClient;
import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.model.response.BoatsAllowed;
import com.mersey.rowing.club.condition_checker.model.response.ConditionResponse;
import com.mersey.rowing.club.condition_checker.model.response.SessionConditions;
import com.mersey.rowing.club.condition_checker.model.response.TimeType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ThymeleafController {

  @Autowired ConditionResponseClient conditionResponseClient;

  @Autowired DateUtil dateUtil;

  @Autowired BoatCapabilityClient boatCapabilityClient;

  @GetMapping("/")
  public String index(Model model) {

    model.addAttribute("currentDateTime", WebsiteDateUtils.getDateNowFormatted());
    model.addAttribute("previousDays", WebsiteDateUtils.getPrevious7Days());
    model.addAttribute("nextDays", WebsiteDateUtils.getNext4DaysAndToday());

    return "index";
  }

  @GetMapping("/date")
  public String dateDetails(@RequestParam("selectedDate") String selectedDate, Model model) {
    model.addAttribute("selectedDate", selectedDate);

    String dateTime = dateUtil.getDateTimeAsDdMmYyyyFromWebsite(selectedDate);

    ConditionResponse conditionResponse =
        conditionResponseClient.getConditionResponseFromDateTime(dateTime, null).getBody();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // List of session start times
    List<SessionConditions> sessionStartTimes =
        conditionResponse.getSessionConditions().stream()
            .filter(sc -> sc.getTimeType().equals(TimeType.SESSION_START)) // Only SESSION_START
            .sorted(
                Comparator.comparing(
                    sc -> LocalDateTime.parse(sc.getDate(), formatter))) // Sort by date
            .toList();
    // Map to hold session UUIDs and their conditions and average boats allowed
    Map<SessionConditions, List<SessionConditions>> sessionConditionsMap =
        new HashMap<>(); // this is what needs sorting
    Map<SessionConditions, List<SessionConditions>> sortedMap =
        sessionConditionsMap.entrySet().stream()
            .sorted(
                Comparator.comparing(
                    entry -> LocalDateTime.parse(entry.getKey().getDate(), formatter)))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    sessionConditionsMap = sortedMap;

    Map<SessionConditions, BoatsAllowed> sessionBoatsMap = new HashMap<>();

    for (SessionConditions startTime : sessionStartTimes) {
      String uuid = startTime.getSessionUUID();
      List<SessionConditions> conditionsForSession =
          conditionResponse.getSessionConditions().stream()
              .filter(sc -> sc.getSessionUUID().equals(uuid))
              .toList();

      // Calculate average boats permitted for the session
      BoatsAllowed averageBoatsAllowed =
          boatCapabilityClient.getSessionAverage(
              conditionsForSession); // conditionsForSession = null

      // Map start time to conditions and average boats allowed
      sessionConditionsMap.put(startTime, conditionsForSession);
      sessionBoatsMap.put(startTime, averageBoatsAllowed);
    }

    // Add data to the model
    model.addAttribute("sessionConditionsMap", sessionConditionsMap);
    model.addAttribute("sessionBoatsMap", sessionBoatsMap);
    model.addAttribute("sessionStartTimes", sessionStartTimes);

    return "dateDetails";
  }
}
