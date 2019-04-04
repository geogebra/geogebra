/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for functions convertible to GeoFunction
 * 
 * @author Markus
 *
 */
public interface GeoFunctionable extends GeoElementND, UnivariateFunction {
	/**
	 * @return this as GeoFunction
	 * @deprecated if you need the expression, you can get it through
	 *             getFunction
	 */
	@Deprecated
	public GeoFunction getGeoFunction();

	/**
	 * Like getFunction(), but for root finding we don't want to divide line
	 * equations by y, so that Root(3x=6) works.
	 * 
	 * @param forRoot
	 *            whether just the roots should be preserved
	 * @return this as function
	 */
	public Function getFunction(boolean forRoot);

	/**
	 * For root we consider abs(sqrt(x)) also polynomial
	 * 
	 * @param forRoot
	 *            whether we need this for root finding
	 * @return whether the function is polynomial
	 */
	public boolean isPolynomialFunction(boolean forRoot);

}
