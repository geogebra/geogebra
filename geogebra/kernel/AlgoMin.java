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

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Computes Min[a, b]
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoMin extends AlgoTwoNumFunction {

	private static final long serialVersionUID = 1L;
	       
	/**
	 * Creates new min algo
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 */
    AlgoMin(Construction cons, String label, NumberValue a, NumberValue b) {       
  	  super(cons, label, a, b); 
      }   
    
    AlgoMin(Construction cons, NumberValue a, NumberValue b) {       
  	  super(cons, a, b); 
      }   
    
    public String getClassName() {
        return "AlgoMin";
    }
    
    // calc minimum of a,b 
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double min = Math.min(a.getDouble(), b.getDouble());
    		num.setValue(min);
    	} else
    		num.setUndefined();
    }       
    
}
