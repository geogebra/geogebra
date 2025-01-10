/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Element which can be considered function (function, line)
 *
 */
public interface Functional extends Evaluatable {
	/**
	 * Returns the function
	 * 
	 * @return function
	 */
	public Function getFunction();

	/**
	 * Returns the function
	 * 
	 * @return function
	 * @deprecated use getFunction for getting the expression
	 */
	@Deprecated
	public GeoFunction getGeoFunction();

	/**
	 * Returns the function's derivative wrapped in GeoElement
	 * 
	 * @param order
	 *            order of the derivative
	 * @param fast
	 *            flag for derivative without CAS
	 * @return wrapped derivative
	 */
	public GeoFunction getGeoDerivative(int order, boolean fast);

}
