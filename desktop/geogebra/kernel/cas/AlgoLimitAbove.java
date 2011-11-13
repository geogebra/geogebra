/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.NumberValue;
/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoLimitAbove extends AlgoLimit {

    public AlgoLimitAbove(Construction cons, String label, GeoFunction f, NumberValue num) {
    	super(cons, label, f, num);
    }
    
    public String getClassName() {
        return "AlgoLimitAbove";
    }
     
    protected final void compute() {       
        if (!f.isDefined() || !input[1].isDefined()) {
        	outNum.setUndefined();
        	return;
        }    
                
        outNum.setValue(f.getLimit(num.getDouble(), -1));
		
    }

    

}
