/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * Curve in parametric form (f(t),g(t))
 */
public interface ParametricCurve extends Traceable, Path, CurveEvaluable {
	double getMinParameter(); 
	double getMaxParameter();	
	/**
	 * @return x-coord as function of parameter 
	 */
	RealRootFunction getRealRootFunctionX();
	/**
	 * @return y-coord as function of parameter 
	 */
	RealRootFunction getRealRootFunctionY();
	
	/**
	 * Evaluates the curve for given parameter value
	 * @param t parameter value
	 * @param out array to store the result
	 */
	void evaluateCurve(double t, double [] out);
	/**
	 * Evaluates the curve for given parameter value
	 * @param t parameter value
	 * @return result as GeoVec2D
	 */
	GeoVec2D evaluateCurve(double t);	

	/**
	 * @param t parameter value
	 * @return curvature at given parameter
	 */
	double evaluateCurvature(double t);
	/**
	 * @return true when this is function of x
	 */
	boolean isFunctionInX();
	
}
