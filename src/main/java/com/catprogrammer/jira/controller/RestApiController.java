package com.catprogrammer.jira.controller;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.catprogrammer.jira.constant.Auth;
import com.catprogrammer.jira.model.Worklog;
import com.catprogrammer.jira.util.JiraUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping("/rest")
public class RestApiController {

    private final Logger logger = LoggerFactory.getLogger(RestApiController.class);
    
    @Autowired
    private JiraUtil jiraUtil;

    @Autowired
    private Gson gson;

    @GetMapping("/worklogs")
    public ResponseEntity<String> findWorklogsBetweenDates(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        if (start == null && end == null) {
            end = LocalDate.now();
            start = end.minusDays(30);
        }
        if (end == null) {
            end = start.plusDays(30);
        }
        if (start == null) {
            start = end.minusDays(30);
        }

        List<Worklog> worklogs = jiraUtil.findWorklogsBetween(Auth.getUser(), start, end);

        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", gson.toJson(worklogs));

        return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/fillday")
    public ResponseEntity<String> fillDay(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam("total") double total) {

        if (total >= 8) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "Total must < 8");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        double dailyHours = 0, restHours = 0;
        if (total >= 7.5) {
            // fill daily meeting
            dailyHours = 8 - total;
        } else {
            // fill daily meeting and "[imputation] Montée en compétence nouveau collaborateur"
            dailyHours = 0.5;
            restHours = 8 - 0.5 - total;
        }

        boolean res = jiraUtil.doWorklog("FNBCPV-11415", date, dailyHours, 10, 30)
                && jiraUtil.doWorklog("FNBCPV-14761", date, restHours, 11, 00);

        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", res);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }
    
    @PostMapping("/update")
    public ResponseEntity<String> updateTimeOfTicketofDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam("ticket") String key,
            @RequestParam("id") String id,
            @RequestParam("value") double value) {
        
        if (key.split("-").length != 2) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "Ticket key not correct");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        if (value > 8) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "value must <= 8");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        
        
        
        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", true);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {

        return null;
    }
}
