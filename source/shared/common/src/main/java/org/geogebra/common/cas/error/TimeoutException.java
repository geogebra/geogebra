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
