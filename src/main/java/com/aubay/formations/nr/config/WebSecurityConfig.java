package com.aubay.formations.nr.config;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.transaction.CannotCreateTransactionException;

import com.aubay.formations.nr.repositories.UserRepository;
import com.aubay.formations.nr.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Web security configuration
 *
 * @formatter:off
 * @author jbureau@aubay.com
 */
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private ObjectMapper mapper;

	@Value("${frontend.url}")
	private String frontendUrl;

	@Bean
	public UserDetailsService userDetailsService(final DataSource ds) {
		return new JdbcUserDetailsManager(ds) {

			@Autowired
			private UserRepository userRepository;

			@Autowired
			private EmployeeService employeeService;

			@Override
			@Transactional
			public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
				final var user = userRepository.findById(username);
				if(!user.isPresent()) {
					throw new UsernameNotFoundException("L'utilisateur " + username + " n'a pas été trouvé");
				}
				employeeService.initializeEmployee(user.get().getEmployee(), true /* Load only direct team for authenticated user */);
				return user.get();
			}
		};
	}

	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		// API Securité
		http.authorizeRequests(authorize -> authorize
				.antMatchers("/v2/api-docs", "/swagger-ui/**", "/swagger-resources/**").permitAll()
				.antMatchers("/**").authenticated());
		// Handle access rejections
		http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		// Enable CORS
		http.cors();
		// Enable CSRF by Cookies
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// Authentication process
		http.formLogin().successHandler((request, response, authentication) -> {
			response.setStatus(HttpStatus.OK.value());
			mapper.writeValue(response.getOutputStream(), authentication.getPrincipal());
		}).failureHandler((request, response, exception) -> {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			if(exception.getCause() instanceof CannotCreateTransactionException) {
				response.getOutputStream().write("La base de données est inaccessible".getBytes());
			} else if(exception instanceof BadCredentialsException) {
				response.getOutputStream().write("Les identifiants sont incorrects".getBytes());
			} else {
				response.getOutputStream().write("L'authentification a échouée".getBytes());
			}
		});
		// Logout
		http.logout().logoutSuccessHandler((request, response, authentication) ->
			response.setStatus(HttpStatus.OK.value())
		);
		return http.build();
	}

}