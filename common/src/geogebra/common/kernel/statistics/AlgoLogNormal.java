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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.MyMath2;

import org.apache.commons.math.distribution.NormalDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoLogNormal extends AlgoDistribution {

	
    
    public AlgoLogNormal(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoUniform;
    }

	@Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double mean = a.getDouble();
		    double sd = b.getDouble();
    		    double x = c.getDouble();
        		try {
        			
        			if (sd < 0) {
        				num.setUndefined();
        			} else if (x < 0) {
        				num.setValue(0);
        			} else  {
        				num.setValue(1/2 * MyMath2.erf(0,1,(Math.log(x-mean)/(Math.sqrt(2)*sd)) + 1/2));
        			}
        			
        			// old hack
					//processAlgebraCommand( "1/2 erf((ln(If["+x+"<0,0,"+x+"])-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2", true );
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



