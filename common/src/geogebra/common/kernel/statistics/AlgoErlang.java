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
import geogebra.common.util.MyMath2;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoErlang extends AlgoDistribution {

	
    
    public AlgoErlang(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    @Override
	public Commands getClassName() {
		return Commands.Erlang;
	}
    
	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double k = a.getDouble();
		    double l = b.getDouble();
    		    double x = c.getDouble();
    		    
    		    if (x < 0) {
    		    	num.setValue(0);
    		    } else {
    		    	num.setValue(MyMath2.gammaIncomplete(k, l *x) / MyMath2.factorial(k - 1));
    		    }
        			
        			// old hack
    			//command = "If[x<0,0,gamma("+k+",("+l+")x)/("+k+"-1)!]";

        			
    	} else
    		num.setUndefined();
    }       
        
    
}



