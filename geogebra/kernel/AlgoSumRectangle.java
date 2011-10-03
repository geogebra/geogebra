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
 * Rectangle sum of function f in interval [a, b] with
 * n intervals and dividing point d. ( [0,1] from start to end of interval.)
 */
public class AlgoSumRectangle extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new rectangle sum
	 * @param cons construction
	 * @param label label
	 * @param f function
	 * @param a lower bound
	 * @param b upper bound
	 * @param n number of columns
	 * @param d 0 for left sum, 1 for right sum
	 */
	public AlgoSumRectangle(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n,NumberValue d) {
		super(cons, label, f, a, b, n, d, AlgoFunctionAreaSums.TYPE_RECTANGLESUM);		
	}
	
	private AlgoSumRectangle( GeoFunction f, 
			   NumberValue a, NumberValue b, NumberValue n,NumberValue d) {
		super(f, a, b, n, d, AlgoFunctionAreaSums.TYPE_RECTANGLESUM);		
	}
	
	public String getClassName() {
		return "AlgoSumRectangle";
	}
	
	public AlgoSumRectangle copy() {
		return new AlgoSumRectangle((GeoFunction)this.getF().copy(),(NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),(NumberValue)this.getN().copy().evaluate(), (NumberValue)this.getD().deepCopy(kernel));
	}
	
}//class AlgoSumRectangle
