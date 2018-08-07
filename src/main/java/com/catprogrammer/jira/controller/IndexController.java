package com.catprogrammer.jira.controller;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.catprogrammer.jira.constant.Auth;
import com.catprogrammer.jira.model.Worklog;
import com.catprogrammer.jira.util.JiraUtil;

@Controller
@RequestMapping("/")
public class IndexController {
    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private JiraUtil jiraUtil;

    @GetMapping("/")
    public String index(Model model) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(start.lengthOfMonth());
        //List<Worklog> worklogs = jiraUtil.findWorklogsBetween(Auth.getUser(), start, end);
        //logger.info(jiraUtil.issueKeyToId("FNBCPV-11415"));
        return "index";
    }
}
