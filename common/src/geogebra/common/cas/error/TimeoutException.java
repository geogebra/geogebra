package geogebra.common.cas.error;

import geogebra.common.kernel.CASException;

/**
 * Signals a Timeout exception from the CAS.
 * 
 * This exception gets thrown when the CAS is unable to produce
 * a valid response in the allowed time.
 * 
 * @author Thomas
 *
 */
public class TimeoutException extends CASException {

	private static final long serialVersionUID = 1L;
	/**
	 * Creates new timeout exception
	 * @param message exception message
	 */
	public TimeoutException(String message) {
		super( message);
	}

	@Override
	public String getKey() {
		return "CAS.TimeoutError";
	}

}
