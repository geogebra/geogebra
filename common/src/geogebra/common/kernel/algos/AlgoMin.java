/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;

/**
 * Computes Min[a, b]
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoMin extends AlgoTwoNumFunction {

	/**
	 * Creates new min algo
	 */
    public AlgoMin(Construction cons, String label, NumberValue a, NumberValue b) {       
  	  super(cons, label, a, b); 
      }   
    
    AlgoMin(Construction cons, NumberValue a, NumberValue b) {       
  	  super(cons, a, b); 
      }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoMin;
    }
    
    // calc minimum of a,b 
    @Override
	public final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double min = Math.min(a.getDouble(), b.getDouble());
    		num.setValue(min);
    	} else
    		num.setUndefined();
    }       
    
}
