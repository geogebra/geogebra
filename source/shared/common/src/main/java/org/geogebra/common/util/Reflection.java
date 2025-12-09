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

package org.geogebra.common.util;

/**
 * Reflection class.
 */
public interface Reflection {

	/**
	 * Calls the method with simple name methodName on object with parameters.
	 * @param object the object
	 * @param methodName the simple method name (e.g. call or update)
	 * @param parameters the parameters
	 * @throws Exception if the method does not exist, if it is private or protected or
	 *                   if the underlying method throws an exception
	 */
	void call(Object object, String methodName, Object[] parameters) throws Exception;
}
