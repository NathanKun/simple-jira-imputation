package com.catprogrammer.jira.controller;

import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class Index {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("time", Calendar.getInstance().getTime().toString());
		return "index";
	}
}
