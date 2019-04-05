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
	 * Convert to a function, the returned object stays in sync through an algo
	 * (performance loss).
	 * 
	 * @return this as GeoFunction
	 * @deprecated if you need the expression, you can get it through
	 *             getFunction
	 */
	@Deprecated
	public GeoFunction getGeoFunction();

	/**
	 * For GeoFunctions return the wrapped function, for other elements create
	 * equivalent function on the fly (=no guaranteed to stay in sync).
	 * 
	 * @return this as function
	 */
	public Function getFunction();

	/**
	 * Like getFunction(), but for root finding we don't want to divide line
	 * equations by y, so that Root(3x=6) works.
	 * 
	 * @return this as function
	 */
	public Function getFunctionForRoot();

	/**
	 * For root we consider abs(sqrt(x)) also polynomial
	 * 
	 * @param forRoot
	 *            whether we need this for root finding
	 * @return whether the function is polynomial
	 */
	public boolean isPolynomialFunction(boolean forRoot);

}
