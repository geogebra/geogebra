/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.integration;

import geogebra.kernel.GeoConic;
import geogebra.kernel.Kernel;
import geogebra.kernel.cas.AlgoIntegralDefinite;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Computes the arc length of an ellipse.
 */
public class EllipticArcLength {

	private double [] halfAxes;
	private RealRootFunction arcLengthFunction;
	
	public EllipticArcLength(GeoConic ellipse) {
		halfAxes = ellipse.getHalfAxes();
		arcLengthFunction = new EllipticArcLengthFunction();
	}
	
	/**
	 * Computes the arc length of an ellipse where
	 * a is the start parameter and b is the end parameter
	 * of the arc in radians.
	 */
	public double compute(double a, double b) {
		if (a <= b)
			return AlgoIntegralDefinite.numericIntegration(arcLengthFunction, a, b);
		else
			return AlgoIntegralDefinite.numericIntegration(arcLengthFunction, 0, Kernel.PI_2)
				 - AlgoIntegralDefinite.numericIntegration(arcLengthFunction, b, a);
		
	}
	
	/**
	 * f(t) = sqrt((a sin(t))^2 + (b cos(t))^2)
	 */
	private class EllipticArcLengthFunction implements RealRootFunction {
		public double evaluate(double t) {
			double p = halfAxes[0] * Math.sin(t);
			double q = halfAxes[1] * Math.cos(t);
			return Math.sqrt(p*p + q*q);
		}
	}
}
