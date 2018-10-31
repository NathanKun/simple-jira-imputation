package com.catprogrammer.jira.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.catprogrammer.jira.constant.Auth;
import com.catprogrammer.jira.model.Cell;
import com.catprogrammer.jira.model.CellType;
import com.catprogrammer.jira.model.Entry;
import com.catprogrammer.jira.model.Worklog;
import com.catprogrammer.jira.util.JiraUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Restful Api controller
 * RequestMapping("/rest")
 * @author NathanKun
 *
 */
@RestController
@RequestMapping("/rest")
public class RestApiController {

    private final Logger logger = LoggerFactory.getLogger(RestApiController.class);
    
    @Autowired
    private JiraUtil jiraUtil;

    @Autowired
    private Gson gson;
    
    @GetMapping("/index")
    public ResponseEntity<String> index(Model model,
            @RequestParam(value = "start",
                    required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(value = "end",
                    required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "type", required = false, defaultValue = "json") String type) {

        if (start == null && end == null) {
            start = LocalDate.now().withDayOfMonth(1);
            end = start.withDayOfMonth(start.lengthOfMonth());
        }
        if (end == null) {
            end = start.plusDays(30);
        }
        if (start == null) {
            start = end.minusDays(30);
        }

        List<Worklog> worklogs =
                jiraUtil.findWorklogsBetween(user != null ? user : Auth.getUser(), start, end);

        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Cell> headers = new ArrayList<Cell>();
        List<Cell> fillDayRow = new ArrayList<Cell>();
        List<Cell> totalRow = new ArrayList<Cell>();
        List<List<Cell>> table = new ArrayList<List<Cell>>();
        double[] totals = new double[start.until(end).getDays() + 1];

        for (Worklog w : worklogs) {
            List<Cell> row = new ArrayList<Cell>();
            row.add(new Cell(w.getKey(), w.getSummary(), CellType.TICKET));

            for (int i = 0; i <= start.until(end).getDays(); i++) {
                LocalDate day = start.plusDays(i);
                boolean found = false;

                for (Entry entry : w.getEntries()) {
                    if (day.isEqual(entry.getStartDate().toLocalDate())) {
                        if (!found) {
                            found = true;
                            row.add(new Cell(entry.getTimeSpentHour(),
                                    String.valueOf(entry.getId()), CellType.TIMESPENT));
                        } else {
                            Cell c = row.get(row.size() - 1);
                            String oldValue = c.getDisplayValue();
                            double oldValueDouble = Double.parseDouble(
                                    oldValue.substring(0, c.getDisplayValue().length() - 1));
                            double newValueDouble =
                                    (double) entry.getTimeSpent() / 3600 + oldValueDouble;
                            c.setDisplayValue(newValueDouble + "h");
                            c.setData(c.getData() + "," + entry.getId());
                        }
                        totals[i] += entry.getTimeSpent();
                    }
                }

                if (!found) {
                    row.add(new Cell(null, w.getKey(), CellType.ADDTIME));
                }
            }

            table.add(row);
        }

        for (int i = 0; i <= start.until(end).getDays(); i++) {
            LocalDate day = start.plusDays(i);

            headers.add(new Cell(start.plusDays(i).format(shortFormatter),
                    day.getDayOfWeek() == DayOfWeek.SATURDAY
                            || day.getDayOfWeek() == DayOfWeek.SUNDAY ? "WEEKEND" : "WEEKDAY",
                    CellType.HEADER));

            fillDayRow.add(new Cell(null, day.format(longFormatter), CellType.FILLDAY));

            totalRow.add(new Cell(totals[i] / 3600 + "h", null, CellType.TOTAL));
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("headers", gson.toJson(headers));
        json.addProperty("fillDayRow", gson.toJson(fillDayRow));
        json.addProperty("totalRow", gson.toJson(totalRow));
        json.addProperty("table", gson.toJson(table));
        
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    /**
     * Find all worklogs between two dates
     * @param start start date
     * @param end   end date
     * @return  json response of worklogs
     */
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

    /**
     * Fill a day with FNBCPV-11415 and FNBCPV-14761
     * @param date      date to fill
     * @param total     total hours already have in the date
     * @return  json response
     */
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

        boolean res1 = jiraUtil.addWorklog("FNBCPV-11415", date, dailyHours, 10, 30);
        boolean res2 = jiraUtil.addWorklog("FNBCPV-14761", date, restHours, 11, 00);

        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", res1 && res2);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }
    
    /**
     * Update worklog of a ticket
     * @param key           ticket
     * @param worklogId     worklog id
     * @param value         value of worklog in hours
     * @return json response
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateTimeOfTicketofDate(
            @RequestParam("ticket") String key,
            @RequestParam("id") String worklogId,
            @RequestParam("value") double value) {
        
        if (key.split("-").length != 2) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "Ticket key not correct");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        if (value > 8 || value < 0) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "value must <= 8 or >= 0");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        boolean res = false;
        if (value == 0) {
            logger.info("Delete wodklog for ticket " + key + " with worklog id " + worklogId);
            res = jiraUtil.deleteWorklog(key, worklogId);
        } else { // value > 0
            logger.info("Update wodklog for ticket " + key + " with worklog id " + worklogId + " with value " + value);
            res = jiraUtil.updateWorklog(key, worklogId, value);
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", res);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }
    
    /**
     * Delete worklog by id
     * @param key           ticket
     * @param worklogId     worklog id
     * @return json response
     */
    @PostMapping("/delete")
    public ResponseEntity<String> updateTimeOfTicketofDate(
            @RequestParam("ticket") String key,
            @RequestParam("id") String worklogId) {
        
        if (key.split("-").length != 2) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "Ticket key not correct");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }
        
        logger.info("Delete wodklog for ticket " + key + " with worklog id " + worklogId);

        boolean res = jiraUtil.deleteWorklog(key, worklogId);
        
        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", res);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }
    
    /**
     * Add worklog to a ticket
     * @param date  date to add worklog
     * @param key   key of ticket
     * @param value value of worklog in hours
     * @return  json response
     */
    @PostMapping("/add")
    public ResponseEntity<String> addTimeOfTicketofDate(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam("ticket") String key,
            @RequestParam("value") double value) {
        
        if (key.split("-").length != 2) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "Ticket key not correct");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        if (value > 8 || value < 0) {
            JsonObject json = new JsonObject();
            json.addProperty("valid", false);
            json.addProperty("error", "value must <= 8 or >= 0");
            return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        boolean res = jiraUtil.addWorklog(key, date, value, 00, 00);
        
        JsonObject json = new JsonObject();
        json.addProperty("valid", true);
        json.addProperty("data", res);

        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }
}
