package com.aubay.formations.nr.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.aubay.formations.nr.entities.Usage;

/**
 * Interceptor for all web API calls<br />
 * This interceptor show how many SQL statements was executed and the execution
 * time<br />
 * This interceptor provide usage statistics about endpoints.
 *
 * @author jbureau@aubay.com
 */
@Component
public class RequestStatisticsInterceptor implements AsyncHandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(RequestStatisticsInterceptor.class);

	private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	private static final List<Usage> BUFFER = new ArrayList<>();

	private static final int MAX_BUFFER_SIZE = 1000; // Trigger batch saving

	private static final int BATCH_SIZE = 100; // Chunk size when batching INSERT

	private final ThreadLocal<Long> executionTime = new ThreadLocal<>();

	@Autowired
	private HibernateStatisticsInterceptor hibernateStatisticsInterceptor;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

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
	 * After each API execution, collect statistics<br />
	 * Trigger asynchronous saving when MAX_BUFFER_SIZE is achieved.
	 * @formatter:off
	 */
	@Override
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex) throws Exception {
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
		final var usage = new Usage(request.getMethod() + " " +  path, duration, queries, length);
		BUFFER.add(usage);
		if(BUFFER.size() >= MAX_BUFFER_SIZE) {
			persistsUsages();
		}
		LOG.info("[Time: {} ms] [Queries: {}] {} {}", duration, queries, request.getMethod(), request.getRequestURI());
	}

	/**
	 * Asynchronous Usages Saving
	 * Batching inserts with BATCH_SIZE
	 * @formatter:on
	 */
	public void persistsUsages() {
		EXECUTOR.submit(() -> {
			final var em = entityManagerFactory.createEntityManager();
			final List<Usage> chunk = new ArrayList<>(BUFFER);
			BUFFER.clear();
			EntityTransaction tx = null;
			try {
				tx = em.getTransaction();
				em.unwrap(Session.class).setJdbcBatchSize(BATCH_SIZE);
				tx.begin();
				for (var i = 0; i < chunk.size(); i++) {
					if (i > 0 && i % BATCH_SIZE == 0) {
						em.flush();
						em.clear();
					}
					em.persist(chunk.get(i));
				}
				tx.commit();
			} catch (final PersistenceException e) {
				tx.rollback();
				LOG.error("Error when batching save usages", e);
				BUFFER.addAll(chunk);
			} finally {
				em.close();
			}
		});
	}

	@Override
	public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler) throws Exception {
		hibernateStatisticsInterceptor.clearCounter();
		executionTime.remove();
	}
}