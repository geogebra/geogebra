package org.geogebra.common.main;

public interface AsyncManagerI {

	/**
	 * Try executing r until it succeeds
	 * @param callback code that requires modules that might've not been loaded yet
	 */
	void scheduleCallback(Runnable callback);
}
