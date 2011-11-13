/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.ExponentialDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseExponential extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoInverseExponential(Construction cons, String label, NumberValue a,NumberValue b) {
        super(cons, label, a, b, null, null);
    }

    public String getClassName() {
        return "AlgoInverseExponential";
    }
    
	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
    		    double param = a.getDouble();
    		    double val = b.getDouble();
        		try {
        			ExponentialDistribution dist = getExponentialDistribution(param);
        			num.setValue(dist.inverseCumulativeProbability(val));     // P(T <= val)
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



