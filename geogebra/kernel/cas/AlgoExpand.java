/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.Construction;


/**
 * Try to expand the given function's expression
 * (e.g. function expression or dependent number's expression). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExpand extends AlgoCasBase {
       
    public AlgoExpand(Construction cons, String label, CasEvaluableFunction f) {
    	super(cons, label, f);
    }   
    
    public String getClassName() {
        return "AlgoCasExpand";
    }

	@Override
	protected void applyCasCommand() {
		// symbolic expand of f
		g.setUsingCasCommand("Numeric(Expand(%))", f, false);		
	}
	
//    final public String toString() {  
//    	StringBuilder sb = new StringBuilder();
//    	sb.append(getCommandDescription());
//    	
//        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
//            sb.append(": ");
//            sb.append(g.toGeoElement().getLabel());
//            if (g.toGeoElement() instanceof GeoFunction)
//            {
//            	sb.append('(');
//            	sb.append(((GeoFunction) g.toGeoElement()).getVarString());
//            	sb.append(')');
//            }
//            sb.append(" = ");
//            sb.append(g.toSymbolicString());
//        }
//        return sb.toString();
//    }

}
