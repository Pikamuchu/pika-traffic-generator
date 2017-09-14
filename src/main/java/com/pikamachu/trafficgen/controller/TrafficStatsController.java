package com.pikamachu.trafficgen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pikamachu.trafficgen.service.TrafficStatsService;

/**
 * The Class TrafficStatsController.
 */
@Controller
public class TrafficStatsController {

	/** The service. */
	@Autowired
	TrafficStatsService service;
	
	/**
	 * Stats.
	 *
	 * @param model
	 *            the model
	 * @return the string
	 */
	@RequestMapping("/stats")
	public String stats(Model model) {
        model.addAttribute("stats", service.toHtml());
        return "stats";
	}

}
