package org.geogebra.common.main;

/**
 * Schedules tasks that depend on asynchronously loaded modules (relevant in Web).
 * On platforms that don't need asynchronous code loading it runs tasks immediately.
 */
public interface AsyncManagerI {

	/**
	 * Try executing the callback until it succeeds
	 * @param callback code that requires modules that might've not been loaded yet
	 */
	void scheduleCallback(Runnable callback);
}
