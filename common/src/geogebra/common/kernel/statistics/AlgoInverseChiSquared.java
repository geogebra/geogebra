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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

import org.apache.commons.math.distribution.ChiSquaredDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseChiSquared extends AlgoDistribution {

	
	/**
     * @param cons construction
     * @param label label for output
     * @param a degrees of freedom
     * @param b variable value
     */
    public AlgoInverseChiSquared(Construction cons, String label, NumberValue a,NumberValue b) {
        super(cons, label, a, b, null, null);
    }
    /**
     * @param cons construction
     * @param a degrees of freedom
     * @param b variable value
     */
    public AlgoInverseChiSquared(Construction cons, NumberValue a,
			NumberValue b) {
        super(cons, a, b, null, null);
	}

    @Override
	public Commands getClassName() {
		return Commands.InverseChiSquared;
	}
    
	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
    		    double param = a.getDouble();
    		    double val = b.getDouble();
        		try {
        			ChiSquaredDistribution dist = getChiSquaredDistribution(param);
        			num.setValue(dist.inverseCumulativeProbability(val));    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



