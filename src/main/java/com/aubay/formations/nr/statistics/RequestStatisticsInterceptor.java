package com.aubay.formations.nr.statistics;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.aubay.formations.nr.entities.Usage;
import com.aubay.formations.nr.repositories.UsageRepository;
import com.aubay.formations.nr.utils.Chrono;

import lombok.extern.log4j.Log4j2;

/**
 * Interceptor for all web API calls<br />
 * This interceptor show how many SQL statements was executed and the execution
 * time<br />
 * This interceptor provide usage statistics about endpoints.
 *
 * @author jbureau@aubay.com
 */
@Log4j2
@Component
public class RequestStatisticsInterceptor implements AsyncHandlerInterceptor {

	private final ThreadLocal<Long> executionTime = new ThreadLocal<>();

	@Autowired
	private HibernateStatisticsInterceptor hibernateStatisticsInterceptor;

	@Autowired
	private UsageRepository usageRepository;

	/**
	 * Before each API execution, start counters
	 */
	@Override
	public boolean preHandle(final HttpServletRequest req, final HttpServletResponse res, final Object handler)
			throws Exception {
		executionTime.set(System.currentTimeMillis());
		hibernateStatisticsInterceptor.startCounter();
		return true;
	}

	/**
	 * After each API execution, save collected statistics
	 * @formatter:off
	 */
	@Override
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex) throws Exception {
		Chrono.start();
		final var duration = (int) (System.currentTimeMillis() - executionTime.get());
		final var queries = hibernateStatisticsInterceptor.getQueryCount().intValue();
		hibernateStatisticsInterceptor.clearCounter();
		executionTime.remove();
		var length = 0;
		if (response instanceof final ContentCachingResponseWrapper cachedResponse) {
			length = cachedResponse.getContentSize();
		}
		var path = "";
		if(handler instanceof final HandlerMethod handlerMethod) {
			final var requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
			path = requestMapping != null && requestMapping.path().length > 0 ? requestMapping.path()[0] : "";
		}
		usageRepository.save(Usage.builder()
				.uri(request.getMethod() + " " +  path)
				.date(new Date())
				.duration(duration)
				.queries(queries)
				.weight(length)
				.build());
		Chrono.trace("Statistics");
		Chrono.stop();
		log.info("[Time: {} ms] [Queries: {}] {} {}", duration, queries, request.getMethod(), request.getRequestURI());
	}

	@Override
	public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler) throws Exception {
		hibernateStatisticsInterceptor.clearCounter();
		executionTime.remove();
	}
}