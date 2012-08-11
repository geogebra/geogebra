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

import org.apache.commons.math.distribution.TDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseTDistribution extends AlgoDistribution {

	
	/**
     * @param cons construction
     * @param label label for output
     * @param a degrees of freedom
     * @param b variable value
     */
    public AlgoInverseTDistribution(Construction cons, String label, NumberValue a,NumberValue b) {
        super(cons, label, a, b, null, null);
    }
    /**
     * @param cons construction
     * @param a degrees of freedom
     * @param b variable value
     */
    public AlgoInverseTDistribution(Construction cons, NumberValue a,
			NumberValue b) {
        super(cons, a, b, null, null);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoInverseTDistribution;
    }
    
	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
    		    double param = a.getDouble();
    		    double val = b.getDouble();
        		try {
        			TDistribution t = getTDistribution(param);
        			num.setValue(t.inverseCumulativeProbability(val));    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



