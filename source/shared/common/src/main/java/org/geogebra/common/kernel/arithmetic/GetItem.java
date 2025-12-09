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
 * Common interface for lists and commands so that CAS handlers can work with
 * Midpoint[a,b] and logb(3,5) the same way
 * 
 * @author zbynek
 *
 */
public interface GetItem extends ExpressionValue {
	/**
	 * 
	 * @param i
	 *            index
	 * @return argument of command / item of list at given position
	 */
	public ExpressionValue getItem(int i);

	/**
	 * needed to distinguish eg igamma with 2 or 3 args
	 * 
	 * @return length
	 */
	public int size();

	/**
	 * @return label of expected result
	 */
	public String getLabel();
}
