package com.aubay.formations.nr.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chronometer<br />
 * Call start(), then trace() to show how many time is elapsed since start() or
 * last trace() call.<br />
 * <br />
 * PLEASE DONT FORGET Chrono.stop() TO PREVENT MEMORY LEAKS. <br />
 * <br />
 * Example :<br />
 * <i>Chrono.start();<br />
 * employee.fired();<br />
 * Chrono.trace("Employee fired");<br />
 * employeeRepository.save(employee);<br />
 * Chrono.trace("Employee saved");</i><br />
 * <i>Chrono.stop();<br />
 * <br />
 * Will show you :<br />
 * [CHRONO] Employee fired (67ms)<br />
 * [CHRONO] Employee saved (246ms)<br />
 *
 * @author jbureau@aubay.com
 */
public class Chrono {

	private static final Logger LOG = LoggerFactory.getLogger(Chrono.class);

	private static ThreadLocal<Date> threadChronoTime = new ThreadLocal<>();

	private Chrono() {
		// Hide constructor
	}

	/**
	 * Initialize Chronometer
	 */
	public static void start() {
		threadChronoTime.set(new Date());
	}

	/**
	 * Prevent memory leak
	 */
	public static void stop() {
		threadChronoTime.remove();
	}

	/**
	 * Trace chrono label
	 *
	 * @param chronoLabel
	 */
	public static void trace(final String chronoLabel) {
		trace(chronoLabel, null);
	}

	/**
	 * Trace step chronometer with number of rows
	 *
	 * @param stepLabel chrono label
	 * @param nbRows    impacted
	 */
	public static void trace(final String stepLabel, final Integer nbRows) {
		final var stepTime = new Date();
		final var lastStep = threadChronoTime.get();
		final var trace = "[CHRONO] " + stepLabel + (nbRows != null ? " (" + nbRows + " lignes traitées)" : "")
				+ (lastStep != null ? " : " + (stepTime.getTime() - lastStep.getTime()) + "ms" : "");
		LOG.info(trace);
		threadChronoTime.set(stepTime);
	}
}