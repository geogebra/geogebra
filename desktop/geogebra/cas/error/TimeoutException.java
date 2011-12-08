package geogebra.cas.error;

import geogebra.common.cas.CASException;

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

	public TimeoutException(String message) {
		super( message);
	}

	@Override
	public String getKey() {
		return "CAS.TimeoutError";
	}

}
