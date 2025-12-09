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

package org.geogebra.common.kernel.arithmetic;

/**
 * Sets the assignment type of an expression or equation (or also NO assignment)
 * 
 * @author Zbynek, comments by Johannes
 */
public enum AssignmentType {
	/**
	 * no assignment in the expression/equation
	 */
	NONE,

	/**
	 * the default assignment (normally ":=" for functions and ":" for
	 * equations)
	 */
	DEFAULT,

	/**
	 * the assignment which delays the evaluation ("::=")
	 */
	DELAYED
}
