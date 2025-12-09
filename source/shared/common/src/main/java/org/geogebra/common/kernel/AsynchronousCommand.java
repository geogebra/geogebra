/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel;

/**
 * Interface for classes that can call CAS asynchronously and receive callbacks
 * 
 * @author Zbynek Konecny
 */
public interface AsynchronousCommand {
	/**
	 * This flag switches using asynchronous CAS on or off in Desktop, has no
	 * effect in Web.
	 */
	final public boolean USE_ASYNCHRONOUS = false;

	/**
	 * @param output
	 *            CAS output as GeoGebra string
	 * @param requestID
	 *            request number
	 */
	public void handleCASoutput(String output, int requestID);

	/**
	 * @param exception
	 *            exception caused by given request
	 * @param requestID
	 *            request number
	 */
	public void handleException(Throwable exception, int requestID);

	/**
	 * @return whether caching is allowed for this command
	 */
	public boolean useCaching();

	/**
	 * @return input for CAS as GeoGebraCAS string
	 */
	public String getCasInput();

	/**
	 * @return kernel
	 */
	public Kernel getKernel();

}
