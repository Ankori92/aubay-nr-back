package com.aubay.formations.nr.services;

import java.util.ArrayList;
import java.util.Arrays;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import com.aubay.formations.nr.entities.Employee;
import com.aubay.formations.nr.entities.User;
import com.aubay.formations.nr.repositories.UserRepository;
import com.aubay.formations.nr.utils.AuthentHelper;

/**
 * Application services (User, etc)
 *
 * @author jbureau@aubay.com
 */
@Service
@Transactional
public class ApplicationService {

	@Autowired
	private AuthentHelper authentHelper;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Get the authenticated user
	 *
	 * @return
	 */
	public User whoami() {
		final var authenticatedUserId = authentHelper.getUsername();
		final var authenticatedUser = userRepository.getReferenceById(authenticatedUserId);
		final var employee = authenticatedUser.getEmployee();
		Hibernate.initialize(employee.getCountry());
		employee.setEmployees(new ArrayList<>());
		return authenticatedUser;
	}

	/**
	 * Create application account
	 *
	 * @param username
	 * @param password
	 * @param authorities
	 * @formatter:off
	 */
	public void createAccount(final String username, final String password, final Employee employee, final GrantedAuthority... authorities) {
		userRepository.save(new User(username, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password), employee, Arrays.asList(authorities)));
	}
}
