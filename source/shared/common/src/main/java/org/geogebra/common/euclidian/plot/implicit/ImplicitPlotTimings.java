/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.util.debug.Log;

/**
 * Lightweight timing logger for implicit plotter profiling.
 */
public final class ImplicitPlotTimings {
	private static final boolean ENABLED = false;
	private static final String PREFIX = "[TIMING] ";

	private ImplicitPlotTimings() {
		// utility class
	}

	/**
	 * @return current timestamp for elapsed timing
	 */
	public static long start() {
		return ENABLED ? System.currentTimeMillis() : 0;
	}

	/**
	 * Logs elapsed time for one stage.
	 * @param label timing label
	 * @param start start timestamp from {@link #start()}
	 */
	public static void log(String label, long start) {
		if (!ENABLED) {
			return;
		}
		Log.debug(PREFIX + label + "=" + (System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * Logs elapsed time for one stage with details.
	 * @param label timing label
	 * @param start start timestamp from {@link #start()}
	 * @param details extra diagnostic fields
	 */
	public static void log(String label, long start, String details) {
		if (!ENABLED) {
			return;
		}
		Log.debug(PREFIX + label + "=" + delta(start) + "ms " + details);
	}

	/**
	 * Gives the elapsed time from start
	 * @param start time in millis
	 * @return the elapsed time from the start
	 */
	public static long delta(long start) {
		return System.currentTimeMillis() - start;
	}
}
