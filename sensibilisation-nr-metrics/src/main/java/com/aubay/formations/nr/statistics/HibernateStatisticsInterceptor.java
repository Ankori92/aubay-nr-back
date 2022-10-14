package com.aubay.formations.nr.statistics;

import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;

/**
 * Interceptor for hibernate SQL requests<br />
 * Count all request between startCounter() and clearCounter()
 *
 * @author jbureau@aubay.com
 */
@Component
public class HibernateStatisticsInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 8659095500852761418L;

	private static final ThreadLocal<Long> queryCount = new ThreadLocal<Long>();

	/**
	 * (Re-)start the SQL counter
	 */
	public void startCounter() {
		queryCount.set(0L);
	}

	/**
	 * Get current SQL count
	 *
	 * @return current Query count
	 */
	public Long getQueryCount() {
		return queryCount.get();
	}

	/**
	 * Remove this Thread counter
	 */
	public void clearCounter() {
		queryCount.remove();
	}

	/**
	 * Count all executed PrepareStatement
	 */
	@Override
	public String onPrepareStatement(final String sql) {
		final Long count = queryCount.get();
		queryCount.set((count == null ? 0L : count) + 1);
		return super.onPrepareStatement(sql);
	}
}