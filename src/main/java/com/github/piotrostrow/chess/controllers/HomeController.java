package com.github.piotrostrow.chess.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This is not needed apparently??
 */
@Controller
public class HomeController {

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

}
