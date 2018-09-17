package com.catprogrammer.jira.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Index controller
 * RequestMapping("/")
 * @author NathanKun
 *
 */
@Controller
@RequestMapping("/")
public class IndexController {
    //private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private JiraUtil jiraUtil;

    @GetMapping("/")
    public String index(Model model,
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

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
            row.add(new Cell(w.getKey(), w.getSummary(), CellType.TICKET));

            for (int i = 0; i <= start.until(end).getDays(); i++) {
                LocalDate day = start.plusDays(i);
                boolean found = false;

                for (Entry entry : w.getEntries()) {
                    if (day.isEqual(entry.getStartDate().toLocalDate())) {
                        if (!found) {
                            found = true;
                            row.add(new Cell(entry.getTimeSpentHour(), String.valueOf(entry.getId()), CellType.TIMESPENT));
                        } else {
                            Cell c = row.get(row.size() - 1);
                            String oldValue = c.getDisplayValue();
                            double oldValueDouble = Double.parseDouble(oldValue.substring(0, c.getDisplayValue().length() - 1));
                            double newValueDouble = (double)entry.getTimeSpent() / 3600 + oldValueDouble;
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

        model.addAttribute("headers", headers);
        model.addAttribute("fillDayRow", fillDayRow);
        model.addAttribute("totalRow", totalRow);
        model.addAttribute("table", table);

        return "index";

    }

}
