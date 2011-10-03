/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * upper sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumTrapezoidal extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new trapezoidal sum
	 * @param cons construction
	 * @param label label
	 * @param f function
	 * @param a lower bound
	 * @param b upper bound
	 * @param n number of columns
	 */
	public AlgoSumTrapezoidal(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, AlgoFunctionAreaSums.TYPE_TRAPEZOIDALSUM);		
	}
	
	private AlgoSumTrapezoidal( 
			   NumberValue a, NumberValue b, NumberValue n,double[]vals,double[]borders) {
		super( a, b, n, AlgoFunctionAreaSums.TYPE_TRAPEZOIDALSUM,vals,borders);		
	}
	
	public String getClassName() {
		return "AlgoSumTrapezoidal";
	}
	
	public AlgoSumTrapezoidal copy() {
		return new AlgoSumTrapezoidal((NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),(NumberValue)this.getN().copy().evaluate(),
				getValues().clone(),getLeftBorder().clone());
	}
	
}
