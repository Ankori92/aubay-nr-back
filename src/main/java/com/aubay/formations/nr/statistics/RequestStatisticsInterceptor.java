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
import org.springframework.beans.factory.annotation.Value;
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

	private final ThreadLocal<Long> executionTime = new ThreadLocal<>();

	@Autowired
	private HibernateStatisticsInterceptor hibernateStatisticsInterceptor;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Value("${statistics.buffer.size:1000}")
	private int bufferSize;

	@Value("${statistics.batching.size:100}")
	private int batchingSize;

	@Value("${statistics.metrics.logs.enabled:true}")
	private boolean logsEnabled;

	/**
	 * Before each API execution, start counters
	 */
	@Override
	public boolean preHandle(final HttpServletRequest req, final HttpServletResponse res, final Object handler)
			throws Exception {
		beginCounters();
		return true;
	}

	/**
	 * After each API execution, collect metrics<br />
	 * Trigger asynchronous saving when "bufferSize" is achieved.
	 */
	@Override
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex) throws Exception {

		// Get metrics
		final var path = getRequestMappingPath(handler, request);
		final var duration = getDuration();
		final var queries = getQueriesCount();
		final var length = getResponseLength(response);

		// Add this usage to BUFFER
		BUFFER.add(new Usage(path, duration, queries, length));

		// Log metrics
		log(request, duration, queries, length, path);

		// Persist current usages in BUFFER
		if (BUFFER.size() >= bufferSize) {
			persistsUsages();
		}

		// Clear thread specific data
		postTreatments();
	}

	/**
	 * Get queries count for this request
	 *
	 * @return queries count
	 */
	public int getQueriesCount() {
		return hibernateStatisticsInterceptor.getQueryCount().intValue();
	}

	/**
	 * Get duration of this request
	 *
	 * @return number of milliseconds since begin of this request
	 */
	public int getDuration() {
		return (int) (System.currentTimeMillis() - executionTime.get());
	}

	/**
	 * Get response length
	 *
	 * @param response
	 * @return length of the response, in Bytes
	 */
	public int getResponseLength(final HttpServletResponse response) {
		var length = 0;
		if (response instanceof final ContentCachingResponseWrapper cachedResponse) {
			length = cachedResponse.getContentSize();
		}
		return length;
	}

	/**
	 * Get paths of Request Handler
	 *
	 * @param handler
	 * @param request
	 * @return
	 */
	public String getRequestMappingPath(final Object handler, final HttpServletRequest request) {
		var path = "";
		if (handler instanceof final HandlerMethod handlerMethod) {
			path = request.getMethod() + " "
					+ String.join(", ", handlerMethod.getMethodAnnotation(RequestMapping.class).path());
		}
		return path;
	}

	/**
	 * Trace metrics about this request, if logs are enabled
	 *
	 * @param request  of this request
	 * @param duration of this request
	 * @param queries  realized in this request
	 * @param length   of response of this request
	 * @param path     of the handler of this request
	 */
	public void log(final HttpServletRequest request, final int duration, final int queries, final int length,
			final String path) {
		if (logsEnabled && LOG.isInfoEnabled()) {
			LOG.info("[URI : {} {}] [Handler : {}][Request duration: {} ms][SQL queries: {}] [Response weight: {} ko]",
					request.getMethod(), request.getRequestURI(), path, duration, queries, length / 1000);
		}
	}

	/**
	 * Asynchronous Usages Saving<br />
	 * Batching inserts with batchingSize
	 */
	public void persistsUsages() {
		// Run treatment on executor (Asynchronous saving)
		EXECUTOR.submit(() -> {
			// Create entity manager
			final var em = entityManagerFactory.createEntityManager();
			// Get usages to be saved, clear the buffer
			final List<Usage> chunk = new ArrayList<>(BUFFER);
			BUFFER.clear();
			// Start saving
			EntityTransaction tx = null;
			try {
				// Configure batching (Save "batchingSize" usages in ONE insert)
				em.unwrap(Session.class).setJdbcBatchSize(batchingSize);
				// New transaction (can be rolled back)
				tx = em.getTransaction();
				tx.begin();
				// Save each usages, flush and clear when batchingSize is achieved
				for (var i = 0; i < chunk.size(); i++) {
					if (i > 0 && i % batchingSize == 0) {
						em.flush();
						em.clear();
					}
					em.persist(chunk.get(i));
				}
				// Commit when all usages are saved
				tx.commit();
			} catch (final PersistenceException e) {
				// When errors, rollback the transaction and restore unsaved chunk in buffer
				tx.rollback();
				LOG.error("Error when batching save usages", e);
				BUFFER.addAll(chunk);
			} finally {
				em.close();
			}
		});
	}

	/**
	 * Start counters
	 */
	public void beginCounters() {
		hibernateStatisticsInterceptor.startCounter();
		executionTime.set(System.currentTimeMillis());
	}

	/**
	 * Clear counters and thread specific data (Prevent Memory Leaks)<br />
	 * Detect memory leaks
	 */
	public void postTreatments() {
		// Clear counters
		hibernateStatisticsInterceptor.clearCounter();
		executionTime.remove();
		// Prevent memory leak when massive errors
		if (BUFFER.size() >= 10000) {
			BUFFER.clear();
			LOG.warn("========= AUBAY METRICS =========");
			LOG.warn("WARNING : Too many \"usages\" in buffer");
			LOG.warn("Buffer have been cleared to prevent memory leaks");
			LOG.warn("Please verify usage saving");
			LOG.warn("=================================");
		}
	}

	@Override
	public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler) throws Exception {
		postTreatments();
	}
}