/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel;

/**
 * Interface for classes that can call CAS asynchronously and receive callbacks
 * @author Zbynek Konecny
 */
public interface AsynchronousCommand {
	/**
	 * This flag switches using asyncronous CAS on or off in Desktop,
	 * has no effect in Web.
	 */
	public boolean USE_ASYNCHRONOUS=false;
	
	/**
	 * @param output CAS output as GeoGebra string
	 * @param requestID request number
	 */
	public void handleCASoutput(String output,int requestID);

	/**
	 * @param exception exception caused by given request
	 * @param requestID request number
	 */
	public void handleException(Throwable exception, int requestID);
	
	/**
	 * @return whether caching is allowed for this command
	 */
	public boolean useCacheing();

	/**
	 * @return input for CAS as GeoGebraCAS string
	 */
	public String getCasInput();

}
