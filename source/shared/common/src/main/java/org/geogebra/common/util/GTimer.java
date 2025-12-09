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

package org.geogebra.common.util;

/**
 * Runs tasks in set time intervals (once or repeatedly).
 */
public interface GTimer {

	/**
	 * Start the timer in one-off mode.
	 */
	void start();

	/**
	 * Start the timer in repeating mode.
	 */
	void startRepeat();

	/**
	 * Stop the timer.
	 */
	void stop();

	/**
	 * @return whether the timer is running
	 */
	boolean isRunning();

	/**
	 * @param delay delay in milliseconds
	 */
	void setDelay(int delay);
}
