package org.geogebra.common.kernel;

/**
 * Listener for timer changes
 */
public interface TimerListener {

	/**
	 * timer was started
	 */
	public abstract void onTimerStarted();

	/**
	 * timer was stopped
	 */
	public abstract void onTimerStopped();

}
