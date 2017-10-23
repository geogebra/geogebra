package org.geogebra.common.main.error;

/**
 * Adds raw exception handling to error handler
 */
public interface ErrorLogger {

	/**
	 * Handle raw exception
	 * 
	 * @param e
	 *            exception or error
	 */
	public void log(Throwable e);

}
