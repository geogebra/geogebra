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
 *
 * @author Markus
 */
public interface ListValue extends ExpressionValue {

	/**
	 * Returns a MyList object.
	 * 
	 * @return MyList representation of this list
	 */
	MyList getMyList();

	/**
	 * Tries to return this list as an array of double values
	 * 
	 * @param offset
	 *            how many elements should be skipped
	 * 
	 * @return array of double values from this list
	 */
	double[] toDouble(int offset);

	/**
	 * @return number of elements of this list
	 */
	int size();

	/**
	 * @param i
	 *            list index (0-based)
	 * @return element at given index
	 */
	ExpressionValue get(int i);

	/**
	 * @return true if list is matrix
	 */
	boolean isMatrix();

}
