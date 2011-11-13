package geogebra.cas.error;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;
import geogebra.main.MyError;

/**
 * Base class for all CAS exceptions.
 * All exceptions the CAS throws should be of this type (unless you want
 * to use {@link GeoGebraCAS#evaluateRaw(String)}.
 * All CAS exceptions have a translation key that is used to translate
 * the exception into a user-visible error message.
 * 
 * @author Thomas
 *
 */
public class CASException extends RuntimeException {

	public CASException(String message)
	{
		super(message);
	}
	
	public CASException(Throwable t)
	{
		super(t.getMessage());
	}
	
	/**
	 * Returns the Key for this Exception, which can also be used for translation.
	 * @return The error key.
	 */
	public String getKey()
	{
		return "CAS.GeneralErrorMessage";
	}
}
