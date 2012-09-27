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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;

import java.math.BigInteger;

/**
 * Computes GCD[a, b]
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoGCD extends AlgoTwoNumFunction {  
	
    public AlgoGCD(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    @Override
	public Algos getClassName() {
        return Algos.AlgoGCD;
    }
    
    
      
    @Override
	public final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		
    		if (a.getDouble() > Long.MAX_VALUE || b.getDouble() > Long.MAX_VALUE || 
    				a.getDouble() < -Long.MAX_VALUE || b.getDouble() < -Long.MAX_VALUE) {
    			num.setUndefined();
    			return;
    		}
    		
    		
    		if (Kernel.isEqual(a.getDouble(), Math.floor(a.getDouble()+0.5d), Kernel.MAX_PRECISION)  
    				&& Kernel.isEqual(b.getDouble(), Math.floor(b.getDouble()+0.5d), Kernel.MAX_PRECISION))
    		{  
    			BigInteger i1 = BigInteger.valueOf((long) Math.floor(a.getDouble()+0.5d));
    			BigInteger i2 = BigInteger.valueOf((long) Math.floor(b.getDouble()+0.5d));
    			
    			i1 = i1.gcd(i2);
    			
    			double result = Math.abs(i1.doubleValue());
    			
    	    	// can't store integers greater than this in a double accurately
    	    	if (result > 1e15) {
    	    		num.setUndefined();
    	    		return;
    	    	}
   			
    			num.setValue(result);
    		} else {
    			num.setUndefined();
    		}
    			
    	} else
    		num.setUndefined();
    }       
    
}
