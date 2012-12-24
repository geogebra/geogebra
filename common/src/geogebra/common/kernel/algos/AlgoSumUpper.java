/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.util.Cloner;


/**
 * upper sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumUpper extends AlgoFunctionAreaSums {
		
	/**
	 * Creates new upper sum
	 * @param cons construction
	 * @param label label
	 * @param f function
	 * @param a lower bound
	 * @param b upper bound
	 * @param n number of columns
	 */
	public AlgoSumUpper(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, AlgoFunctionAreaSums.TYPE_UPPERSUM);	
		cons.registerEuclidianViewCE(this);
	}
	
	private AlgoSumUpper( 
			   NumberValue a, NumberValue b, NumberValue n,double[]vals,double[]borders) {
		super( a, b, n, AlgoFunctionAreaSums.TYPE_UPPERSUM,vals,borders);			
	}
	
	@Override
	public Commands getClassName() {
		return Commands.UpperSum;
	}
	
	public AlgoSumUpper copy() {
		return new AlgoSumUpper((NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),this.getN().copy().evaluateNum(),
				Cloner.clone(getValues()),Cloner.clone(getLeftBorder()));
	}
	
}
