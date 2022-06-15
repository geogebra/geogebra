package org.geogebra.common.main.error;

/**
 * Adds raw exception handling to error handler
 */
public interface ErrorLogger extends ErrorHandler {

	/**
	 * Handle raw exception
	 * 
	 * @param e
	 *            exception or error
	 */
	void log(Throwable e);

}
