package com.catprogrammer.jira.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.catprogrammer.jira.constant.Auth;
import com.catprogrammer.jira.model.Cell;
import com.catprogrammer.jira.model.CellType;
import com.catprogrammer.jira.model.Entry;
import com.catprogrammer.jira.model.Worklog;
import com.catprogrammer.jira.util.JiraUtil;

@Controller
@RequestMapping("/")
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private JiraUtil jiraUtil;

    @GetMapping("/")
    public String index(Model model,
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

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

        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Cell> headers = new ArrayList<Cell>();
        List<Cell> fillDayRow = new ArrayList<Cell>();
        List<Cell> totalRow = new ArrayList<Cell>();
        List<List<Cell>> table = new ArrayList<List<Cell>>();
        double[] totals = new double[start.until(end).getDays() + 1];

        for (Worklog w : worklogs) {
            List<Cell> row = new ArrayList<Cell>();
            row.add(new Cell(w.getKey(), w.getSummary(), null, CellType.TICKET));

            for (int i = 0; i <= start.until(end).getDays(); i++) {
                LocalDate day = start.plusDays(i);
                boolean found = false;

                for (Entry entry : w.getEntries()) {
                    if (day.isEqual(entry.getStartDate().toLocalDate())) {
                        found = true;
                        row.add(new Cell(entry.getTimeSpentHour(), null, null, CellType.TIMESPENT));
                        totals[i] += entry.getTimeSpent();
                    }
                }

                if (!found) {
                    row.add(new Cell(null, w.getKey(), null, CellType.ADDTIME));
                }
            }

            table.add(row);
        }

        for (int i = 0; i <= start.until(end).getDays(); i++) {
            LocalDate day = start.plusDays(i);

            headers.add(new Cell(start.plusDays(i).format(shortFormatter),
                    day.getDayOfWeek() == DayOfWeek.SATURDAY
                            || day.getDayOfWeek() == DayOfWeek.SUNDAY ? "WEEKEND" : "WEEKDAY",
                    null, CellType.HEADER));

            fillDayRow.add(new Cell(null, day.format(longFormatter), null, CellType.FILLDAY));

            totalRow.add(new Cell(totals[i] / 3600 + "h", null, null, CellType.TOTAL));
        }

        model.addAttribute("headers", headers);
        model.addAttribute("fillDayRow", fillDayRow);
        model.addAttribute("totalRow", totalRow);
        model.addAttribute("table", table);

        return "index";

    }

}
