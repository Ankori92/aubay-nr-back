package com.aubay.formations.nr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application Runner
 *
 * @author jbureau@aubay.com
 */
@EnableCaching
@SpringBootApplication
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

	/**
	 * Singleton for Jackson Object Mapper (JSON Serialization/Deserialization)
	 * Injection ready
	 */
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
