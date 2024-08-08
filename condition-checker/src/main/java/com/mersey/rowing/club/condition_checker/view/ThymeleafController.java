package com.mersey.rowing.club.condition_checker.view;

import com.mersey.rowing.club.condition_checker.controller.boats.BoatCapabilityClient;
import com.mersey.rowing.club.condition_checker.controller.response.ConditionResponseClient;
import com.mersey.rowing.club.condition_checker.controller.util.DateUtil;
import com.mersey.rowing.club.condition_checker.model.response.BoatsAllowed;
import com.mersey.rowing.club.condition_checker.model.response.ConditionResponse;
import com.mersey.rowing.club.condition_checker.model.response.SessionConditions;
import com.mersey.rowing.club.condition_checker.model.response.TimeType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // List of session start times
    List<SessionConditions> sessionStartTimes =
        conditionResponse.getSessionConditions().stream()
            .filter(sc -> sc.getTimeType().equals(TimeType.SESSION_START))
            .toList();

    // Map to hold session UUIDs and their conditions and average boats allowed
    Map<SessionConditions, List<SessionConditions>> sessionConditionsMap = new HashMap<>();
    Map<SessionConditions, BoatsAllowed> sessionBoatsMap = new HashMap<>();

    for (SessionConditions startTime : sessionStartTimes) {
      String uuid = startTime.getSessionUUID();
      List<SessionConditions> conditionsForSession =
          conditionResponse.getSessionConditions().stream()
              .filter(sc -> sc.getSessionUUID().equals(uuid))
              .toList();

      // Calculate average boats permitted for the session
      BoatsAllowed averageBoatsAllowed =
          boatCapabilityClient.getSessionAverage(conditionsForSession);

      // Map start time to conditions and average boats allowed
      sessionConditionsMap.put(startTime, conditionsForSession);
      sessionBoatsMap.put(startTime, averageBoatsAllowed);

    }

    // Add data to the model
    model.addAttribute("sessionConditionsMap", sessionConditionsMap);
    model.addAttribute("sessionBoatsMap", sessionBoatsMap);

    return "dateDetails";
  }
}
