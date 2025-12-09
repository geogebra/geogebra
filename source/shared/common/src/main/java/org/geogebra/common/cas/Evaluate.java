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

package org.geogebra.common.cas;

/**
 * Platform (Java / GWT) independent interface for MPReduce interpreter
 *
 */
public interface Evaluate {

	/**
	 * @param exp
	 *            MPReduce input
	 * @param timeoutMilliseconds
	 *            maximal time for computation in ms
	 * @return CAS output
	 * @throws Throwable
	 *             if computation fails or takes too long
	 */
	public String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable;

}
