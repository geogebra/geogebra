/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.common.kernel.arithmetic.Evaluatable;
import geogebra.common.kernel.arithmetic.FunctionInterface;
import geogebra.common.kernel.geos.GeoFunctionInterface;
import geogebra.kernel.geos.GeoFunction;


/**
 * Element which can be considered function (function, line)
 *
 */
public interface Functional extends Evaluatable{
	/**
	 * Returns the function
	 * @return function
	 */
	public FunctionInterface getFunction();
	/**
	 * Returns the function's derivative wrapped in GeoElement
	 * @param order order of the derivative
	 * @return wrapped derivative
	 */
	public GeoFunctionInterface getGeoDerivative(int order);
}
