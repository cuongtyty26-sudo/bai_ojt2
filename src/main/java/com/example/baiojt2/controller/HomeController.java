package com.example.baiojt2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the application's public home page.
 */
@Controller
public class HomeController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
}
