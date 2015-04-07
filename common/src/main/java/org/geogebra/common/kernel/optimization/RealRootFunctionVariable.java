/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.
 */

package org.geogebra.common.kernel.optimization;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * RealRootFunctionVariable
 * 
 * Presents the relationship <dependent variable> <-- <independent variable> as
 * a "function", so that ExtrFinder can use it as a function and find the value
 * of the independent variable when the dependent variable is maximum or
 * minimum.
 * 
 * Used by the command Maximize[ <dependent variable>, <independent variable> ]
 * in kernel.AlgoMaximize (Also a minimum version...)
 * 
 * @author Hans-Petter Ulven
 * @version 20.02.2011
 */
public class RealRootFunctionVariable implements RealRootFunction {

	private NumberValue geodep = null; // dependent variable
	private GeoNumeric geoindep = null; // independent variable

	/**
	 * Constructor
	 * 
	 * @param geodep dependent value
	 * @param geoindep free number
	 */
	public RealRootFunctionVariable(NumberValue geodep, GeoNumeric geoindep) {
		this.geodep = geodep;
		this.geoindep = geoindep;
	}// Constructor

	public double evaluate(double x) {
		double result = 0.0;
		if ((geodep != null) && (geoindep != null)) {
			geoindep.setValue(x);
			geoindep.updateCascade();
			result = geodep.getDouble();
			// if type
			return result;
		}
		return Double.NaN;
		// if variables are ok
	}// evaluate(double)

}// class RealRootFunctionVariable

