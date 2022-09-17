package com.aubay.formations.nr.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.aubay.formations.nr.entities.User;

/**
 * Authentication relative helper
 *
 * @author jbureau@aubay.com
 */
@Component
public class AuthentHelper {

	/**
	 * Get authenticated user
	 *
	 * @return authenticated user
	 */
	public User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * Get authenticated user
	 *
	 * @return authenticated user ID
	 */
	public String getUsername() {
		return getUser().getUsername();
	}
}
