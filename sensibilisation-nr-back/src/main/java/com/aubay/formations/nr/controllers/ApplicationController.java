package com.aubay.formations.nr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.entities.User;
import com.aubay.formations.nr.services.ApplicationService;

/**
 * Application controller (User, lang, etc)
 *
 * @author jbureau@aubay.com
 */
@RestController
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	/**
	 * Who is authenticated ?
	 *
	 * @return Authenticated User
	 */
	@GetMapping("/whoami")
	public User whoami() {
		return applicationService.whoami();
	}
}
