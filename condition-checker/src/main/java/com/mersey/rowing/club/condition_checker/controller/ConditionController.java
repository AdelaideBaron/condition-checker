package com.mersey.rowing.club.condition_checker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ConditionController {

    @GetMapping("/get_conditions")
    public void getConditions(@RequestHeader(value = "date", required = false) String date, @RequestHeader(value = "time", required = false) String time){

        if(date == null && time == null){
            log.info("date and time is null");
            // go with default of today + 24 hours
        } else if (date == null) {
            log.info("date only is null");
        } else if (time == null) {
            log.info("time only is null");
            // will return a response of weather summary, and boats that can go out
            // call open weather API and map the response
            // pass this into the BoatCapabilityClient to assess which boats can go
        }else{
            log.info("both date and time supplied");
            //specific
        }
    }
}
