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
 * Computes Max[a, b]
 * @author  Markus Hohenwarterarea of conic c
 * @version 
 */
public class AlgoMax extends AlgoTwoNumFunction {         
        
	/**
	 * Creates new max algo
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 */
    AlgoMax(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    public String getClassName() {
        return "AlgoMax";
    }
      
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double max = Math.max(a.getDouble(), b.getDouble());
    		num.setValue(max);
    	} else
    		num.setUndefined();
    }       
    
}
