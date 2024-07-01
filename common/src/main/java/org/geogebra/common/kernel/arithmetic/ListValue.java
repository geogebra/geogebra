/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
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
