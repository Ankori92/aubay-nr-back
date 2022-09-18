package com.aubay.formations.nr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Application Runner
 *
 * @author jbureau@aubay.com
 */
@SpringBootApplication
@EnableSwagger2
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
	 * Configure Swagger API
	 *
	 * @return
	 */
	@Bean
	public Docket api() {
		final var swagger2 = DocumentationType.SWAGGER_2;
		return new Docket(swagger2).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
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
