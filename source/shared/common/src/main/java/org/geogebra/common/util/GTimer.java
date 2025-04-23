package org.geogebra.common.util;

/**
 * Runs tasks in set time intervals (once or repeatedly).
 */
public interface GTimer {

	void start();

	void startRepeat();

	void stop();

	boolean isRunning();

	void setDelay(int delay);
}
