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
