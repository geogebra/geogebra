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
