package com.aubay.formations.nr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.entities.User;
import com.aubay.formations.nr.enums.LangEnum;
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

	/**
	 * Change authenticated user preferred language
	 *
	 * @param lang LangEnum value
	 * @return Authenticated user after modification
	 */
	@PostMapping("/users/lang/{lang}")
	public User setPreferedLanguage(@PathVariable("lang") final LangEnum lang) {
		return applicationService.setPreferredLanguage(lang);
	}
}
