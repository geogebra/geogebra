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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.WeibullDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoWeibull extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoWeibull(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    public Algos getClassName() {
        return Algos.AlgoWeibull;
    }

    @SuppressWarnings("deprecation")
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double param = a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			WeibullDistribution dist = getWeibullDistribution(param, param2);
        			num.setValue(dist.cumulativeProbability(val));     // P(T <= val)
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



