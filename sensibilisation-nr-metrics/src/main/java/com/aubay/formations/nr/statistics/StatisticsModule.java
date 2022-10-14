package com.aubay.formations.nr.statistics;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Statistics Module.<br />
 * Activate RequestInterceptor (API calls count, API response weight) Activate
 * HibernateInterceptor (SQL queries count)
 *
 * @author jbureau@aubay.com
 */
@Configuration
public class StatisticsModule implements WebMvcConfigurer {

	@Autowired
	private RequestStatisticsInterceptor requestInterceptor;

	/**
	 * Register the requestInterceptor (Listening endpoints API)
	 */
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor).addPathPatterns("/**");
	}

	/**
	 * Wrap the HttpServletResponse to allow multiple response reading (Used to get
	 * the content weight of the response).<br />
	 * This bean is a filter.
	 */
	@Bean
	public Filter cachedResponseWrapper() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res,
					final FilterChain chain) throws ServletException, IOException {
				final ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(res);
				doFilter(req, cachedResponse, chain);
				cachedResponse.copyBodyToResponse(); // Reload content for client
			}
		};
	}

	/**
	 * Hibernate Statistics Interceptor Registration.
	 */
	@Component
	public static class HibernateStatisticsInterceptorRegistration implements HibernatePropertiesCustomizer {

		@Autowired
		private HibernateStatisticsInterceptor statisticsInterceptor;

		@Override
		public void customize(final Map<String, Object> hibernateProperties) {
			hibernateProperties.put("hibernate.session_factory.interceptor", statisticsInterceptor);
		}
	}
}
