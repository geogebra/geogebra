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

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.main.MyError.Errors;

/**
 * Base class for all CAS exceptions. All exceptions the CAS throws should be of
 * this type (unless you want to use {@link GeoGebraCAS#evaluateRaw(String)}.
 * All CAS exceptions have a translation key that is used to translate the
 * exception into a user-visible error message.
 * 
 * @author Thomas
 *
 */
public class CASException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String key;

	/**
	 * Creates new CAS exception
	 * 
	 * @param message
	 *            exception message
	 */
	public CASException(String message) {
		super(message);
	}

	public CASException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates new CAS exception
	 * 
	 * @param cause
	 *            Throwable that caused this exception
	 */
	public CASException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	/**
	 * Returns the Key for this Exception, which can also be used for
	 * translation.
	 * 
	 * @return The error key.
	 */
	public String getKey() {
		if (key != null) {
			return key;
		}
		return Errors.CASGeneralErrorMessage.getKey();
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
