package com.catprogrammer.jira.controller;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.catprogrammer.jira.util.JiraUtil;

@Controller
@RequestMapping("/")
public class Index {

	@Autowired
	private JiraUtil jiraUtil;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("time", Calendar.getInstance().getTime().toString());
		jiraUtil.test();
		return "index";
	}
}
