package org.geogebra.common.cas.error;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.main.MyError.Errors;

/**
 * Signals a Timeout exception from the CAS.
 * 
 * This exception gets thrown when the CAS is unable to produce a valid response
 * in the allowed time.
 * 
 * @author Thomas
 *
 */
public class TimeoutException extends CASException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates new timeout exception
	 * 
	 * @param message
	 *            exception message
	 */
	public TimeoutException(String message) {
		super(message);
	}

	@Override
	public String getKey() {
		return Errors.CASTimeoutError.getKey();
	}

}
