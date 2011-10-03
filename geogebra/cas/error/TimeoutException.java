package geogebra.cas.error;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;


/**
 * Signals a Timeout exception from the CAS.
 * 
 * This exception gets thrown when the CAS is unable to produce
 * a valid response in the allowed time.
 * 
 * @see GeoGebraCAS#setTimeout(int)
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
