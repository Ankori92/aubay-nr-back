package com.aubay.formations.nr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application Runner
 *
 * @author jbureau@aubay.com
 */
@EnableCaching
@SpringBootApplication
@EnableMongoRepositories
public class WebApplication implements WebMvcConfigurer {

	@Value("${frontend.url}")
	private String frontendUrl;

	/**
	 * WebApplication Runner (Main method)
	 */
	public static void main(final String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	/**
	 * Configure CORS
	 */
	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(frontendUrl).allowedMethods("*").allowCredentials(true);
	}

}
