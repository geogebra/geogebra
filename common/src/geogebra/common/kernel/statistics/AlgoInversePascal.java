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
import geogebra.common.kernel.commands.Commands;

import org.apache.commons.math.distribution.PascalDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInversePascal extends AlgoDistribution {

	
	/**
     * @param cons construction
     * @param label label for output
     * @param a number of successes
     * @param b probability of success
     * @param c variable value
     */
    public AlgoInversePascal(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }
    /**
     * @param cons construction
     * @param a number of successes
     * @param b probability of success
     * @param c variable value
     */
    public AlgoInversePascal(Construction cons, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, a, b, c, null);
    }

    @Override
	public Commands getClassName() {
		return Commands.InversePascal;
	}

    
	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    int param = (int)Math.round(a.getDouble());
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			PascalDistribution dist = getPascalDistribution(param, param2);
        			
        			double result = dist.inverseCumulativeProbability(val);
        			
        			// eg InversePascal[1,1,1] returns  2147483647 
        			if (result >= Integer.MAX_VALUE)
        				num.setValue(param);
        			else
        				num.setValue(result + 1);    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



