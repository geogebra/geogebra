/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.util.Cloner;


/**
 * Left sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumLeft extends AlgoFunctionAreaSums {
		
	/**
	 * Creates lower sum
	 * @param cons construction
	 * @param label label
	 * @param f function
	 * @param a lower bound
	 * @param b upper bound
	 * @param n number of columns
	 */
	public AlgoSumLeft(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, AlgoFunctionAreaSums.TYPE_LEFTSUM);		
		cons.registerEuclidianViewCE(this);
	}
	
	private AlgoSumLeft(  
			   NumberValue a, NumberValue b, NumberValue n,double[]vals,double[]borders) {
		super(a, b, n, AlgoFunctionAreaSums.TYPE_LEFTSUM,vals,borders);				
	}
	
	public AlgoSumLeft copy() {		
		return new AlgoSumLeft( (NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),this.getN().copy().evaluateNum(),
				Cloner.clone(getValues()),Cloner.clone(getLeftBorder()));
	}
	@Override
	public Algos getClassName() {
		return Algos.AlgoSumLeft;
	}
	
}//class AlgoSumLeft
